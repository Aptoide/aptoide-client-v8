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
private fun TestPlayPauseIcon() {
  Image(
    imageVector = getPlayPauseIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPlayPauseIcon(): ImageVector = ImageVector.Builder(
  name = "PlayPauseIcon", defaultWidth = 24.0.dp, defaultHeight =
    24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
).apply {
  group {
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(8.0f, 16.0f)
      horizontalLineTo(10.0f)
      verticalLineTo(8.0f)
      horizontalLineTo(8.0f)
      verticalLineTo(16.0f)
      close()
      moveTo(12.0f, 16.0f)
      lineTo(18.0f, 12.0f)
      lineTo(12.0f, 8.0f)
      verticalLineTo(16.0f)
      close()
      moveTo(12.0f, 22.0f)
      curveTo(10.617f, 22.0f, 9.317f, 21.737f, 8.1f, 21.212f)
      curveTo(6.883f, 20.688f, 5.825f, 19.975f, 4.925f, 19.075f)
      curveTo(4.025f, 18.175f, 3.313f, 17.117f, 2.787f, 15.9f)
      curveTo(2.263f, 14.683f, 2.0f, 13.383f, 2.0f, 12.0f)
      curveTo(2.0f, 10.617f, 2.263f, 9.317f, 2.787f, 8.1f)
      curveTo(3.313f, 6.883f, 4.025f, 5.825f, 4.925f, 4.925f)
      curveTo(5.825f, 4.025f, 6.883f, 3.313f, 8.1f, 2.787f)
      curveTo(9.317f, 2.263f, 10.617f, 2.0f, 12.0f, 2.0f)
      curveTo(13.383f, 2.0f, 14.683f, 2.263f, 15.9f, 2.787f)
      curveTo(17.117f, 3.313f, 18.175f, 4.025f, 19.075f, 4.925f)
      curveTo(19.975f, 5.825f, 20.688f, 6.883f, 21.212f, 8.1f)
      curveTo(21.737f, 9.317f, 22.0f, 10.617f, 22.0f, 12.0f)
      curveTo(22.0f, 13.383f, 21.737f, 14.683f, 21.212f, 15.9f)
      curveTo(20.688f, 17.117f, 19.975f, 18.175f, 19.075f, 19.075f)
      curveTo(18.175f, 19.975f, 17.117f, 20.688f, 15.9f, 21.212f)
      curveTo(14.683f, 21.737f, 13.383f, 22.0f, 12.0f, 22.0f)
      close()
    }
  }
}.build()
