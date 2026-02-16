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

@Preview
@Composable
private fun TestTapToExchangeBackground() {
  Image(
    imageVector = getTapToExchangeBackground(),
    contentDescription = null,
  )
}

fun getTapToExchangeBackground(): ImageVector = ImageVector.Builder(
  name = "TapToExchangeBackground",
  defaultWidth = 360.0.dp,
  defaultHeight = 257.0.dp,
  viewportWidth = 360.0f,
  viewportHeight = 257.0f
).apply {
  group {
    path(
      fill = radialGradient(
        0.17f to Color(0xFF913DD8),
        0.58f to Color(0x00595959),
        center = Offset(179.72f, 134.61f),
        radius = 300.0f
      ),
      stroke = null,
      fillAlpha = 0.4f,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(527.41f, 104.47f)
      lineTo(189.59f, 132.6f)
      lineTo(496.11f, -12.66f)
      curveTo(497.1f, -13.13f, 497.48f, -14.34f, 496.93f, -15.29f)
      lineTo(468.18f, -65.29f)
      curveTo(467.64f, -66.24f, 466.4f, -66.51f, 465.51f, -65.89f)
      lineTo(186.99f, 128.08f)
      lineTo(380.1f, -151.6f)
      curveTo(380.73f, -152.52f, 380.45f, -153.79f, 379.48f, -154.36f)
      lineTo(329.78f, -183.18f)
      curveTo(328.82f, -183.74f, 327.58f, -183.35f, 327.1f, -182.33f)
      lineTo(182.48f, 125.45f)
      lineTo(210.65f, -215.79f)
      horizontalLineTo(149.11f)
      lineTo(177.28f, 125.45f)
      lineTo(32.67f, -182.31f)
      curveTo(32.18f, -183.34f, 30.93f, -183.73f, 29.96f, -183.16f)
      lineTo(-19.71f, -154.37f)
      curveTo(-20.68f, -153.8f, -20.97f, -152.52f, -20.32f, -151.58f)
      lineTo(172.77f, 128.07f)
      lineTo(-105.74f, -65.89f)
      curveTo(-106.64f, -66.51f, -107.89f, -66.24f, -108.43f, -65.28f)
      lineTo(-137.16f, -15.3f)
      curveTo(-137.71f, -14.35f, -137.33f, -13.13f, -136.34f, -12.65f)
      lineTo(170.16f, 132.6f)
      lineTo(-167.58f, 104.48f)
      curveTo(-168.68f, 104.39f, -169.63f, 105.25f, -169.63f, 106.37f)
      verticalLineTo(164.05f)
      curveTo(-169.63f, 165.16f, -168.68f, 166.03f, -167.58f, 165.94f)
      lineTo(170.16f, 137.82f)
      lineTo(-136.46f, 283.13f)
      curveTo(-137.39f, 283.57f, -137.75f, 284.71f, -137.24f, 285.6f)
      lineTo(-108.37f, 335.81f)
      curveTo(-107.86f, 336.7f, -106.69f, 336.96f, -105.85f, 336.38f)
      lineTo(172.76f, 142.35f)
      lineTo(-20.48f, 422.23f)
      curveTo(-21.04f, 423.04f, -20.79f, 424.16f, -19.94f, 424.65f)
      lineTo(30.19f, 453.72f)
      curveTo(31.04f, 454.21f, 32.13f, 453.87f, 32.55f, 452.98f)
      lineTo(177.27f, 144.97f)
      lineTo(149.27f, 484.2f)
      curveTo(149.18f, 485.28f, 150.03f, 486.21f, 151.12f, 486.21f)
      horizontalLineTo(208.64f)
      curveTo(209.72f, 486.21f, 210.57f, 485.28f, 210.49f, 484.2f)
      lineTo(182.48f, 144.97f)
      lineTo(327.13f, 452.81f)
      curveTo(327.59f, 453.8f, 328.79f, 454.17f, 329.72f, 453.63f)
      lineTo(379.53f, 424.74f)
      curveTo(380.47f, 424.19f, 380.74f, 422.96f, 380.13f, 422.07f)
      lineTo(186.99f, 142.34f)
      lineTo(465.51f, 336.31f)
      curveTo(466.41f, 336.93f, 467.64f, 336.65f, 468.18f, 335.71f)
      lineTo(496.93f, 285.7f)
      curveTo(497.47f, 284.75f, 497.09f, 283.54f, 496.11f, 283.08f)
      lineTo(189.59f, 137.82f)
      lineTo(527.4f, 165.94f)
      curveTo(528.47f, 166.03f, 529.38f, 165.19f, 529.38f, 164.12f)
      verticalLineTo(106.3f)
      curveTo(529.38f, 105.23f, 528.46f, 104.38f, 527.4f, 104.47f)
      horizontalLineTo(527.41f)
      close()
    }
    path(
      fill = linearGradient(
        0.0f to Color(0xFF18181E),
        1.0f to Color(0x0018181E),
        start = Offset(154.54f, 1.88f),
        end = Offset(189.5f, 132.07f)
      ),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(527.41f, 104.47f)
      lineTo(189.59f, 132.6f)
      lineTo(496.11f, -12.66f)
      curveTo(497.1f, -13.13f, 497.48f, -14.34f, 496.93f, -15.29f)
      lineTo(468.18f, -65.29f)
      curveTo(467.64f, -66.24f, 466.4f, -66.51f, 465.51f, -65.89f)
      lineTo(186.99f, 128.08f)
      lineTo(380.1f, -151.6f)
      curveTo(380.73f, -152.52f, 380.45f, -153.79f, 379.48f, -154.36f)
      lineTo(329.78f, -183.18f)
      curveTo(328.82f, -183.74f, 327.58f, -183.35f, 327.1f, -182.33f)
      lineTo(182.48f, 125.45f)
      lineTo(210.65f, -215.79f)
      horizontalLineTo(149.11f)
      lineTo(177.28f, 125.45f)
      lineTo(32.67f, -182.31f)
      curveTo(32.18f, -183.34f, 30.93f, -183.73f, 29.96f, -183.16f)
      lineTo(-19.71f, -154.37f)
      curveTo(-20.68f, -153.8f, -20.97f, -152.52f, -20.32f, -151.58f)
      lineTo(172.77f, 128.07f)
      lineTo(-105.74f, -65.89f)
      curveTo(-106.64f, -66.51f, -107.89f, -66.24f, -108.43f, -65.28f)
      lineTo(-137.16f, -15.3f)
      curveTo(-137.71f, -14.35f, -137.33f, -13.13f, -136.34f, -12.65f)
      lineTo(170.16f, 132.6f)
      lineTo(-167.58f, 104.48f)
      curveTo(-168.68f, 104.39f, -169.63f, 105.25f, -169.63f, 106.37f)
      verticalLineTo(164.05f)
      curveTo(-169.63f, 165.16f, -168.68f, 166.03f, -167.58f, 165.94f)
      lineTo(170.16f, 137.82f)
      lineTo(-136.46f, 283.13f)
      curveTo(-137.39f, 283.57f, -137.75f, 284.71f, -137.24f, 285.6f)
      lineTo(-108.37f, 335.81f)
      curveTo(-107.86f, 336.7f, -106.69f, 336.96f, -105.85f, 336.38f)
      lineTo(172.76f, 142.35f)
      lineTo(-20.48f, 422.23f)
      curveTo(-21.04f, 423.04f, -20.79f, 424.16f, -19.94f, 424.65f)
      lineTo(30.19f, 453.72f)
      curveTo(31.04f, 454.21f, 32.13f, 453.87f, 32.55f, 452.98f)
      lineTo(177.27f, 144.97f)
      lineTo(149.27f, 484.2f)
      curveTo(149.18f, 485.28f, 150.03f, 486.21f, 151.12f, 486.21f)
      horizontalLineTo(208.64f)
      curveTo(209.72f, 486.21f, 210.57f, 485.28f, 210.49f, 484.2f)
      lineTo(182.48f, 144.97f)
      lineTo(327.13f, 452.81f)
      curveTo(327.59f, 453.8f, 328.79f, 454.17f, 329.72f, 453.63f)
      lineTo(379.53f, 424.74f)
      curveTo(380.47f, 424.19f, 380.74f, 422.96f, 380.13f, 422.07f)
      lineTo(186.99f, 142.34f)
      lineTo(465.51f, 336.31f)
      curveTo(466.41f, 336.93f, 467.64f, 336.65f, 468.18f, 335.71f)
      lineTo(496.93f, 285.7f)
      curveTo(497.47f, 284.75f, 497.09f, 283.54f, 496.11f, 283.08f)
      lineTo(189.59f, 137.82f)
      lineTo(527.4f, 165.94f)
      curveTo(528.47f, 166.03f, 529.38f, 165.19f, 529.38f, 164.12f)
      verticalLineTo(106.3f)
      curveTo(529.38f, 105.23f, 528.46f, 104.38f, 527.4f, 104.47f)
      horizontalLineTo(527.41f)
      close()
    }
    path(
      fill = radialGradient(
        0.17f to Color(0xFF913DD8),
        0.58f to Color(0x00595959),
        center = Offset(179.69f, 134.6f),
        radius = 300.0f
      ),
      stroke = null,
      fillAlpha = 0.4f,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin =
        Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(523.67f, 195.6f)
      lineTo(189.76f, 135.21f)
      lineTo(523.7f, 74.48f)
      curveTo(524.77f, 74.28f, 525.45f, 73.21f, 525.16f, 72.15f)
      lineTo(510.3f, 16.46f)
      curveTo(510.01f, 15.41f, 508.89f, 14.82f, 507.86f, 15.19f)
      lineTo(188.41f, 130.18f)
      lineTo(447.44f, -89.64f)
      curveTo(448.3f, -90.37f, 448.35f, -91.66f, 447.56f, -92.46f)
      lineTo(406.95f, -133.14f)
      curveTo(406.16f, -133.93f, 404.87f, -133.88f, 404.14f, -133.02f)
      lineTo(184.73f, 126.48f)
      lineTo(300.19f, -195.48f)
      lineTo(240.68f, -211.42f)
      lineTo(179.7f, 125.13f)
      lineTo(119.44f, -209.29f)
      curveTo(119.24f, -210.4f, 118.13f, -211.11f, 117.04f, -210.81f)
      lineTo(61.57f, -195.89f)
      curveTo(60.49f, -195.6f, 59.87f, -194.44f, 60.26f, -193.37f)
      lineTo(174.67f, 126.49f)
      lineTo(-44.49f, -132.81f)
      curveTo(-45.2f, -133.65f, -46.48f, -133.7f, -47.25f, -132.92f)
      lineTo(-87.95f, -92.15f)
      curveTo(-88.73f, -91.36f, -88.68f, -90.09f, -87.84f, -89.38f)
      lineTo(170.97f, 130.18f)
      lineTo(-148.33f, 15.55f)
      curveTo(-149.37f, 15.17f, -150.51f, 15.77f, -150.8f, 16.84f)
      lineTo(-165.71f, 72.49f)
      curveTo(-166.0f, 73.57f, -165.31f, 74.65f, -164.22f, 74.85f)
      lineTo(169.62f, 135.22f)
      lineTo(-164.43f, 195.98f)
      curveTo(-165.44f, 196.17f, -166.08f, 197.18f, -165.82f, 198.17f)
      lineTo(-150.89f, 254.09f)
      curveTo(-150.62f, 255.09f, -149.56f, 255.64f, -148.6f, 255.3f)
      lineTo(170.97f, 140.27f)
      lineTo(-88.25f, 360.24f)
      curveTo(-89.0f, 360.88f, -89.05f, 362.03f, -88.35f, 362.72f)
      lineTo(-47.4f, 403.76f)
      curveTo(-46.7f, 404.45f, -45.55f, 404.41f, -44.92f, 403.65f)
      lineTo(174.65f, 143.96f)
      lineTo(59.87f, 464.02f)
      curveTo(59.5f, 465.04f, 60.09f, 466.16f, 61.14f, 466.44f)
      lineTo(116.75f, 481.34f)
      curveTo(117.8f, 481.62f, 118.86f, 480.95f, 119.06f, 479.88f)
      lineTo(179.68f, 145.31f)
      lineTo(239.96f, 479.82f)
      curveTo(240.15f, 480.89f, 241.21f, 481.56f, 242.26f, 481.27f)
      lineTo(297.89f, 466.31f)
      curveTo(298.94f, 466.03f, 299.52f, 464.9f, 299.16f, 463.89f)
      lineTo(184.72f, 143.95f)
      lineTo(403.89f, 403.26f)
      curveTo(404.59f, 404.09f, 405.85f, 404.14f, 406.62f, 403.37f)
      lineTo(447.35f, 362.56f)
      curveTo(448.12f, 361.8f, 448.07f, 360.53f, 447.24f, 359.83f)
      lineTo(188.4f, 140.25f)
      lineTo(507.77f, 254.91f)
      curveTo(508.78f, 255.27f, 509.88f, 254.7f, 510.16f, 253.66f)
      lineTo(525.1f, 197.88f)
      curveTo(525.38f, 196.84f, 524.72f, 195.79f, 523.67f, 195.6f)
      lineTo(523.67f, 195.6f)
      close()
    }
    path(
      fill = linearGradient(
        0.0f to Color(0xFF18181E),
        1.0f to Color(0x0018181E),
        start = Offset(189.66f, 0.01f),
        end = Offset(189.66f, 134.68f)
      ),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(523.67f, 195.6f)
      lineTo(189.76f, 135.21f)
      lineTo(523.7f, 74.48f)
      curveTo(524.77f, 74.28f, 525.45f, 73.21f, 525.16f, 72.15f)
      lineTo(510.3f, 16.46f)
      curveTo(510.01f, 15.41f, 508.89f, 14.82f, 507.86f, 15.19f)
      lineTo(188.41f, 130.18f)
      lineTo(447.44f, -89.64f)
      curveTo(448.3f, -90.37f, 448.35f, -91.66f, 447.56f, -92.46f)
      lineTo(406.95f, -133.14f)
      curveTo(406.16f, -133.93f, 404.87f, -133.88f, 404.14f, -133.02f)
      lineTo(184.73f, 126.48f)
      lineTo(300.19f, -195.48f)
      lineTo(240.68f, -211.42f)
      lineTo(179.7f, 125.13f)
      lineTo(119.44f, -209.29f)
      curveTo(119.24f, -210.4f, 118.13f, -211.11f, 117.04f, -210.81f)
      lineTo(61.57f, -195.89f)
      curveTo(60.49f, -195.6f, 59.87f, -194.44f, 60.26f, -193.37f)
      lineTo(174.67f, 126.49f)
      lineTo(-44.49f, -132.81f)
      curveTo(-45.2f, -133.65f, -46.48f, -133.7f, -47.25f, -132.92f)
      lineTo(-87.95f, -92.15f)
      curveTo(-88.73f, -91.36f, -88.68f, -90.09f, -87.84f, -89.38f)
      lineTo(170.97f, 130.18f)
      lineTo(-148.33f, 15.55f)
      curveTo(-149.37f, 15.17f, -150.51f, 15.77f, -150.8f, 16.84f)
      lineTo(-165.71f, 72.49f)
      curveTo(-166.0f, 73.57f, -165.31f, 74.65f, -164.22f, 74.85f)
      lineTo(169.62f, 135.22f)
      lineTo(-164.43f, 195.98f)
      curveTo(-165.44f, 196.17f, -166.08f, 197.18f, -165.82f, 198.17f)
      lineTo(-150.89f, 254.09f)
      curveTo(-150.62f, 255.09f, -149.56f, 255.64f, -148.6f, 255.3f)
      lineTo(170.97f, 140.27f)
      lineTo(-88.25f, 360.24f)
      curveTo(-89.0f, 360.88f, -89.05f, 362.03f, -88.35f, 362.72f)
      lineTo(-47.4f, 403.76f)
      curveTo(-46.7f, 404.45f, -45.55f, 404.41f, -44.92f, 403.65f)
      lineTo(174.65f, 143.96f)
      lineTo(59.87f, 464.02f)
      curveTo(59.5f, 465.04f, 60.09f, 466.16f, 61.14f, 466.44f)
      lineTo(116.75f, 481.34f)
      curveTo(117.8f, 481.62f, 118.86f, 480.95f, 119.06f, 479.88f)
      lineTo(179.68f, 145.31f)
      lineTo(239.96f, 479.82f)
      curveTo(240.15f, 480.89f, 241.21f, 481.56f, 242.26f, 481.27f)
      lineTo(297.89f, 466.31f)
      curveTo(298.94f, 466.03f, 299.52f, 464.9f, 299.16f, 463.89f)
      lineTo(184.72f, 143.95f)
      lineTo(403.89f, 403.26f)
      curveTo(404.59f, 404.09f, 405.85f, 404.14f, 406.62f, 403.37f)
      lineTo(447.35f, 362.56f)
      curveTo(448.12f, 361.8f, 448.07f, 360.53f, 447.24f, 359.83f)
      lineTo(188.4f, 140.25f)
      lineTo(507.77f, 254.91f)
      curveTo(508.78f, 255.27f, 509.88f, 254.7f, 510.16f, 253.66f)
      lineTo(525.1f, 197.88f)
      curveTo(525.38f, 196.84f, 524.72f, 195.79f, 523.67f, 195.6f)
      lineTo(523.67f, 195.6f)
      close()
    }
  }
}.build()
