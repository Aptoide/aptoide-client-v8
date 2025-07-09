package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getPlayAndEarnSmallLogo
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PlayAndEarnHomeListCard(
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
      PlayAndEarnCTAHeader(onClick = onHeaderClick)
      PlayAndEarnLargeAppCarousel(
        apps = apps,
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp)
      )
    }
  }
}

@Composable
fun PlayAndEarnCTAHeader(
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Image(
          imageVector = getPlayAndEarnSmallLogo(),
          contentDescription = null,
        )

        Text(
          text = "Play & Earn",
          style = AGTypography.Title,
          color = Palette.White
        )
      }

      Row(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "Earning Now",
          style = AGTypography.InputsM,
          color = Palette.White
        )

        Image(
          modifier = Modifier.size(18.dp),
          imageVector = getForward(Palette.White, Palette.Secondary),
          contentDescription = null,
        )
      }
    }

    Text(
      text = "Earn rewards by playing new games!",
      style = AGTypography.InputsM,
      color = Palette.White
    )
  }
}

@Preview
@Composable
private fun PlayAndEarnHomeListCardPreview() {
  PlayAndEarnHomeListCard(
    apps = List(10) { randomApp },
    onHeaderClick = {}
  )
}
