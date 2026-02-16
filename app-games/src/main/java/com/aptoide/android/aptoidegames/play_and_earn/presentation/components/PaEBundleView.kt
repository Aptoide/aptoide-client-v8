package com.aptoide.android.aptoidegames.play_and_earn.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.campaigns.presentation.PaEBundlesUiState
import cm.aptoide.pt.campaigns.presentation.rememberPaEBundles
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsPlayAndEarn
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items.PaECompactAppItem
import com.aptoide.android.aptoidegames.play_and_earn.presentation.layout.PaEHorizontalCarousel
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.playAndEarnRewardsRoute

@Composable
fun PaEBundleView(
  navigate: (String) -> Unit,
  spaceBy: Int = 0,
) {
  val bundle = rememberPaEBundles()
    .let { it as? PaEBundlesUiState.Idle }
    ?.bundles?.trending
    ?.takeIf { it.apps.isNotEmpty() }

  val paeAnalytics = rememberPaEAnalytics()

  if (bundle != null) {
    OverrideAnalyticsPlayAndEarn(
      navigate = navigate
    ) { navigateTo ->
      Box(
        modifier = Modifier
          .wrapContentHeight()
          .padding(bottom = spaceBy.dp)
      ) {
        Image(
          modifier = Modifier
            .matchParentSize()
            .padding(bottom = 112.dp),
          painter = painterResource(R.drawable.play_and_earn_bg_2),
          contentDescription = null,
          contentScale = ContentScale.Crop,
        )

        Column(
          modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          PaEBundleHeader(
            onClick = {
              paeAnalytics.sendPaEHomeEarnNowClick()
              navigateTo(playAndEarnRewardsRoute)
            }
          )
          PaEHorizontalCarousel(
            apps = bundle.apps,
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 16.dp)
          ) { index ->
            val app = bundle.apps[index]
            PaECompactAppItem(
              app = app,
              onClick = {
                paeAnalytics.sendPaEHomeAppClick(app.packageName)
                navigateTo(
                  buildAppViewRoute(app, isGamified = true).withItemPosition(index)
                )
              }
            )
          }
        }
      }
    }
  }
}

@Preview
@Composable
private fun PaEBundleViewPreview() {
  PaEBundleView(navigate = {})
}
