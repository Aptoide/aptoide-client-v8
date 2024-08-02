package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
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
fun TestNoNetworkError() {
  Image(
    imageVector = getNoNetworkError(Color.Black, Color.DarkGray, Color.Gray),
    contentDescription = null,
  )
}

fun getNoNetworkError(
  color: Color,
  color2: Color,
  color3: Color,
): ImageVector = ImageVector.Builder(
  name = "NoNetworkError",
  defaultWidth = 328.0.dp,
  defaultHeight = 144.0.dp,
  viewportWidth = 328.0f,
  viewportHeight = 144.0f
).apply {
  path(
    fill = SolidColor(color),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(246.0f, 16.0f)
    horizontalLineToRelative(58.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-58.0f)
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
    moveTo(265.0f, 64.0f)
    horizontalLineToRelative(23.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-23.0f)
    close()
  }
  path(
    fill = SolidColor(color3),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(41.0f, 0.0f)
    horizontalLineToRelative(23.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-23.0f)
    close()
  }
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
      moveTo(210.5f, 135.6f)
      lineTo(154.1f, 78.9f)
      curveTo(149.4f, 80.0f, 145.025f, 81.65f, 140.975f, 83.85f)
      curveTo(136.925f, 86.05f, 133.3f, 88.8f, 130.1f, 92.1f)
      lineTo(117.5f, 79.2f)
      curveTo(120.7f, 76.0f, 124.15f, 73.2f, 127.85f, 70.8f)
      curveTo(131.55f, 68.4f, 135.5f, 66.3f, 139.7f, 64.5f)
      lineTo(126.2f, 51.0f)
      curveTo(122.1f, 53.1f, 118.275f, 55.425f, 114.725f, 57.975f)
      curveTo(111.175f, 60.525f, 107.8f, 63.4f, 104.6f, 66.6f)
      lineTo(92.0f, 53.7f)
      curveTo(95.2f, 50.5f, 98.525f, 47.625f, 101.975f, 45.075f)
      curveTo(105.425f, 42.525f, 109.1f, 40.1f, 113.0f, 37.8f)
      lineTo(100.4f, 25.2f)
      lineTo(108.8f, 16.8f)
      lineTo(219.2f, 127.2f)
      lineTo(210.5f, 135.6f)
      close()
      moveTo(199.4f, 90.3f)
      lineTo(169.1f, 60.0f)
      curveTo(177.2f, 60.8f, 184.775f, 62.85f, 191.825f, 66.15f)
      curveTo(198.875f, 69.45f, 205.1f, 73.8f, 210.5f, 79.2f)
      lineTo(199.4f, 90.3f)
      close()
      moveTo(223.4f, 66.6f)
      curveTo(215.7f, 58.9f, 206.775f, 52.875f, 196.625f, 48.525f)
      curveTo(186.475f, 44.175f, 175.6f, 42.0f, 164.0f, 42.0f)
      curveTo(161.9f, 42.0f, 159.875f, 42.075f, 157.925f, 42.225f)
      curveTo(155.975f, 42.375f, 154.0f, 42.6f, 152.0f, 42.9f)
      lineTo(136.7f, 27.6f)
      curveTo(141.1f, 26.4f, 145.575f, 25.5f, 150.125f, 24.9f)
      curveTo(154.675f, 24.3f, 159.3f, 24.0f, 164.0f, 24.0f)
      curveTo(178.2f, 24.0f, 191.45f, 26.65f, 203.75f, 31.95f)
      curveTo(216.05f, 37.25f, 226.8f, 44.5f, 236.0f, 53.7f)
      lineTo(223.4f, 66.6f)
      close()
      moveTo(164.0f, 126.0f)
      lineTo(142.85f, 104.7f)
      curveTo(145.65f, 101.9f, 148.875f, 99.75f, 152.525f, 98.25f)
      curveTo(156.175f, 96.75f, 160.0f, 96.0f, 164.0f, 96.0f)
      curveTo(168.0f, 96.0f, 171.825f, 96.75f, 175.475f, 98.25f)
      curveTo(179.125f, 99.75f, 182.35f, 101.9f, 185.15f, 104.7f)
      lineTo(164.0f, 126.0f)
      close()
    }
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(64.0f, 104.0f)
      horizontalLineToRelative(16.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-16.0f)
      close()
    }
  }
}.build()
