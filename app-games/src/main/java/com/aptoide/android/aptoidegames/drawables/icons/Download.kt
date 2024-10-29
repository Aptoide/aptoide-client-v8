package com.aptoide.android.aptoidegames.drawables.icons

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
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestDownloadIcon() {
  Image(
    imageVector = getDownloadIcon(Palette.Primary),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getDownloadIcon(color: Color): ImageVector =
  ImageVector.Builder(
    name = "DownloadIcon",
    defaultWidth = 24.0.dp,
    defaultHeight = 24.0.dp,
    viewportWidth = 24.0f,
    viewportHeight = 24.0f
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
        moveTo(12.0f, 15.875f)
        lineTo(7.2125f, 11.0875f)
        lineTo(8.5125f, 9.7625f)
        lineTo(11.0625f, 12.3125f)
        verticalLineTo(4.25f)
        horizontalLineTo(12.9375f)
        verticalLineTo(12.3125f)
        lineTo(15.4875f, 9.7625f)
        lineTo(16.7875f, 11.0875f)
        lineTo(12.0f, 15.875f)
        close()
        moveTo(4.25f, 19.75f)
        verticalLineTo(14.9375f)
        horizontalLineTo(6.125f)
        verticalLineTo(17.875f)
        horizontalLineTo(17.875f)
        verticalLineTo(14.9375f)
        horizontalLineTo(19.75f)
        verticalLineTo(19.75f)
        horizontalLineTo(4.25f)
        close()
      }
    }
  }.build()
