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
fun TestPaymentsNoNetworkError() {
  Image(
    imageVector = getPaymentsNoNetworkError(Color.Magenta, Color.Cyan),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPaymentsNoNetworkError(
  color1: Color,
  color2: Color,
): ImageVector = ImageVector.Builder(
  name = "PaymentsNoNetworkError",
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
    moveTo(192.417f, 90.867f)
    lineTo(157.95f, 56.217f)
    curveTo(155.078f, 56.889f, 152.404f, 57.897f, 149.929f, 59.242f)
    curveTo(147.454f, 60.586f, 145.239f, 62.267f, 143.283f, 64.283f)
    lineTo(135.583f, 56.4f)
    curveTo(137.539f, 54.444f, 139.647f, 52.733f, 141.908f, 51.267f)
    curveTo(144.169f, 49.8f, 146.583f, 48.517f, 149.15f, 47.417f)
    lineTo(140.9f, 39.167f)
    curveTo(138.394f, 40.45f, 136.057f, 41.871f, 133.888f, 43.429f)
    curveTo(131.718f, 44.988f, 129.656f, 46.744f, 127.7f, 48.7f)
    lineTo(120.0f, 40.817f)
    curveTo(121.956f, 38.861f, 123.988f, 37.104f, 126.096f, 35.546f)
    curveTo(128.204f, 33.988f, 130.45f, 32.506f, 132.833f, 31.1f)
    lineTo(125.133f, 23.4f)
    lineTo(130.267f, 18.267f)
    lineTo(197.733f, 85.733f)
    lineTo(192.417f, 90.867f)
    close()
    moveTo(185.633f, 63.183f)
    lineTo(167.117f, 44.667f)
    curveTo(172.067f, 45.156f, 176.696f, 46.408f, 181.004f, 48.425f)
    curveTo(185.313f, 50.442f, 189.117f, 53.1f, 192.417f, 56.4f)
    lineTo(185.633f, 63.183f)
    close()
    moveTo(200.3f, 48.7f)
    curveTo(195.594f, 43.994f, 190.14f, 40.313f, 183.938f, 37.654f)
    curveTo(177.735f, 34.996f, 171.089f, 33.667f, 164.0f, 33.667f)
    curveTo(162.717f, 33.667f, 161.479f, 33.713f, 160.288f, 33.804f)
    curveTo(159.096f, 33.896f, 157.889f, 34.033f, 156.667f, 34.217f)
    lineTo(147.317f, 24.867f)
    curveTo(150.006f, 24.133f, 152.74f, 23.583f, 155.521f, 23.217f)
    curveTo(158.301f, 22.85f, 161.128f, 22.667f, 164.0f, 22.667f)
    curveTo(172.678f, 22.667f, 180.775f, 24.286f, 188.292f, 27.525f)
    curveTo(195.808f, 30.764f, 202.378f, 35.194f, 208.0f, 40.817f)
    lineTo(200.3f, 48.7f)
    close()
    moveTo(164.0f, 85.0f)
    lineTo(151.075f, 71.983f)
    curveTo(152.786f, 70.272f, 154.757f, 68.958f, 156.988f, 68.042f)
    curveTo(159.218f, 67.125f, 161.556f, 66.667f, 164.0f, 66.667f)
    curveTo(166.444f, 66.667f, 168.782f, 67.125f, 171.013f, 68.042f)
    curveTo(173.243f, 68.958f, 175.214f, 70.272f, 176.925f, 71.983f)
    lineTo(164.0f, 85.0f)
    close()
  }
}.build()
