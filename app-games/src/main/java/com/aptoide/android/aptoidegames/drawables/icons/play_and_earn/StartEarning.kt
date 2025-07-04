package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
private fun TestStartEarningIcon() {
  Image(
    imageVector = getStartEarningIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getStartEarningIcon(): ImageVector = ImageVector.Builder(
  name = "StartEarning",
  defaultWidth = 180.0.dp,
  defaultHeight = 128.0.dp,
  viewportWidth = 180.0f,
  viewportHeight = 128.0f
).apply {
  path(
    fill = linearGradient(
      0.2f to Color(0xFFFFFFFF),
      1.0f to Color(0xFF999999),
      start = Offset(157.02f, 11.8f),
      end = Offset(157.02f, 38.41f)
    ),
    stroke = null,
    fillAlpha = 0.7f,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(145.9f, 38.41f)
    curveTo(142.84f, 38.41f, 140.22f, 37.35f, 138.04f, 35.23f)
    curveTo(135.87f, 33.1f, 134.78f, 30.51f, 134.78f, 27.44f)
    curveTo(134.78f, 24.82f, 135.57f, 22.47f, 137.16f, 20.42f)
    curveTo(138.74f, 18.36f, 140.81f, 17.05f, 143.37f, 16.48f)
    curveTo(144.22f, 13.38f, 145.9f, 10.87f, 148.43f, 8.95f)
    curveTo(150.95f, 7.03f, 153.82f, 6.07f, 157.02f, 6.07f)
    curveTo(160.96f, 6.07f, 164.3f, 7.44f, 167.05f, 10.19f)
    curveTo(169.79f, 12.93f, 171.17f, 16.28f, 171.17f, 20.22f)
    curveTo(173.49f, 20.49f, 175.42f, 21.49f, 176.95f, 23.22f)
    curveTo(178.49f, 24.96f, 179.25f, 26.99f, 179.25f, 29.31f)
    curveTo(179.25f, 31.84f, 178.37f, 33.99f, 176.6f, 35.76f)
    curveTo(174.83f, 37.53f, 172.68f, 38.41f, 170.16f, 38.41f)
    horizontalLineTo(145.9f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFFFEA04)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(32.0f, 35.56f)
    lineTo(88.89f, 0.0f)
    lineTo(145.78f, 35.56f)
    verticalLineTo(92.44f)
    lineTo(88.89f, 128.0f)
    lineTo(32.0f, 92.44f)
    verticalLineTo(35.56f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFD6A422)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(88.82f, 80.0f)
    curveTo(93.88f, 80.0f, 98.72f, 80.83f, 103.34f, 82.48f)
    curveTo(107.7f, 84.03f, 111.65f, 86.13f, 115.23f, 88.76f)
    verticalLineTo(96.01f)
    lineTo(109.29f, 95.41f)
    curveTo(109.01f, 95.61f, 109.57f, 95.21f, 109.29f, 95.41f)
    curveTo(106.43f, 93.28f, 103.27f, 91.65f, 99.82f, 90.51f)
    curveTo(96.38f, 89.38f, 92.71f, 88.81f, 88.82f, 88.81f)
    curveTo(84.93f, 88.81f, 81.26f, 89.38f, 77.82f, 90.51f)
    curveTo(74.37f, 91.65f, 71.22f, 93.28f, 68.36f, 95.41f)
    curveTo(68.07f, 95.21f, 68.63f, 95.61f, 68.36f, 95.41f)
    lineTo(61.2f, 96.01f)
    lineTo(61.75f, 89.25f)
    curveTo(65.49f, 86.39f, 69.68f, 84.13f, 74.3f, 82.48f)
    curveTo(78.92f, 80.83f, 83.76f, 80.0f, 88.82f, 80.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFF000000)),
    stroke = null,
    fillAlpha = 0.2f,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(88.82f, 80.0f)
    curveTo(93.88f, 80.0f, 98.72f, 80.83f, 103.34f, 82.48f)
    curveTo(107.7f, 84.03f, 111.65f, 86.13f, 115.23f, 88.76f)
    verticalLineTo(96.01f)
    lineTo(109.29f, 95.41f)
    curveTo(109.01f, 95.61f, 109.57f, 95.21f, 109.29f, 95.41f)
    curveTo(106.43f, 93.28f, 103.27f, 91.65f, 99.82f, 90.51f)
    curveTo(96.38f, 89.38f, 92.71f, 88.81f, 88.82f, 88.81f)
    curveTo(84.93f, 88.81f, 81.26f, 89.38f, 77.82f, 90.51f)
    curveTo(74.37f, 91.65f, 71.22f, 93.28f, 68.36f, 95.41f)
    curveTo(68.07f, 95.21f, 68.63f, 95.61f, 68.36f, 95.41f)
    lineTo(61.2f, 96.01f)
    lineTo(61.75f, 89.25f)
    curveTo(65.49f, 86.39f, 69.68f, 84.13f, 74.3f, 82.48f)
    curveTo(78.92f, 80.83f, 83.76f, 80.0f, 88.82f, 80.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFD6A422)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = EvenOdd
  ) {
    moveTo(102.02f, 71.2f)
    horizontalLineTo(75.62f)
    verticalLineTo(44.79f)
    horizontalLineTo(102.02f)
    verticalLineTo(71.2f)
    close()
    moveTo(82.66f, 64.16f)
    horizontalLineTo(94.98f)
    verticalLineTo(51.84f)
    horizontalLineTo(82.66f)
    verticalLineTo(64.16f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFF000000)),
    stroke = null,
    fillAlpha = 0.2f,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = EvenOdd
  ) {
    moveTo(102.02f, 71.2f)
    horizontalLineTo(75.62f)
    verticalLineTo(44.79f)
    horizontalLineTo(102.02f)
    verticalLineTo(71.2f)
    close()
    moveTo(82.66f, 64.16f)
    horizontalLineTo(94.98f)
    verticalLineTo(51.84f)
    horizontalLineTo(82.66f)
    verticalLineTo(64.16f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFFFC93E)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(88.89f, 21.33f)
    verticalLineTo(0.0f)
    lineTo(32.0f, 35.56f)
    verticalLineTo(92.44f)
    lineTo(88.89f, 128.0f)
    verticalLineTo(106.67f)
    lineTo(53.34f, 81.78f)
    verticalLineTo(46.22f)
    lineTo(88.89f, 21.33f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFD6A422)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(88.89f, 21.33f)
    verticalLineTo(0.0f)
    lineTo(145.78f, 35.56f)
    verticalLineTo(92.44f)
    lineTo(88.89f, 128.0f)
    verticalLineTo(106.67f)
    lineTo(124.44f, 81.78f)
    verticalLineTo(46.22f)
    lineTo(88.89f, 21.33f)
    close()
  }
  path(
    fill = linearGradient(
      0.2f to Color(0xFFFFFFFF),
      1.0f to Color(0xFF999999),
      start = Offset(142.46f, 29.15f),
      end = Offset(142.46f, 65.45f)
    ),
    stroke = null,
    fillAlpha = 0.9f,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(157.63f, 65.45f)
    curveTo(161.81f, 65.45f, 165.39f, 64.01f, 168.35f, 61.11f)
    curveTo(171.32f, 58.22f, 172.8f, 54.68f, 172.8f, 50.49f)
    curveTo(172.8f, 46.91f, 171.72f, 43.72f, 169.56f, 40.91f)
    curveTo(167.4f, 38.11f, 164.57f, 36.32f, 161.08f, 35.53f)
    curveTo(159.93f, 31.31f, 157.63f, 27.88f, 154.18f, 25.26f)
    curveTo(150.74f, 22.64f, 146.83f, 21.33f, 142.46f, 21.33f)
    curveTo(137.09f, 21.33f, 132.52f, 23.21f, 128.78f, 26.95f)
    curveTo(125.03f, 30.7f, 123.16f, 35.26f, 123.16f, 40.64f)
    curveTo(119.99f, 41.0f, 117.36f, 42.37f, 115.27f, 44.74f)
    curveTo(113.18f, 47.11f, 112.13f, 49.87f, 112.13f, 53.05f)
    curveTo(112.13f, 56.49f, 113.34f, 59.42f, 115.75f, 61.84f)
    curveTo(118.16f, 64.25f, 121.09f, 65.45f, 124.54f, 65.45f)
    horizontalLineTo(157.63f)
    close()
  }
  path(
    fill = linearGradient(
      0.2f to Color(0xFFFFFFFF),
      1.0f to Color(0xFF999999),
      start = Offset(37.0f, 72.6f),
      end = Offset(37.0f, 116.89f)
    ),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(18.5f, 116.89f)
    curveTo(13.4f, 116.89f, 9.04f, 115.12f, 5.42f, 111.59f)
    curveTo(1.81f, 108.06f, 0.0f, 103.74f, 0.0f, 98.64f)
    curveTo(0.0f, 94.27f, 1.32f, 90.37f, 3.95f, 86.95f)
    curveTo(6.59f, 83.53f, 10.03f, 81.35f, 14.3f, 80.39f)
    curveTo(15.7f, 75.24f, 18.5f, 71.06f, 22.7f, 67.86f)
    curveTo(26.91f, 64.67f, 31.67f, 63.07f, 37.0f, 63.07f)
    curveTo(43.56f, 63.07f, 49.12f, 65.36f, 53.69f, 69.92f)
    curveTo(58.26f, 74.49f, 60.55f, 80.06f, 60.55f, 86.62f)
    curveTo(64.41f, 87.06f, 67.62f, 88.73f, 70.17f, 91.62f)
    curveTo(72.72f, 94.51f, 74.0f, 97.88f, 74.0f, 101.75f)
    curveTo(74.0f, 105.96f, 72.53f, 109.53f, 69.59f, 112.47f)
    curveTo(66.64f, 115.42f, 63.07f, 116.89f, 58.86f, 116.89f)
    horizontalLineTo(18.5f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFffffff)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(57.2f, 7.56f)
    lineTo(60.07f, 12.46f)
    lineTo(64.97f, 15.33f)
    curveTo(66.29f, 16.11f, 66.29f, 18.03f, 64.97f, 18.8f)
    lineTo(60.07f, 21.67f)
    lineTo(57.2f, 26.58f)
    curveTo(56.42f, 27.9f, 54.5f, 27.9f, 53.73f, 26.58f)
    lineTo(50.86f, 21.67f)
    lineTo(45.95f, 18.8f)
    curveTo(44.63f, 18.03f, 44.63f, 16.11f, 45.95f, 15.33f)
    lineTo(50.86f, 12.46f)
    lineTo(53.73f, 7.56f)
    curveTo(54.5f, 6.24f, 56.42f, 6.24f, 57.2f, 7.56f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFffffff)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(133.58f, 112.32f)
    lineTo(133.99f, 113.26f)
    lineTo(134.69f, 113.8f)
    curveTo(134.88f, 113.95f, 134.88f, 114.32f, 134.69f, 114.46f)
    lineTo(133.99f, 115.01f)
    lineTo(133.58f, 115.94f)
    curveTo(133.47f, 116.2f, 133.2f, 116.2f, 133.09f, 115.94f)
    lineTo(132.68f, 115.01f)
    lineTo(131.98f, 114.46f)
    curveTo(131.79f, 114.32f, 131.79f, 113.95f, 131.98f, 113.8f)
    lineTo(132.68f, 113.26f)
    lineTo(133.09f, 112.32f)
    curveTo(133.2f, 112.07f, 133.47f, 112.07f, 133.58f, 112.32f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFffffff)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(155.33f, 84.36f)
    lineTo(158.2f, 89.26f)
    lineTo(163.11f, 92.13f)
    curveTo(164.43f, 92.91f, 164.43f, 94.83f, 163.11f, 95.6f)
    lineTo(158.2f, 98.47f)
    lineTo(155.33f, 103.38f)
    curveTo(154.56f, 104.7f, 152.64f, 104.7f, 151.87f, 103.38f)
    lineTo(148.99f, 98.47f)
    lineTo(144.09f, 95.6f)
    curveTo(142.77f, 94.83f, 142.77f, 92.91f, 144.09f, 92.13f)
    lineTo(148.99f, 89.26f)
    lineTo(151.87f, 84.36f)
    curveTo(152.64f, 83.04f, 154.56f, 83.04f, 155.33f, 84.36f)
    close()
  }
}.build()
