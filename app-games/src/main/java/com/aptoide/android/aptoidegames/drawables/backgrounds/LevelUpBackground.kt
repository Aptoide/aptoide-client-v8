package com.aptoide.android.aptoidegames.drawables.backgrounds

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
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
private fun TestLevelUpBackground() {
  Image(
    imageVector = getLevelUpBackground(),
    contentDescription = null,
  )
}

fun getLevelUpBackground(level: Int = 0): ImageVector = ImageVector.Builder(
  name = "LevelUpBackground",
  defaultWidth = 360.0.dp,
  defaultHeight = 294.0.dp,
  viewportWidth = 360.0f,
  viewportHeight = 294.0f
).apply {
  group {
    path(
      fill = radialGradient(
        0.0f to getLevelColor(level),
        0.84f to Color(0x00595959),
        center = Offset(179.85f, 128.95f),
        radius = 176.08f
      ),
      stroke = null,
      fillAlpha = 0.4f,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(501.49f, 101.12f)
      lineTo(188.99f, 127.09f)
      lineTo(472.54f, -7.0f)
      curveTo(473.45f, -7.43f, 473.8f, -8.55f, 473.3f, -9.42f)
      lineTo(446.7f, -55.58f)
      curveTo(446.2f, -56.45f, 445.05f, -56.71f, 444.23f, -56.13f)
      lineTo(186.58f, 122.92f)
      lineTo(365.22f, -135.26f)
      curveTo(365.81f, -136.11f, 365.55f, -137.28f, 364.65f, -137.8f)
      lineTo(318.67f, -164.4f)
      curveTo(317.78f, -164.92f, 316.64f, -164.56f, 316.19f, -163.62f)
      lineTo(182.41f, 120.49f)
      lineTo(208.47f, -194.51f)
      horizontalLineTo(151.54f)
      lineTo(177.6f, 120.49f)
      lineTo(43.82f, -163.6f)
      curveTo(43.37f, -164.55f, 42.22f, -164.92f, 41.32f, -164.39f)
      lineTo(-4.63f, -137.81f)
      curveTo(-5.53f, -137.29f, -5.8f, -136.1f, -5.2f, -135.24f)
      lineTo(173.43f, 122.91f)
      lineTo(-84.21f, -56.13f)
      curveTo(-85.05f, -56.71f, -86.2f, -56.45f, -86.71f, -55.57f)
      lineTo(-113.28f, -9.44f)
      curveTo(-113.79f, -8.56f, -113.44f, -7.43f, -112.52f, -6.99f)
      lineTo(171.02f, 127.09f)
      lineTo(-141.42f, 101.13f)
      curveTo(-142.44f, 101.04f, -143.31f, 101.85f, -143.31f, 102.88f)
      verticalLineTo(156.12f)
      curveTo(-143.31f, 157.15f, -142.44f, 157.95f, -141.42f, 157.87f)
      lineTo(171.02f, 131.91f)
      lineTo(-112.63f, 266.04f)
      curveTo(-113.49f, 266.45f, -113.82f, 267.51f, -113.35f, 268.33f)
      lineTo(-86.65f, 314.67f)
      curveTo(-86.17f, 315.5f, -85.1f, 315.74f, -84.31f, 315.2f)
      lineTo(173.42f, 136.09f)
      lineTo(-5.34f, 394.44f)
      curveTo(-5.86f, 395.2f, -5.63f, 396.23f, -4.84f, 396.68f)
      lineTo(41.53f, 423.52f)
      curveTo(42.32f, 423.97f, 43.33f, 423.65f, 43.72f, 422.83f)
      lineTo(177.59f, 138.51f)
      lineTo(151.69f, 451.65f)
      curveTo(151.6f, 452.65f, 152.39f, 453.51f, 153.4f, 453.51f)
      horizontalLineTo(206.61f)
      curveTo(207.61f, 453.51f, 208.4f, 452.65f, 208.32f, 451.65f)
      lineTo(182.41f, 138.51f)
      lineTo(316.22f, 422.68f)
      curveTo(316.64f, 423.59f, 317.76f, 423.93f, 318.62f, 423.43f)
      lineTo(364.7f, 396.76f)
      curveTo(365.57f, 396.26f, 365.82f, 395.12f, 365.25f, 394.3f)
      lineTo(186.58f, 136.09f)
      lineTo(444.23f, 315.13f)
      curveTo(445.06f, 315.71f, 446.2f, 315.45f, 446.7f, 314.58f)
      lineTo(473.3f, 268.41f)
      curveTo(473.8f, 267.54f, 473.45f, 266.42f, 472.54f, 266.0f)
      lineTo(188.98f, 131.91f)
      lineTo(501.48f, 157.87f)
      curveTo(502.47f, 157.95f, 503.31f, 157.17f, 503.31f, 156.18f)
      verticalLineTo(102.81f)
      curveTo(503.31f, 101.82f, 502.46f, 101.04f, 501.48f, 101.12f)
      horizontalLineTo(501.49f)
      close()
    }
    path(
      fill = linearGradient(
        0.0f to Color(0xFF18181E),
        1.0f to Color(0x0018181E),
        start = Offset(156.56f, 6.42f),
        end = Offset(188.77f, 126.63f)
      ),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(501.49f, 101.12f)
      lineTo(188.99f, 127.09f)
      lineTo(472.54f, -7.0f)
      curveTo(473.45f, -7.43f, 473.8f, -8.55f, 473.3f, -9.42f)
      lineTo(446.7f, -55.58f)
      curveTo(446.2f, -56.45f, 445.05f, -56.71f, 444.23f, -56.13f)
      lineTo(186.58f, 122.92f)
      lineTo(365.22f, -135.26f)
      curveTo(365.81f, -136.11f, 365.55f, -137.28f, 364.65f, -137.8f)
      lineTo(318.67f, -164.4f)
      curveTo(317.78f, -164.92f, 316.64f, -164.56f, 316.19f, -163.62f)
      lineTo(182.41f, 120.49f)
      lineTo(208.47f, -194.51f)
      horizontalLineTo(151.54f)
      lineTo(177.6f, 120.49f)
      lineTo(43.82f, -163.6f)
      curveTo(43.37f, -164.55f, 42.22f, -164.92f, 41.32f, -164.39f)
      lineTo(-4.63f, -137.81f)
      curveTo(-5.53f, -137.29f, -5.8f, -136.1f, -5.2f, -135.24f)
      lineTo(173.43f, 122.91f)
      lineTo(-84.21f, -56.13f)
      curveTo(-85.05f, -56.71f, -86.2f, -56.45f, -86.71f, -55.57f)
      lineTo(-113.28f, -9.44f)
      curveTo(-113.79f, -8.56f, -113.44f, -7.43f, -112.52f, -6.99f)
      lineTo(171.02f, 127.09f)
      lineTo(-141.42f, 101.13f)
      curveTo(-142.44f, 101.04f, -143.31f, 101.85f, -143.31f, 102.88f)
      verticalLineTo(156.12f)
      curveTo(-143.31f, 157.15f, -142.44f, 157.95f, -141.42f, 157.87f)
      lineTo(171.02f, 131.91f)
      lineTo(-112.63f, 266.04f)
      curveTo(-113.49f, 266.45f, -113.82f, 267.51f, -113.35f, 268.33f)
      lineTo(-86.65f, 314.67f)
      curveTo(-86.17f, 315.5f, -85.1f, 315.74f, -84.31f, 315.2f)
      lineTo(173.42f, 136.09f)
      lineTo(-5.34f, 394.44f)
      curveTo(-5.86f, 395.2f, -5.63f, 396.23f, -4.84f, 396.68f)
      lineTo(41.53f, 423.52f)
      curveTo(42.32f, 423.97f, 43.33f, 423.65f, 43.72f, 422.83f)
      lineTo(177.59f, 138.51f)
      lineTo(151.69f, 451.65f)
      curveTo(151.6f, 452.65f, 152.39f, 453.51f, 153.4f, 453.51f)
      horizontalLineTo(206.61f)
      curveTo(207.61f, 453.51f, 208.4f, 452.65f, 208.32f, 451.65f)
      lineTo(182.41f, 138.51f)
      lineTo(316.22f, 422.68f)
      curveTo(316.64f, 423.59f, 317.76f, 423.93f, 318.62f, 423.43f)
      lineTo(364.7f, 396.76f)
      curveTo(365.57f, 396.26f, 365.82f, 395.12f, 365.25f, 394.3f)
      lineTo(186.58f, 136.09f)
      lineTo(444.23f, 315.13f)
      curveTo(445.06f, 315.71f, 446.2f, 315.45f, 446.7f, 314.58f)
      lineTo(473.3f, 268.41f)
      curveTo(473.8f, 267.54f, 473.45f, 266.42f, 472.54f, 266.0f)
      lineTo(188.98f, 131.91f)
      lineTo(501.48f, 157.87f)
      curveTo(502.47f, 157.95f, 503.31f, 157.17f, 503.31f, 156.18f)
      verticalLineTo(102.81f)
      curveTo(503.31f, 101.82f, 502.46f, 101.04f, 501.48f, 101.12f)
      horizontalLineTo(501.49f)
      close()
    }
  }
}.build()

private fun getLevelColor(level: Int) = when (level) {
  1, 2 -> Palette.Orange150
  3, 4 -> Palette.Blue100
  5, 6 -> Palette.Yellow100
  7, 8 -> Palette.Blue100
  9, 10 -> Palette.Yellow100
  else -> Palette.Orange150
}
