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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.randomAppWithImages
import cm.aptoide.pt.feature_apps.domain.AppSource
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getThumbUpIcon
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PlayAndEarnSectionHeader
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items.PlayAndEarnLargeAppItem
import kotlin.random.Random

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
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    PlayAndEarnHowItWorksSection()
    PlayAndEarnLetsGoCard(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      onLetsGoClick = {}
    )
    PlayAndEarnFavouritesList(navigate)
  }
}

@Composable
fun PlayAndEarnFavouritesList(
  navigate: (String) -> Unit
) {
  val apps = remember { List(3) { randomAppWithImages } }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(all = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    PlayAndEarnSectionHeader(
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
      repeat(apps.size) {
        PlayAndEarnLargeAppItem(
          app = apps[it],
          progress = Random.nextDouble(0.0, 1.0).toFloat(),
          onClick = {
            navigate(
              buildAppViewRoute(AppSource.of(packageName = "com.mobile.legends", appId = null))
            )
          }
        )
      }
    }
  }
}

@Preview
@Composable
fun PlayAndEarnRewardsScreenPreview() {
  PlayAndEarnRewardsScreen(
    navigate = {}
  )
}
