package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestBonusIconRight() {
  Image(
    imageVector = getBonusIconRight(Color.Green, Color.Black, Color.Gray),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getBonusIconRight(
  iconColor: Color,
  outlineColor: Color,
  backgroundColor: Color,
): ImageVector = ImageVector.Builder(
  name = "BonusIconRight",
  defaultWidth = 32.dp,
  defaultHeight = 32.dp,
  viewportWidth = 32f,
  viewportHeight = 32f,
).apply {
  path(
    pathFillType = PathFillType.EvenOdd,
    fill = SolidColor(backgroundColor),
  ) {
    moveTo(0f, 26.6667f)
    verticalLineTo(10.6667f)
    horizontalLineTo(4f)
    verticalLineTo(0f)
    horizontalLineTo(32f)
    verticalLineTo(26.6667f)
    horizontalLineTo(26.6667f)
    verticalLineTo(32f)
    horizontalLineTo(10.6667f)
    verticalLineTo(26.6667f)
    horizontalLineTo(0f)
    close()
  }
  path(
    fill = SolidColor(iconColor),
  ) {
    moveTo(8.88874f, 15.1115f)
    verticalLineTo(24.8893f)
    horizontalLineTo(23.111f)
    verticalLineTo(15.1115f)
    horizontalLineTo(24.8887f)
    verticalLineTo(9.77821f)
    horizontalLineTo(20.2665f)
    curveTo(20.3258f, 9.63006f, 20.3702f, 9.48562f, 20.3999f, 9.34488f)
    curveTo(20.4295f, 9.20414f, 20.4443f, 9.05229f, 20.4443f, 8.88932f)
    curveTo(20.4443f, 8.14858f, 20.185f, 7.51895f, 19.6665f, 7.00043f)
    curveTo(19.148f, 6.48192f, 18.5184f, 6.22266f, 17.7776f, 6.22266f)
    curveTo(17.4369f, 6.22266f, 17.1184f, 6.28192f, 16.8221f, 6.40043f)
    curveTo(16.5258f, 6.51895f, 16.2517f, 6.69673f, 15.9999f, 6.93377f)
    curveTo(15.748f, 6.71155f, 15.4739f, 6.53747f, 15.1776f, 6.41155f)
    curveTo(14.8813f, 6.28562f, 14.5628f, 6.22266f, 14.2221f, 6.22266f)
    curveTo(13.4813f, 6.22266f, 12.8517f, 6.48192f, 12.3332f, 7.00043f)
    curveTo(11.8147f, 7.51895f, 11.5554f, 8.14858f, 11.5554f, 8.88932f)
    curveTo(11.5554f, 9.05229f, 11.5665f, 9.20784f, 11.5887f, 9.35599f)
    curveTo(11.611f, 9.50414f, 11.6591f, 9.64488f, 11.7332f, 9.77821f)
    horizontalLineTo(7.11096f)
    verticalLineTo(15.1115f)
    horizontalLineTo(8.88874f)
    close()
  }
  path(
    fill = SolidColor(outlineColor),
  ) {
    moveTo(8.88874f, 24.8893f)
    verticalLineTo(15.1115f)
    horizontalLineTo(7.11096f)
    verticalLineTo(9.77821f)
    horizontalLineTo(11.7332f)
    curveTo(11.6591f, 9.64488f, 11.611f, 9.50414f, 11.5887f, 9.35599f)
    curveTo(11.5665f, 9.20784f, 11.5554f, 9.05229f, 11.5554f, 8.88932f)
    curveTo(11.5554f, 8.14858f, 11.8147f, 7.51895f, 12.3332f, 7.00043f)
    curveTo(12.8517f, 6.48192f, 13.4813f, 6.22266f, 14.2221f, 6.22266f)
    curveTo(14.5628f, 6.22266f, 14.8813f, 6.28562f, 15.1776f, 6.41155f)
    curveTo(15.4739f, 6.53747f, 15.748f, 6.71155f, 15.9999f, 6.93377f)
    curveTo(16.2517f, 6.69673f, 16.5258f, 6.51895f, 16.8221f, 6.40043f)
    curveTo(17.1184f, 6.28192f, 17.4369f, 6.22266f, 17.7776f, 6.22266f)
    curveTo(18.5184f, 6.22266f, 19.148f, 6.48192f, 19.6665f, 7.00043f)
    curveTo(20.185f, 7.51895f, 20.4443f, 8.14858f, 20.4443f, 8.88932f)
    curveTo(20.4443f, 9.05229f, 20.4295f, 9.20414f, 20.3999f, 9.34488f)
    curveTo(20.3702f, 9.48562f, 20.3258f, 9.63006f, 20.2665f, 9.77821f)
    horizontalLineTo(24.8887f)
    verticalLineTo(15.1115f)
    horizontalLineTo(23.111f)
    verticalLineTo(24.8893f)
    horizontalLineTo(8.88874f)
    close()
    moveTo(17.7776f, 8.00043f)
    curveTo(17.5258f, 8.00043f, 17.3147f, 8.08562f, 17.1443f, 8.25599f)
    curveTo(16.9739f, 8.42636f, 16.8887f, 8.63747f, 16.8887f, 8.88932f)
    curveTo(16.8887f, 9.14117f, 16.9739f, 9.35229f, 17.1443f, 9.52266f)
    curveTo(17.3147f, 9.69303f, 17.5258f, 9.77821f, 17.7776f, 9.77821f)
    curveTo(18.0295f, 9.77821f, 18.2406f, 9.69303f, 18.411f, 9.52266f)
    curveTo(18.5813f, 9.35229f, 18.6665f, 9.14117f, 18.6665f, 8.88932f)
    curveTo(18.6665f, 8.63747f, 18.5813f, 8.42636f, 18.411f, 8.25599f)
    curveTo(18.2406f, 8.08562f, 18.0295f, 8.00043f, 17.7776f, 8.00043f)
    close()
    moveTo(13.3332f, 8.88932f)
    curveTo(13.3332f, 9.14117f, 13.4184f, 9.35229f, 13.5887f, 9.52266f)
    curveTo(13.7591f, 9.69303f, 13.9702f, 9.77821f, 14.2221f, 9.77821f)
    curveTo(14.4739f, 9.77821f, 14.685f, 9.69303f, 14.8554f, 9.52266f)
    curveTo(15.0258f, 9.35229f, 15.111f, 9.14117f, 15.111f, 8.88932f)
    curveTo(15.111f, 8.63747f, 15.0258f, 8.42636f, 14.8554f, 8.25599f)
    curveTo(14.685f, 8.08562f, 14.4739f, 8.00043f, 14.2221f, 8.00043f)
    curveTo(13.9702f, 8.00043f, 13.7591f, 8.08562f, 13.5887f, 8.25599f)
    curveTo(13.4184f, 8.42636f, 13.3332f, 8.63747f, 13.3332f, 8.88932f)
    close()
    moveTo(8.88874f, 11.556f)
    verticalLineTo(13.3338f)
    horizontalLineTo(15.111f)
    verticalLineTo(11.556f)
    horizontalLineTo(8.88874f)
    close()
    moveTo(15.111f, 23.1115f)
    verticalLineTo(15.1115f)
    horizontalLineTo(10.6665f)
    verticalLineTo(23.1115f)
    horizontalLineTo(15.111f)
    close()
    moveTo(16.8887f, 23.1115f)
    horizontalLineTo(21.3332f)
    verticalLineTo(15.1115f)
    horizontalLineTo(16.8887f)
    verticalLineTo(23.1115f)
    close()
    moveTo(23.111f, 13.3338f)
    verticalLineTo(11.556f)
    horizontalLineTo(16.8887f)
    verticalLineTo(13.3338f)
    horizontalLineTo(23.111f)
    close()
  }
}.build()
