package com.aptoide.android.aptoidegames.home

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.presentation.appsBySortType
import cm.aptoide.pt.feature_categories.presentation.rememberAllCategories
import com.aptoide.android.aptoidegames.analytics.presentation.InitialAnalyticsMeta
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsHomeTab
import com.aptoide.android.aptoidegames.categories.presentation.AllCategoriesView
import com.aptoide.android.aptoidegames.editorial.SeeMoreEditorialsContent
import com.aptoide.android.aptoidegames.editorial.rememberEditorialListState
import com.aptoide.android.aptoidegames.feature_apps.presentation.BONUS_SORT
import com.aptoide.android.aptoidegames.feature_apps.presentation.MoreBonusBundleView
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PlayAndEarnRewardsScreen
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.playAndEarnRewardsRoute

const val gamesRoute = "games"
const val INITIAL_HOME_TAB_PARAM = "initialHomeTab"

fun buildGamesRoute(initialTab: HomeTab? = null): String =
  if (initialTab == null) gamesRoute
  else "$gamesRoute?$INITIAL_HOME_TAB_PARAM=${initialTab.id}"

/**
 * Resolves where a "go to rewards" action should navigate: the Rewards home tab when it is
 * available, otherwise the standalone rewards screen as a fallback.
 */
@Composable
fun rememberRewardsDestination(): String {
  val (showHomeTabRow, tabs) = rememberHomeTabRowState()
  return if (showHomeTabRow && tabs.any { it is HomeTab.Rewards }) {
    buildGamesRoute(HomeTab.Rewards)
  } else {
    playAndEarnRewardsRoute
  }
}

fun gamesScreen() = ScreenData(
  route = "$gamesRoute?$INITIAL_HOME_TAB_PARAM={$INITIAL_HOME_TAB_PARAM}",
  arguments = listOf(
    navArgument(INITIAL_HOME_TAB_PARAM) {
      type = NavType.StringType
      nullable = true
    }
  ),
) { args, navigate, _ ->
  InitialAnalyticsMeta(
    screenAnalyticsName = "Home",
    navigate = navigate
  ) {
    GamesScreenContent(
      navigate = navigate,
      initialHomeTabId = args?.getString(INITIAL_HOME_TAB_PARAM)
    )
  }
}

@Composable
private fun GamesScreenContent(
  navigate: (String) -> Unit,
  initialHomeTabId: String? = null,
) {
  val (showHomeTabRow, tabs) = rememberHomeTabRowState()
  var selectedTab by rememberSaveable(key = tabs.size.toString()) { mutableIntStateOf(0) }

  LaunchedEffect(initialHomeTabId, tabs) {
    initialHomeTabId
      ?.let { id -> tabs.indexOfFirst { it.id == id } }
      ?.takeIf { it >= 0 }
      ?.let { selectedTab = it }
  }

  val homeAnalytics = rememberHomeAnalytics()
  val paeAnalytics = rememberPaEAnalytics()

  Column {
    if (showHomeTabRow) {
      HomeTabRow(
        selectedTab = selectedTab,
        tabsList = tabs.map { it.getTitle() },
        onSelectTab = {
          if (it != selectedTab) {
            homeAnalytics.sendHomeTabClick(tabs[it]::class.simpleName.toString())

            if (tabs[it] is HomeTab.Rewards) {
              paeAnalytics.sendPaERewardsHomeTabClick()
            }
          }
          selectedTab = it
        }
      )
    }
    GamesScreenTabView(
      navigate = navigate,
      currentTab = tabs[selectedTab]
    )
  }
}

@Composable
private fun GamesScreenTabView(
  navigate: (String) -> Unit,
  currentTab: HomeTab
) {
  OverrideAnalyticsHomeTab(
    navigate = navigate,
    homeTab = currentTab::class.simpleName.toString(),
  ) { navigateTo ->
    when (currentTab) {
      HomeTab.ForYou -> BundlesScreen(navigate = navigateTo)

      is HomeTab.TopCharts -> TopChartsView(sort = currentTab.sort, navigate = navigateTo)

      HomeTab.Bonus -> AppCoinsTabView(navigateTo)

      HomeTab.Editorial -> EditorialTabView(navigateTo)

      HomeTab.Categories -> CategoriesTabView(navigateTo)

      HomeTab.Rewards -> PlayAndEarnRewardsScreen(navigateTo)
    }
  }
}

@Composable
private fun AppCoinsTabView(navigate: (String) -> Unit) {
  val (uiState, reload) = appsBySortType(sort = BONUS_SORT)

  MoreBonusBundleView(
    uiState = uiState,
    navigate = navigate,
    reload = reload,
    noNetworkReload = reload
  )
}

@Composable
private fun EditorialTabView(navigate: (String) -> Unit) {
  val tag = "editorials-more"
  val (uiState, reload) = rememberEditorialListState(
    tag = tag,
    subtype = null
  )

  SeeMoreEditorialsContent(
    tag = tag,
    uiState = uiState,
    navigate = navigate,
    onError = reload
  )
}

@Composable
private fun CategoriesTabView(navigate: (String) -> Unit) {
  val (uiState, reload) = rememberAllCategories()

  AllCategoriesView(
    uiState = uiState,
    navigate = navigate,
    onError = reload
  )
}
