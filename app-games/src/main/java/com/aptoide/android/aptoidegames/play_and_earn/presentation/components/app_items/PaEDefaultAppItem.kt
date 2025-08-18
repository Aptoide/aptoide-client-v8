package com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.campaigns.domain.PaEApp
import cm.aptoide.pt.campaigns.domain.asNormalApp
import cm.aptoide.pt.campaigns.domain.randomPaEApp
import cm.aptoide.pt.download_view.presentation.rememberDownloadState
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaEInstallProgressText
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaEInstallViewShort
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaEProgressIndicator
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.rememberDownloadGraphicFilter
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PaEDefaultAppItem(
  app: PaEApp,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val state = rememberDownloadState(app.asNormalApp())
  val colorFilter = rememberDownloadGraphicFilter(state)

  Column(
    modifier = modifier
      .clickable(onClick = onClick)
      .width(280.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(280f / 136f)
        .border(width = 2.dp, color = Palette.Yellow100)
    ) {
      AptoideFeatureGraphicImage(
        modifier = Modifier.matchParentSize(),
        data = app.graphic,
        contentDescription = null,
        colorFilter = colorFilter
      )
    }
    PaEProgressIndicator(
      progress = app.progress?.getNormalizedProgress() ?: 0f,
      modifier = Modifier.width(280.dp)
    )

    Row(
      modifier = Modifier
        .padding(top = 14.dp)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(end = 16.dp),
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

      PaEInstallViewShort(app = app)
    }
  }
}

@Preview
@Composable
private fun PaEDefaultAppItemPreview() {
  PaEDefaultAppItem(
    app = randomPaEApp,
    onClick = {},
  )
}
