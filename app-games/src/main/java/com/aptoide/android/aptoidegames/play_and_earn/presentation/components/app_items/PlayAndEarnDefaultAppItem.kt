package com.aptoide.android.aptoidegames.play_and_earn.presentation.components.app_items

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getSmallCoinIcon
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.AppXPText
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.ProgressIndicator
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random

@Composable
fun PlayAndEarnDefaultAppItem(
  app: App,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
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
        data = app.featureGraphic,
        contentDescription = null,
      )
    }
    ProgressIndicator(
      progress = Random.nextDouble(0.2, 0.8).toFloat(),
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

        AppXPText(20, 50)
      }

      Button(
        onClick = onClick,
        enabled = true,
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
        shape = CutCornerShape(0),
        colors = ButtonDefaults.buttonColors(
          backgroundColor = Palette.Secondary,
          disabledBackgroundColor = Palette.Grey,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp),
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = "Install",
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = AGTypography.InputsS,
            color = Palette.White
          )
          Icon(
            imageVector = getSmallCoinIcon(),
            contentDescription = null,
            tint = Color.Unspecified
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun PlayAndEarnDefaultAppItemPreview() {
  PlayAndEarnDefaultAppItem(
    app = randomApp,
    onClick = {},
  )
}
