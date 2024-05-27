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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestPaymentsGenericError() {
  Image(
    imageVector = getPaymentsGenericError(Color.Magenta, Color.Cyan),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPaymentsGenericError(
  color1: Color,
  color2: Color,
): ImageVector = ImageVector.Builder(
  name = "PaymentsGenericError",
  defaultWidth = 328.0.dp,
  defaultHeight = 96.0.dp,
  viewportWidth = 328.0f,
  viewportHeight = 96.0f
).apply {
  path(
    fill = SolidColor(color1),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(80.0f, 16.0f)
    horizontalLineToRelative(16.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-16.0f)
    close()
  }
  path(
    fill = SolidColor(color2),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(209.0f, 8.0f)
    horizontalLineToRelative(47.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-47.0f)
    close()
  }
  path(
    fill = SolidColor(color1),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(224.0f, 64.0f)
    horizontalLineToRelative(23.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-23.0f)
    close()
  }
  path(
    fill = SolidColor(color1),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(97.0f, 0.0f)
    horizontalLineToRelative(23.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-23.0f)
    close()
  }
  path(
    fill = SolidColor(color2),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(88.0f, 72.0f)
    horizontalLineToRelative(16.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-16.0f)
    close()
  }
  path(
    fill = SolidColor(color1),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(164.0f, 85.0f)
    curveTo(160.028f, 85.0f, 156.346f, 84.022f, 152.954f, 82.067f)
    curveTo(149.562f, 80.111f, 146.889f, 77.422f, 144.933f, 74.0f)
    horizontalLineTo(134.667f)
    verticalLineTo(66.667f)
    horizontalLineTo(142.367f)
    curveTo(142.183f, 65.444f, 142.076f, 64.222f, 142.046f, 63.0f)
    curveTo(142.015f, 61.778f, 142.0f, 60.556f, 142.0f, 59.333f)
    horizontalLineTo(134.667f)
    verticalLineTo(52.0f)
    horizontalLineTo(142.0f)
    curveTo(142.0f, 50.778f, 142.015f, 49.556f, 142.046f, 48.333f)
    curveTo(142.076f, 47.111f, 142.183f, 45.889f, 142.367f, 44.667f)
    horizontalLineTo(134.667f)
    verticalLineTo(37.333f)
    horizontalLineTo(144.933f)
    curveTo(145.789f, 35.928f, 146.751f, 34.614f, 147.821f, 33.392f)
    curveTo(148.89f, 32.169f, 150.128f, 31.1f, 151.533f, 30.183f)
    lineTo(145.667f, 24.133f)
    lineTo(150.8f, 19.0f)
    lineTo(158.683f, 26.883f)
    curveTo(160.394f, 26.333f, 162.136f, 26.058f, 163.908f, 26.058f)
    curveTo(165.68f, 26.058f, 167.422f, 26.333f, 169.133f, 26.883f)
    lineTo(177.2f, 19.0f)
    lineTo(182.333f, 24.133f)
    lineTo(176.283f, 30.183f)
    curveTo(177.689f, 31.1f, 178.957f, 32.154f, 180.087f, 33.346f)
    curveTo(181.218f, 34.537f, 182.211f, 35.867f, 183.067f, 37.333f)
    horizontalLineTo(193.333f)
    verticalLineTo(44.667f)
    horizontalLineTo(185.633f)
    curveTo(185.817f, 45.889f, 185.923f, 47.111f, 185.954f, 48.333f)
    curveTo(185.985f, 49.556f, 186.0f, 50.778f, 186.0f, 52.0f)
    horizontalLineTo(193.333f)
    verticalLineTo(59.333f)
    horizontalLineTo(186.0f)
    curveTo(186.0f, 60.556f, 185.985f, 61.778f, 185.954f, 63.0f)
    curveTo(185.923f, 64.222f, 185.817f, 65.444f, 185.633f, 66.667f)
    horizontalLineTo(193.333f)
    verticalLineTo(74.0f)
    horizontalLineTo(183.067f)
    curveTo(181.111f, 77.422f, 178.437f, 80.111f, 175.046f, 82.067f)
    curveTo(171.654f, 84.022f, 167.972f, 85.0f, 164.0f, 85.0f)
    close()
    moveTo(156.667f, 66.667f)
    horizontalLineTo(171.333f)
    verticalLineTo(59.333f)
    horizontalLineTo(156.667f)
    verticalLineTo(66.667f)
    close()
    moveTo(156.667f, 52.0f)
    horizontalLineTo(171.333f)
    verticalLineTo(44.667f)
    horizontalLineTo(156.667f)
    verticalLineTo(52.0f)
    close()
  }
}.build()
