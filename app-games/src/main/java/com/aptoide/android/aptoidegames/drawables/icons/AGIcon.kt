package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestAGIcon() {
  Image(
    imageVector = getAGIcon(Palette.Primary, Palette.Black),
    contentDescription = null,
    modifier = Modifier
      .size(240.dp)
      .background(Palette.White)
  )
}

fun getAGIcon(color1: Color, color2: Color): ImageVector =
  ImageVector.Builder(
    name = "AGIcon",
    defaultWidth = 48.0.dp,
    defaultHeight = 48.0.dp,
    viewportWidth = 48.0f,
    viewportHeight = 48.0f
  ).apply {
    group {
      path(
        fill = SolidColor(color1),
        stroke = null,
        strokeLineWidth = 0.0f,
        strokeLineCap = Butt,
        strokeLineJoin = Miter,
        strokeLineMiter = 4.0f,
        pathFillType = NonZero
      ) {
        moveTo(0.0f, 0.0f)
        horizontalLineToRelative(72.0f)
        verticalLineToRelative(72.0f)
        horizontalLineToRelative(-72.0f)
        close()
      }
      path(
        fill = SolidColor(color2),
        stroke = null,
        strokeLineWidth = 0.0f,
        strokeLineCap = Butt,
        strokeLineJoin = Miter,
        strokeLineMiter = 4.0f,
        pathFillType = EvenOdd
      ) {
        moveTo(33.5007f, 11.333f)
        horizontalLineTo(30.334f)
        verticalLineTo(14.4997f)
        horizontalLineTo(27.1673f)
        verticalLineTo(18.2997f)
        horizontalLineTo(24.0006f)
        verticalLineTo(20.833f)
        horizontalLineTo(20.834f)
        verticalLineTo(17.6663f)
        horizontalLineTo(17.6673f)
        verticalLineTo(14.4997f)
        horizontalLineTo(14.5007f)
        verticalLineTo(11.333f)
        horizontalLineTo(11.334f)
        verticalLineTo(27.1663f)
        horizontalLineTo(14.5007f)
        verticalLineTo(30.333f)
        horizontalLineTo(17.6673f)
        verticalLineTo(33.4997f)
        horizontalLineTo(20.834f)
        verticalLineTo(36.6663f)
        horizontalLineTo(27.1673f)
        verticalLineTo(33.4997f)
        horizontalLineTo(30.334f)
        verticalLineTo(30.333f)
        horizontalLineTo(33.5007f)
        verticalLineTo(27.1663f)
        horizontalLineTo(36.6673f)
        verticalLineTo(14.4997f)
        horizontalLineTo(33.5007f)
        verticalLineTo(11.333f)
        close()
        moveTo(17.6673f, 23.9997f)
        verticalLineTo(27.1663f)
        horizontalLineTo(14.5007f)
        verticalLineTo(23.9997f)
        horizontalLineTo(17.6673f)
        close()
        moveTo(17.6673f, 23.9997f)
        horizontalLineTo(20.834f)
        verticalLineTo(20.833f)
        horizontalLineTo(17.6673f)
        verticalLineTo(23.9997f)
        close()
      }
    }
  }
    .build()
