package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

fun getGoogleIcon(): ImageVector = ImageVector.Builder(
  name = "GoogleIcon", defaultWidth = 21.0.dp, defaultHeight = 20.0.dp,
  viewportWidth = 21.0f, viewportHeight = 20.0f
).apply {
  group {
    path(
      fill = SolidColor(Color(0xFF4285F4)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(20.35f, 10.227f)
      curveTo(20.35f, 9.518f, 20.286f, 8.836f, 20.168f, 8.182f)
      horizontalLineTo(10.75f)
      verticalLineTo(12.05f)
      horizontalLineTo(16.132f)
      curveTo(15.9f, 13.3f, 15.196f, 14.359f, 14.136f, 15.068f)
      verticalLineTo(17.577f)
      horizontalLineTo(17.368f)
      curveTo(19.259f, 15.836f, 20.35f, 13.273f, 20.35f, 10.227f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFF34A853)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(10.751f, 20.0f)
      curveTo(13.451f, 20.0f, 15.715f, 19.104f, 17.369f, 17.577f)
      lineTo(14.137f, 15.068f)
      curveTo(13.242f, 15.668f, 12.096f, 16.023f, 10.751f, 16.023f)
      curveTo(8.146f, 16.023f, 5.942f, 14.264f, 5.155f, 11.9f)
      horizontalLineTo(1.814f)
      verticalLineTo(14.491f)
      curveTo(3.46f, 17.759f, 6.842f, 20.0f, 10.751f, 20.0f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFBBC04)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(5.155f, 11.9f)
      curveTo(4.955f, 11.3f, 4.841f, 10.659f, 4.841f, 10.0f)
      curveTo(4.841f, 9.341f, 4.955f, 8.7f, 5.155f, 8.1f)
      verticalLineTo(5.509f)
      horizontalLineTo(1.814f)
      curveTo(1.136f, 6.859f, 0.75f, 8.387f, 0.75f, 10.0f)
      curveTo(0.75f, 11.614f, 1.136f, 13.141f, 1.814f, 14.491f)
      lineTo(5.155f, 11.9f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFE94235)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(10.751f, 3.977f)
      curveTo(12.219f, 3.977f, 13.537f, 4.482f, 14.574f, 5.473f)
      lineTo(17.442f, 2.605f)
      curveTo(15.71f, 0.991f, 13.446f, 0.0f, 10.751f, 0.0f)
      curveTo(6.842f, 0.0f, 3.46f, 2.241f, 1.814f, 5.509f)
      lineTo(5.155f, 8.1f)
      curveTo(5.942f, 5.736f, 8.146f, 3.977f, 10.751f, 3.977f)
      close()
    }
  }
}.build()
