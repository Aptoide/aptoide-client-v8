package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestWifiDialogIcon() {
  Image(
    getWifiDialogIcon(),
    null,
    modifier = Modifier.size(184.dp)
  )
}

fun getWifiDialogIcon(): ImageVector = ImageVector.Builder(
  name = "WifiDialogIcon",
  defaultWidth = 232.dp,
  defaultHeight = 120.dp,
  viewportWidth = 232f,
  viewportHeight = 120f
).apply {
  path(
    fill = SolidColor(Color(0xFFFFFFFF)),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.NonZero
  ) {
    moveTo(0f, 28f)
    horizontalLineTo(16f)
    verticalLineTo(36f)
    horizontalLineTo(0f)
    verticalLineTo(28f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFC8ED4F)),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.NonZero
  ) {
    moveTo(174f, 16f)
    horizontalLineTo(232f)
    verticalLineTo(32f)
    horizontalLineTo(174f)
    verticalLineTo(16f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFD2D2D2)),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.NonZero
  ) {
    moveTo(209f, 64f)
    horizontalLineTo(232f)
    verticalLineTo(80f)
    horizontalLineTo(209f)
    verticalLineTo(64f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFFFFFFF)),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.NonZero
  ) {
    moveTo(16f, 0f)
    horizontalLineTo(39f)
    verticalLineTo(16f)
    horizontalLineTo(16f)
    verticalLineTo(0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFC8ED4F)),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.NonZero
  ) {
    moveTo(32f, 104f)
    horizontalLineTo(48f)
    verticalLineTo(120f)
    horizontalLineTo(32f)
    verticalLineTo(104f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFC8ED4F)),
    fillAlpha = 1.0f,
    stroke = null,
    strokeAlpha = 1.0f,
    strokeLineWidth = 1.0f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Miter,
    strokeLineMiter = 1.0f,
    pathFillType = PathFillType.NonZero
  ) {
    moveTo(95.2833f, 80.2834f)
    lineTo(87.5833f, 72.4f)
    curveTo(91.3722f, 68.6111f, 95.7111f, 65.7084f, 100.6f, 63.6917f)
    curveTo(105.489f, 61.675f, 110.622f, 60.6667f, 116f, 60.6667f)
    curveTo(121.378f, 60.6667f, 126.526f, 61.6903f, 131.446f, 63.7375f)
    curveTo(136.365f, 65.7847f, 140.689f, 68.7334f, 144.417f, 72.5834f)
    lineTo(136.717f, 80.2834f)
    curveTo(133.967f, 77.5334f, 130.804f, 75.4097f, 127.229f, 73.9125f)
    curveTo(123.654f, 72.4153f, 119.911f, 71.6667f, 116f, 71.6667f)
    curveTo(112.089f, 71.6667f, 108.346f, 72.4153f, 104.771f, 73.9125f)
    curveTo(101.196f, 75.4097f, 98.0333f, 77.5334f, 95.2833f, 80.2834f)
    close()
    moveTo(79.7f, 64.7f)
    lineTo(72f, 57f)
    curveTo(77.8056f, 51.0722f, 84.5125f, 46.5347f, 92.1208f, 43.3875f)
    curveTo(99.7292f, 40.2403f, 107.689f, 38.6667f, 116f, 38.6667f)
    curveTo(124.311f, 38.6667f, 132.271f, 40.2403f, 139.879f, 43.3875f)
    curveTo(147.488f, 46.5347f, 154.194f, 51.0722f, 160f, 57f)
    lineTo(152.3f, 64.7f)
    curveTo(147.472f, 59.8722f, 141.926f, 56.1597f, 135.663f, 53.5625f)
    curveTo(129.399f, 50.9653f, 122.844f, 49.6667f, 116f, 49.6667f)
    curveTo(109.156f, 49.6667f, 102.601f, 50.9653f, 96.3375f, 53.5625f)
    curveTo(90.0736f, 56.1597f, 84.5278f, 59.8722f, 79.7f, 64.7f)
    close()
    moveTo(116f, 101f)
    lineTo(103.075f, 87.9834f)
    curveTo(104.786f, 86.2722f, 106.757f, 84.9584f, 108.988f, 84.0417f)
    curveTo(111.218f, 83.125f, 113.556f, 82.6667f, 116f, 82.6667f)
    curveTo(118.444f, 82.6667f, 120.782f, 83.125f, 123.013f, 84.0417f)
    curveTo(125.243f, 84.9584f, 127.214f, 86.2722f, 128.925f, 87.9834f)
    lineTo(116f, 101f)
    close()
  }
}.build()
