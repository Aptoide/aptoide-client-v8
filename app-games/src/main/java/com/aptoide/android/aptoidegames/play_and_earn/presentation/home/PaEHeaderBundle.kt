package com.aptoide.android.aptoidegames.play_and_earn.presentation.home

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
import cm.aptoide.pt.campaigns.domain.PaEBundle
import cm.aptoide.pt.campaigns.domain.randomPaEBundle
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaEBundleHeader
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items.PaECompactAppItem
import com.aptoide.android.aptoidegames.play_and_earn.presentation.layout.PaEHorizontalCarousel
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.playAndEarnRewardsRoute

@Composable
fun PaEHeaderBundle(
  bundle: PaEBundle,
  modifier: Modifier = Modifier,
  navigate: (String) -> Unit
) {
  Box(
    modifier = modifier.wrapContentHeight()
  ) {
    Image(
      modifier = Modifier.matchParentSize(),
      painter = painterResource(R.drawable.play_and_earn_bg_2),
      contentDescription = null,
      contentScale = ContentScale.Crop
    )

    Column(
      modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      PaEBundleHeader(onClick = { navigate(playAndEarnRewardsRoute) })
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
            navigate(
              buildAppViewRoute(app).withItemPosition(index)
            )
          }
        )
      }
    }
  }
}

@Preview
@Composable
fun PlayAndEarnBundlePreview() {
  PaEHeaderBundle(
    bundle = randomPaEBundle,
    navigate = {}
  )
}
