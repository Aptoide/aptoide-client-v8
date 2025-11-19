package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.campaigns.domain.PaEBundle
import cm.aptoide.pt.campaigns.presentation.PaEBundlesUiState
import cm.aptoide.pt.campaigns.presentation.rememberPaEBundles
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsPlayAndEarn
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getPlayPauseIcon
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getThumbUpIcon
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaESectionHeader
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items.PaEDefaultAppItem
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items.PaELargeAppItem
import com.aptoide.android.aptoidegames.play_and_earn.presentation.layout.PaEHorizontalCarousel
import com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up.levelUpRoute
import com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up.rememberCurrentPaELevel
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rememberPlayAndEarnSetupRoute
import com.aptoide.android.aptoidegames.play_and_earn.rememberPlayAndEarnReady

const val playAndEarnRewardsRoute = "playAndEarnRewards"

fun playAndEarnRewardsScreen() = ScreenData.withAnalytics(
  route = playAndEarnRewardsRoute,
  screenAnalyticsName = "PlayAndEarnRewards",
) { _, navigate, _ ->

  PlayAndEarnRewardsScreen(navigate)
}

@Composable
fun PlayAndEarnRewardsScreen(
  navigate: (String) -> Unit
) {
  val analytics = rememberPaEAnalytics()
  val isPaEReady = rememberPlayAndEarnReady()
  val currentLevel = rememberCurrentPaELevel()
  val paeSetupRoute = rememberPlayAndEarnSetupRoute()

  val scrollState = rememberScrollState()
  val uiState = rememberPaEBundles()
  val trendingBundle = uiState
    .let { it as? PaEBundlesUiState.Idle }
    ?.bundles?.trending
    ?.takeIf { it.apps.isNotEmpty() }

  val keepPlayingBundle = uiState
    .let { it as? PaEBundlesUiState.Idle }
    ?.bundles?.keepPlaying
    ?.takeIf { it.apps.isNotEmpty() }

  OverrideAnalyticsPlayAndEarn(
    navigate = navigate
  ) { navigateTo ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      PaEHowItWorksSection()
      if (isPaEReady) {
        currentLevel?.let { level ->
          PaEKnowMoreCard(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            currentLevel = level + 1,
            onClick = {
              navigateTo(levelUpRoute)
              analytics.sendPaERewardsHubKnowMoreClick()
            }
          )
        }
      } else {
        PaELetsGoCard(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
          onLetsGoClick = {
            navigateTo(paeSetupRoute)
            analytics.sendPaERewardsHubLetsGoClick()
          }
        )
      }
      keepPlayingBundle?.let {
        PaEHorizontalBundleView(it, navigateTo)
      }
      trendingBundle?.let {
        PaEVerticalBundleView(it, navigateTo)
      }
    }
  }
}

@Composable
fun PaEHorizontalBundleView(
  bundle: PaEBundle,
  navigate: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    PaESectionHeader(
      icon = getPlayPauseIcon(),
      text = "Keep Playing"
    )
    PaEHorizontalCarousel(
      apps = bundle.apps,
      modifier = Modifier.fillMaxWidth()
    ) { index ->
      val app = bundle.apps[index]
      PaEDefaultAppItem(
        app = app,
        onClick = {
          navigate(
            buildAppViewRoute(app, isGamified = true).withItemPosition(index)
          )
        },
        navigate = navigate
      )
    }
  }
}

@Composable
fun PaEVerticalBundleView(
  bundle: PaEBundle,
  navigate: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(all = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    PaESectionHeader(
      icon = getThumbUpIcon(),
      text = "Everyone’s favourites"
    )
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      bundle.apps.forEachIndexed { index, app ->
        PaELargeAppItem(
          app = app,
          onClick = { navigate(buildAppViewRoute(app, isGamified = true).withItemPosition(index)) },
          navigate = navigate
        )
      }
    }
  }
}

@Preview
@Composable
private fun PlayAndEarnRewardsScreenPreview() {
  PlayAndEarnRewardsScreen(
    navigate = {}
  )
}
