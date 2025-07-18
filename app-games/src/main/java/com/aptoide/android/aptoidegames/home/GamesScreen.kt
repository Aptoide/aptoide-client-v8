package com.aptoide.android.aptoidegames.home

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_categories.presentation.rememberAllCategories
import cm.aptoide.pt.feature_editorial.presentation.rememberEditorialListState
import com.aptoide.android.aptoidegames.analytics.presentation.InitialAnalyticsMeta
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsHomeTab
import com.aptoide.android.aptoidegames.categories.presentation.AllCategoriesView
import com.aptoide.android.aptoidegames.editorial.SeeMoreEditorialsContent
import com.aptoide.android.aptoidegames.feature_apps.presentation.MoreBonusBundleView
import com.aptoide.android.aptoidegames.feature_apps.presentation.rememberBonusBundle
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PlayAndEarnRewardsScreen

const val gamesRoute = "games"

fun gamesScreen() = ScreenData(
  route = gamesRoute,
) { _, navigate, _ ->
  InitialAnalyticsMeta(
    screenAnalyticsName = "Home",
    navigate = navigate
  ) {
    GamesScreenContent(navigate = navigate)
  }
}

@Composable
private fun GamesScreenContent(
  navigate: (String) -> Unit
) {
  val (showHomeTabRow, tabs) = rememberHomeTabRowState()
  var selectedTab by rememberSaveable(key = tabs.size.toString()) { mutableIntStateOf(0) }

  val homeAnalytics = rememberHomeAnalytics()

  Column {
    if (showHomeTabRow) {
      HomeTabRow(
        selectedTab = selectedTab,
        tabsList = tabs.map { it.getTitle() },
        onSelectTab = {
          if (it != selectedTab) {
            homeAnalytics.sendHomeTabClick(tabs[it]::class.simpleName.toString())
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
  val (_, bonusBundleTag) = rememberBonusBundle()
  val (uiState, reload) = rememberAppsByTag("$bonusBundleTag-more")

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
