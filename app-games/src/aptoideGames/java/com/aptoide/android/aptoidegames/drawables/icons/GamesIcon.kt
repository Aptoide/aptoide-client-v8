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

@Preview
@Composable
fun TestGamesIcon() {
  Image(
    imageVector = getGamesIcon(Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getGamesIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "GamesIcon",
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
      moveTo(4.726f, 19.938f)
      curveTo(3.742f, 19.938f, 2.903f, 19.585f, 2.207f, 18.881f)
      curveTo(1.511f, 18.177f, 1.147f, 17.325f, 1.113f, 16.325f)
      curveTo(1.113f, 16.175f, 1.122f, 16.027f, 1.138f, 15.881f)
      curveTo(1.155f, 15.735f, 1.18f, 15.587f, 1.213f, 15.438f)
      lineTo(3.313f, 7.037f)
      curveTo(3.555f, 6.162f, 4.026f, 5.448f, 4.726f, 4.894f)
      curveTo(5.426f, 4.34f, 6.226f, 4.063f, 7.126f, 4.063f)
      horizontalLineTo(16.876f)
      curveTo(17.776f, 4.063f, 18.576f, 4.34f, 19.276f, 4.894f)
      curveTo(19.976f, 5.448f, 20.447f, 6.162f, 20.688f, 7.037f)
      lineTo(22.788f, 15.438f)
      curveTo(22.822f, 15.587f, 22.851f, 15.74f, 22.876f, 15.894f)
      curveTo(22.901f, 16.048f, 22.913f, 16.2f, 22.913f, 16.35f)
      curveTo(22.913f, 17.35f, 22.559f, 18.198f, 21.851f, 18.894f)
      curveTo(21.142f, 19.59f, 20.284f, 19.938f, 19.276f, 19.938f)
      curveTo(18.584f, 19.938f, 17.944f, 19.758f, 17.357f, 19.4f)
      curveTo(16.77f, 19.042f, 16.33f, 18.55f, 16.038f, 17.925f)
      lineTo(15.338f, 16.475f)
      curveTo(15.247f, 16.3f, 15.113f, 16.167f, 14.938f, 16.075f)
      curveTo(14.763f, 15.983f, 14.576f, 15.938f, 14.376f, 15.938f)
      horizontalLineTo(9.626f)
      curveTo(9.426f, 15.938f, 9.238f, 15.983f, 9.063f, 16.075f)
      curveTo(8.888f, 16.167f, 8.755f, 16.3f, 8.663f, 16.475f)
      lineTo(7.976f, 17.925f)
      curveTo(7.676f, 18.55f, 7.232f, 19.042f, 6.645f, 19.4f)
      curveTo(6.057f, 19.758f, 5.417f, 19.938f, 4.726f, 19.938f)
      close()
      moveTo(13.501f, 10.938f)
      curveTo(13.759f, 10.938f, 13.98f, 10.846f, 14.163f, 10.663f)
      curveTo(14.347f, 10.479f, 14.438f, 10.258f, 14.438f, 10.0f)
      curveTo(14.438f, 9.742f, 14.347f, 9.521f, 14.163f, 9.337f)
      curveTo(13.98f, 9.154f, 13.759f, 9.063f, 13.501f, 9.063f)
      curveTo(13.242f, 9.063f, 13.022f, 9.154f, 12.838f, 9.337f)
      curveTo(12.655f, 9.521f, 12.563f, 9.742f, 12.563f, 10.0f)
      curveTo(12.563f, 10.258f, 12.655f, 10.479f, 12.838f, 10.663f)
      curveTo(13.022f, 10.846f, 13.242f, 10.938f, 13.501f, 10.938f)
      close()
      moveTo(15.501f, 8.938f)
      curveTo(15.759f, 8.938f, 15.98f, 8.846f, 16.163f, 8.663f)
      curveTo(16.347f, 8.479f, 16.438f, 8.258f, 16.438f, 8.0f)
      curveTo(16.438f, 7.742f, 16.347f, 7.521f, 16.163f, 7.338f)
      curveTo(15.98f, 7.154f, 15.759f, 7.063f, 15.501f, 7.063f)
      curveTo(15.242f, 7.063f, 15.022f, 7.154f, 14.838f, 7.338f)
      curveTo(14.655f, 7.521f, 14.563f, 7.742f, 14.563f, 8.0f)
      curveTo(14.563f, 8.258f, 14.655f, 8.479f, 14.838f, 8.663f)
      curveTo(15.022f, 8.846f, 15.242f, 8.938f, 15.501f, 8.938f)
      close()
      moveTo(15.501f, 12.938f)
      curveTo(15.759f, 12.938f, 15.98f, 12.846f, 16.163f, 12.663f)
      curveTo(16.347f, 12.479f, 16.438f, 12.258f, 16.438f, 12.0f)
      curveTo(16.438f, 11.742f, 16.347f, 11.521f, 16.163f, 11.337f)
      curveTo(15.98f, 11.154f, 15.759f, 11.063f, 15.501f, 11.063f)
      curveTo(15.242f, 11.063f, 15.022f, 11.154f, 14.838f, 11.337f)
      curveTo(14.655f, 11.521f, 14.563f, 11.742f, 14.563f, 12.0f)
      curveTo(14.563f, 12.258f, 14.655f, 12.479f, 14.838f, 12.663f)
      curveTo(15.022f, 12.846f, 15.242f, 12.938f, 15.501f, 12.938f)
      close()
      moveTo(17.501f, 10.938f)
      curveTo(17.759f, 10.938f, 17.98f, 10.846f, 18.163f, 10.663f)
      curveTo(18.347f, 10.479f, 18.438f, 10.258f, 18.438f, 10.0f)
      curveTo(18.438f, 9.742f, 18.347f, 9.521f, 18.163f, 9.337f)
      curveTo(17.98f, 9.154f, 17.759f, 9.063f, 17.501f, 9.063f)
      curveTo(17.242f, 9.063f, 17.022f, 9.154f, 16.838f, 9.337f)
      curveTo(16.655f, 9.521f, 16.563f, 9.742f, 16.563f, 10.0f)
      curveTo(16.563f, 10.258f, 16.655f, 10.479f, 16.838f, 10.663f)
      curveTo(17.022f, 10.846f, 17.242f, 10.938f, 17.501f, 10.938f)
      close()
      moveTo(8.501f, 12.462f)
      curveTo(8.701f, 12.462f, 8.87f, 12.394f, 9.007f, 12.256f)
      curveTo(9.145f, 12.119f, 9.213f, 11.95f, 9.213f, 11.75f)
      verticalLineTo(10.712f)
      horizontalLineTo(10.251f)
      curveTo(10.451f, 10.712f, 10.62f, 10.644f, 10.757f, 10.506f)
      curveTo(10.894f, 10.369f, 10.963f, 10.2f, 10.963f, 10.0f)
      curveTo(10.963f, 9.8f, 10.894f, 9.631f, 10.757f, 9.494f)
      curveTo(10.62f, 9.356f, 10.451f, 9.288f, 10.251f, 9.288f)
      horizontalLineTo(9.213f)
      verticalLineTo(8.25f)
      curveTo(9.213f, 8.05f, 9.145f, 7.881f, 9.007f, 7.744f)
      curveTo(8.87f, 7.606f, 8.701f, 7.537f, 8.501f, 7.537f)
      curveTo(8.301f, 7.537f, 8.132f, 7.606f, 7.995f, 7.744f)
      curveTo(7.857f, 7.881f, 7.788f, 8.05f, 7.788f, 8.25f)
      verticalLineTo(9.288f)
      horizontalLineTo(6.751f)
      curveTo(6.551f, 9.288f, 6.382f, 9.356f, 6.245f, 9.494f)
      curveTo(6.107f, 9.631f, 6.038f, 9.8f, 6.038f, 10.0f)
      curveTo(6.038f, 10.2f, 6.107f, 10.369f, 6.245f, 10.506f)
      curveTo(6.382f, 10.644f, 6.551f, 10.712f, 6.751f, 10.712f)
      horizontalLineTo(7.788f)
      verticalLineTo(11.75f)
      curveTo(7.788f, 11.95f, 7.857f, 12.119f, 7.995f, 12.256f)
      curveTo(8.132f, 12.394f, 8.301f, 12.462f, 8.501f, 12.462f)
      close()
    }
  }
}.build()
