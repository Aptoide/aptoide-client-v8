package com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.AppIconImage
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.AppXPText
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.ProgressIndicator
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random

@Composable
fun PlayAndEarnCompactAppItem(
  app: App,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.clickable(onClick = onClick)
  ) {
    Box(
      modifier = Modifier
        .size(width = 280.dp, 176.dp)
        .border(width = 2.dp, color = Palette.Yellow100)
    ) {
      AptoideFeatureGraphicImage(
        modifier = Modifier.matchParentSize(),
        data = app.featureGraphic,
        contentDescription = null,
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(all = 16.dp)
          .align(Alignment.BottomStart),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AppIconImage(
          modifier = Modifier.size(40.dp),
          data = app.icon,
          contentDescription = null,
        )

        Column(
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = app.name,
            style = AGTypography.DescriptionGames,
            color = Palette.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          AppXPText(20, 50)
        }
      }
    }
    ProgressIndicator(
      progress = Random.nextDouble(0.2, 0.8).toFloat(),
      modifier = Modifier.width(280.dp)
    )
  }
}

@Preview
@Composable
private fun PlayAndEarnLargeAppItemPreview() {
  PlayAndEarnCompactAppItem(randomApp, onClick = {})
}
