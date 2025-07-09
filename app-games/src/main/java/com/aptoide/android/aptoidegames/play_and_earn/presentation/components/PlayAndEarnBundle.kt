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
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items.PlayAndEarnCompactAppItem
import com.aptoide.android.aptoidegames.play_and_earn.presentation.layout.PlayAndEarnHorizontalCarousel

@Composable
fun PlayAndEarnBundle(
  apps: List<App>,
  onHeaderClick: () -> Unit
) {
  Box(
    modifier = Modifier.wrapContentHeight()
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
      PlayAndEarnBundleHeader(onClick = onHeaderClick)
      PlayAndEarnHorizontalCarousel(
        apps = apps,
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp)
      ) { index ->
        PlayAndEarnCompactAppItem(app = apps[index], onClick = {})
      }
    }
  }
}

@Preview
@Composable
private fun PlayAndEarnHomeListCardPreview() {
  PlayAndEarnBundle(
    apps = List(10) { randomApp },
    onHeaderClick = {}
  )
}
