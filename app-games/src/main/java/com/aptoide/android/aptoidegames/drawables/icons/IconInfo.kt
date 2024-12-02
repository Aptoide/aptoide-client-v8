package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestArrowInfo() {
  Image(
    imageVector = getInfo(Palette.GreyLight),
    contentDescription = null,
    modifier = Modifier
      .size(240.dp)
      .background(Palette.White)
  )
}

fun getInfo(color: Color): ImageVector =
  ImageVector.Builder(
    name = "Info",
    defaultWidth = 16.0.dp,
    defaultHeight = 16.0.dp,
    viewportWidth = 16.0f,
    viewportHeight = 16.0f
  ).apply {
    group {
      path(
        fill = SolidColor(color),
        stroke = null,
        strokeLineWidth = 0.0f,
        strokeLineCap = Butt,
        strokeLineJoin = Miter,
        strokeLineMiter = 4.0f,
        pathFillType = NonZero
      ) {
        moveTo(7.333f, 4.6668f)
        horizontalLineTo(8.6663f)
        verticalLineTo(6.0002f)
        horizontalLineTo(7.333f)
        verticalLineTo(4.6668f)
        close()
        moveTo(7.333f, 7.3335f)
        horizontalLineTo(8.6663f)
        verticalLineTo(11.3335f)
        horizontalLineTo(7.333f)
        verticalLineTo(7.3335f)
        close()
        moveTo(7.9997f, 1.3335f)
        curveTo(4.3197f, 1.3335f, 1.333f, 4.3202f, 1.333f, 8.0002f)
        curveTo(1.333f, 11.6802f, 4.3197f, 14.6668f, 7.9997f, 14.6668f)
        curveTo(11.6797f, 14.6668f, 14.6663f, 11.6802f, 14.6663f, 8.0002f)
        curveTo(14.6663f, 4.3202f, 11.6797f, 1.3335f, 7.9997f, 1.3335f)
        close()
        moveTo(7.9997f, 13.3335f)
        curveTo(5.0597f, 13.3335f, 2.6663f, 10.9402f, 2.6663f, 8.0002f)
        curveTo(2.6663f, 5.0602f, 5.0597f, 2.6668f, 7.9997f, 2.6668f)
        curveTo(10.9397f, 2.6668f, 13.333f, 5.0602f, 13.333f, 8.0002f)
        curveTo(13.333f, 10.9402f, 10.9397f, 13.3335f, 7.9997f, 13.3335f)
        close()
      }
    }
  }
    .build()
