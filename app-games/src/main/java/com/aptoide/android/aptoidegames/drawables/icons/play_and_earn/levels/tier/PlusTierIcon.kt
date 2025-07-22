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
import com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up.LevelProperties
import kotlin.random.Random
import kotlin.random.nextInt

@Preview
@Composable
private fun TestPlusTierIcon() {
  Image(
    imageVector = getPlusTierIcon(LevelProperties.fromLevel(Random.nextInt(0..9)).mainColor),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPlusTierIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "PlusTierIcon",
  defaultWidth = 8.0.dp,
  defaultHeight = 8.0.dp,
  viewportWidth = 8.0f,
  viewportHeight = 8.0f
).apply {
  path(
    fill = SolidColor(color), stroke = null, strokeLineWidth = 0.0f,
    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(3.342f, 0.3768f)
    lineTo(2.2478f, 2.2459f)
    lineTo(0.3787f, 3.3401f)
    curveTo(-0.1236f, 3.6339f, -0.1236f, 4.3661f, 0.3787f, 4.6599f)
    lineTo(2.2478f, 5.7541f)
    lineTo(3.342f, 7.6232f)
    curveTo(3.6358f, 8.1256f, 4.3681f, 8.1256f, 4.6619f, 7.6232f)
    lineTo(5.7561f, 5.7541f)
    lineTo(7.6252f, 4.6599f)
    curveTo(8.1276f, 4.3661f, 8.1276f, 3.6339f, 7.6252f, 3.3401f)
    lineTo(5.7561f, 2.2459f)
    lineTo(4.6619f, 0.3768f)
    curveTo(4.3681f, -0.1256f, 3.6358f, -0.1256f, 3.342f, 0.3768f)
    close()
  }
}.build()
