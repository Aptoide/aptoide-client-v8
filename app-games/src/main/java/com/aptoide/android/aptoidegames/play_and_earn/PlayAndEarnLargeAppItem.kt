package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.AppIconImage
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.getSmallCoinIcon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random

@Composable
fun PlayAndEarnLargeAppItem(
  app: App,
  onClick: () -> Unit,
) {
  Column(
    modifier = Modifier.clickable(onClick = onClick)
  ) {
    Box(
      modifier = Modifier
        .size(width = 280.dp, 176.dp)
        .border(width = 2.dp, color = Palette.Yellow)
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

@Composable
fun AppXPText(currentXp: Int, totalXp: Int) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Image(
      imageVector = getSmallCoinIcon(),
      contentDescription = null,
    )

    Text(
      text = getAppXPAnnotatedString(currentXp, totalXp),
      style = AGTypography.InputsS,
      color = Palette.White
    )
  }
}

@Composable
fun ProgressIndicator(modifier: Modifier = Modifier, progress: Float) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(8.dp)
      .background(Palette.Grey)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(progress)
        .fillMaxHeight()
        .background(color = Color.White)
        .background(
          brush = Brush.horizontalGradient(
            *arrayOf(
              0.8f to Color(0xFFFFC93E),
              1f to Color(0xFFFFC93E).copy(alpha = 0.2f)
            )
          )
        )
    )
  }
}

fun getAppXPAnnotatedString(currentXp: Int, totalXp: Int): AnnotatedString {
  return buildAnnotatedString {
    withStyle(style = SpanStyle(color = Palette.Yellow)) {
      append(currentXp.toString())
    }
    append("/${totalXp.toString()} XP")
  }
}

@Preview
@Composable
fun PlayAndEarnLargeAppItemPreview() {
  PlayAndEarnLargeAppItem(randomApp, onClick = {})
}
