package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.tier

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random
import kotlin.random.nextInt

@Preview
@Composable
private fun TestTierCoinIcon() {
  Image(
    imageVector = getTierCoinIcon(Random.nextInt(0..9)),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getTierCoinIcon(level: Int): ImageVector = ImageVector.Builder(
  name = "TierCoin",
  defaultWidth = 11.0.dp,
  defaultHeight = 12.0.dp,
  viewportWidth = 11.0f,
  viewportHeight = 12.0f
).apply {
  val colors = level.getCoinColors()

  path(
    fill = SolidColor(colors[0]), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.334f, 3.333f)
    lineTo(5.667f, 0.0f)
    lineTo(11.001f, 3.333f)
    verticalLineTo(8.667f)
    lineTo(5.667f, 12.0f)
    lineTo(0.334f, 8.667f)
    verticalLineTo(3.333f)
    close()
  }
  path(
    fill = SolidColor(colors[1]), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(5.667f, 2.0f)
    verticalLineTo(0.0f)
    lineTo(0.334f, 3.333f)
    verticalLineTo(8.667f)
    lineTo(5.667f, 12.0f)
    verticalLineTo(10.0f)
    lineTo(2.334f, 7.667f)
    verticalLineTo(4.333f)
    lineTo(5.667f, 2.0f)
    close()
  }
  path(
    fill = SolidColor(colors[2]), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(5.668f, 2.0f)
    verticalLineTo(0.0f)
    lineTo(11.001f, 3.333f)
    verticalLineTo(8.667f)
    lineTo(5.668f, 12.0f)
    verticalLineTo(10.0f)
    lineTo(9.001f, 7.667f)
    verticalLineTo(4.333f)
    lineTo(5.668f, 2.0f)
    close()
  }
}.build()

private fun Int.getCoinColors(): List<Color> = when (this) {
  0, 1 -> listOf(Palette.Orange200, Palette.Yellow100, Palette.Orange150)
  2, 3 -> listOf(Palette.Blue100, Palette.White, Palette.Blue200)
  4, 5 -> listOf(Palette.Yellow200, Palette.Yellow50, Palette.Yellow100)
  6, 7 -> listOf(Palette.Blue150, Palette.Blue50, Palette.Blue250)
  8, 9 -> listOf(Palette.Yellow150, Palette.Yellow100, Palette.Yellow200)
  else -> listOf(Color.Transparent, Color.Transparent, Color.Transparent)
}