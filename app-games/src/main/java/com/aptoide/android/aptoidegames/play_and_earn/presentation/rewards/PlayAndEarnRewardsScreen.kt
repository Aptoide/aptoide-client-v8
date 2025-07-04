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
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getThumbUpIcon
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaESectionHeader
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items.PaELargeAppItem

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
  val scrollState = rememberScrollState()
  val trendingBundle = rememberPaEBundles()
    .let { it as? PaEBundlesUiState.Idle }
    ?.bundles?.trending
    ?.takeIf { it.apps.isNotEmpty() }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    PaEHowItWorksSection()
    PaELetsGoCard(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      onLetsGoClick = {}
    )
    trendingBundle?.let {
      PaEVerticalBundleView(it, navigate)
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
          onClick = {
            navigate(buildAppViewRoute(app).withItemPosition(index))
          }
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
