package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn

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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun TestPaECoins() {
  Image(
    imageVector = getPaECoins(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPaECoins(): ImageVector = ImageVector.Builder(
  name = "PlayAndEarnCoins",
  defaultWidth = 57.0.dp,
  defaultHeight = 96.0.dp,
  viewportWidth = 57.0f,
  viewportHeight = 96.0f
).apply {
  path(
    fill = SolidColor(Color(0xFFFFEA04)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(21.5f, 10.714f)
    lineTo(38.642f, 0.0f)
    lineTo(55.784f, 10.714f)
    verticalLineTo(27.856f)
    lineTo(38.642f, 38.57f)
    lineTo(21.5f, 27.856f)
    verticalLineTo(10.714f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFFFC93E)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(38.642f, 6.428f)
    verticalLineTo(0.0f)
    lineTo(21.5f, 10.714f)
    verticalLineTo(27.856f)
    lineTo(38.642f, 38.57f)
    verticalLineTo(32.142f)
    lineTo(27.928f, 24.642f)
    verticalLineTo(13.928f)
    lineTo(38.642f, 6.428f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFD6A422)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(38.648f, 6.428f)
    verticalLineTo(0.0f)
    lineTo(55.791f, 10.714f)
    verticalLineTo(27.856f)
    lineTo(38.648f, 38.57f)
    verticalLineTo(32.142f)
    lineTo(49.362f, 24.642f)
    verticalLineTo(13.928f)
    lineTo(38.648f, 6.428f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFFFEA04)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 42.429f)
    lineTo(17.142f, 31.715f)
    lineTo(34.284f, 42.429f)
    verticalLineTo(59.571f)
    lineTo(17.142f, 70.285f)
    lineTo(0.0f, 59.571f)
    verticalLineTo(42.429f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFFFC93E)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(17.142f, 38.143f)
    verticalLineTo(31.715f)
    lineTo(0.0f, 42.429f)
    verticalLineTo(59.571f)
    lineTo(17.142f, 70.285f)
    verticalLineTo(63.856f)
    lineTo(6.428f, 56.357f)
    verticalLineTo(45.643f)
    lineTo(17.142f, 38.143f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFD6A422)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(17.141f, 38.143f)
    verticalLineTo(31.715f)
    lineTo(34.283f, 42.429f)
    verticalLineTo(59.571f)
    lineTo(17.141f, 70.285f)
    verticalLineTo(63.856f)
    lineTo(27.854f, 56.357f)
    verticalLineTo(45.643f)
    lineTo(17.141f, 38.143f)
    close()
  }
  group {
    path(
      fill = SolidColor(Color(0xFFFFEA04)),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(22.359f, 74.144f)
      lineTo(39.502f, 63.43f)
      lineTo(56.644f, 74.144f)
      verticalLineTo(91.286f)
      lineTo(39.502f, 102.0f)
      lineTo(22.359f, 91.286f)
      verticalLineTo(74.144f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFFC93E)),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(39.502f, 69.858f)
      verticalLineTo(63.43f)
      lineTo(22.359f, 74.144f)
      verticalLineTo(91.286f)
      lineTo(39.502f, 102.0f)
      verticalLineTo(95.571f)
      lineTo(28.788f, 88.072f)
      verticalLineTo(77.358f)
      lineTo(39.502f, 69.858f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFD6A422)),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(39.5f, 69.858f)
      verticalLineTo(63.43f)
      lineTo(56.642f, 74.144f)
      verticalLineTo(91.286f)
      lineTo(39.5f, 102.0f)
      verticalLineTo(95.571f)
      lineTo(50.214f, 88.072f)
      verticalLineTo(77.358f)
      lineTo(39.5f, 69.858f)
      close()
    }
  }
}.build()
