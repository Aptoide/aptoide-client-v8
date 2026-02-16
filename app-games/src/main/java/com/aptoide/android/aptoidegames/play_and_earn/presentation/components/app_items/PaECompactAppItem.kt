package com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.campaigns.domain.PaEApp
import cm.aptoide.pt.campaigns.domain.asNormalApp
import cm.aptoide.pt.campaigns.domain.randomPaEApp
import cm.aptoide.pt.download_view.presentation.rememberDownloadState
import com.aptoide.android.aptoidegames.AppIconImage
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaEInstallProgressText
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaEProgressIndicator
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.rememberDownloadGraphicFilter
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PaECompactAppItem(
  app: PaEApp,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val state = rememberDownloadState(app.asNormalApp())
  val colorFilter = rememberDownloadGraphicFilter(state)

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
        data = app.graphic,
        contentDescription = null,
        colorFilter = colorFilter
      )
      Box(
        modifier = Modifier
          .matchParentSize()
          .background(
            brush = Brush.verticalGradient(
              colorStops = arrayOf(
                0.0f to Color.Black.copy(alpha = 0f),
                1f to Color.Black.copy()
              ),
            ),
            alpha = 0.5f
          )
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

          PaEInstallProgressText(app = app)
        }
      }
    }
    PaEProgressIndicator(
      progress = app.progress?.getNormalizedProgress() ?: 0f,
      modifier = Modifier.width(280.dp)
    )
  }
}

@Preview
@Composable
private fun PaECompactAppItemPreview() {
  PaECompactAppItem(randomPaEApp, onClick = {})
}
