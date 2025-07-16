package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn.levels.level_badges

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
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
private fun TestLevelFiveBadge() {
  Image(
    imageVector = getLevelFiveBadge(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getLevelFiveBadge(): ImageVector = ImageVector.Builder(
  name = "LevelFiveBadge", defaultWidth = 168.0.dp, defaultHeight = 96.0.dp, viewportWidth =
    168.0f, viewportHeight = 96.0f
).apply {
  path(
    fill = linearGradient(
      0.2f to Color(0xFFFFFFFF), 1.0f to Color(0xFF999999), start =
        Offset(139.57f, 73.42f), end = Offset(139.57f, 90.18f)
    ), stroke = null,
    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
    strokeLineMiter = 4.0f, pathFillType = NonZero
  ) {
    moveTo(132.57f, 90.18f)
    curveTo(130.64f, 90.18f, 128.99f, 89.51f, 127.62f, 88.18f)
    curveTo(126.25f, 86.84f, 125.57f, 85.21f, 125.57f, 83.28f)
    curveTo(125.57f, 81.62f, 126.07f, 80.15f, 127.06f, 78.85f)
    curveTo(128.06f, 77.56f, 129.37f, 76.73f, 130.98f, 76.37f)
    curveTo(131.51f, 74.42f, 132.57f, 72.84f, 134.16f, 71.63f)
    curveTo(135.75f, 70.42f, 137.55f, 69.82f, 139.57f, 69.82f)
    curveTo(142.05f, 69.82f, 144.15f, 70.68f, 145.88f, 72.41f)
    curveTo(147.61f, 74.14f, 148.48f, 76.25f, 148.48f, 78.73f)
    curveTo(149.94f, 78.9f, 151.15f, 79.53f, 152.12f, 80.62f)
    curveTo(153.09f, 81.71f, 153.57f, 82.99f, 153.57f, 84.45f)
    curveTo(153.57f, 86.05f, 153.01f, 87.4f, 151.9f, 88.51f)
    curveTo(150.78f, 89.63f, 149.43f, 90.18f, 147.84f, 90.18f)
    horizontalLineTo(132.57f)
    close()
  }
  path(
    fill = linearGradient(
      0.2f to Color(0xFFFFFFFF), 1.0f to Color(0xFF999999), start =
        Offset(126.57f, 15.25f), end = Offset(126.57f, 35.0f)
    ), stroke = null, fillAlpha =
      0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
    strokeLineMiter = 4.0f, pathFillType = NonZero
  ) {
    moveTo(118.07f, 35.0f)
    curveTo(115.72f, 35.0f, 113.72f, 34.21f, 112.06f, 32.64f)
    curveTo(110.4f, 31.06f, 109.57f, 29.14f, 109.57f, 26.86f)
    curveTo(109.57f, 24.91f, 110.17f, 23.17f, 111.38f, 21.65f)
    curveTo(112.6f, 20.13f, 114.18f, 19.15f, 116.14f, 18.73f)
    curveTo(116.78f, 16.42f, 118.07f, 14.56f, 120.0f, 13.14f)
    curveTo(121.93f, 11.71f, 124.12f, 11.0f, 126.57f, 11.0f)
    curveTo(129.58f, 11.0f, 132.14f, 12.02f, 134.24f, 14.06f)
    curveTo(136.34f, 16.09f, 137.39f, 18.58f, 137.39f, 21.5f)
    curveTo(139.16f, 21.7f, 140.64f, 22.44f, 141.81f, 23.73f)
    curveTo(142.98f, 25.02f, 143.57f, 26.52f, 143.57f, 28.25f)
    curveTo(143.57f, 30.13f, 142.89f, 31.72f, 141.54f, 33.03f)
    curveTo(140.19f, 34.34f, 138.55f, 35.0f, 136.61f, 35.0f)
    horizontalLineTo(118.07f)
    close()
  }
  path(
    fill = linearGradient(
      0.2f to Color(0xFFFFFFFF), 1.0f to Color(0xFF999999), start =
        Offset(37.57f, 24.31f), end = Offset(37.57f, 49.0f)
    ), stroke = null, fillAlpha =
      0.6f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
    strokeLineMiter = 4.0f, pathFillType = NonZero
  ) {
    moveTo(48.07f, 49.0f)
    curveTo(50.96f, 49.0f, 53.44f, 48.02f, 55.49f, 46.05f)
    curveTo(57.54f, 44.08f, 58.57f, 41.67f, 58.57f, 38.83f)
    curveTo(58.57f, 36.39f, 57.82f, 34.22f, 56.33f, 32.31f)
    curveTo(54.83f, 30.41f, 52.87f, 29.19f, 50.45f, 28.66f)
    curveTo(49.66f, 25.78f, 48.07f, 23.45f, 45.68f, 21.67f)
    curveTo(43.3f, 19.89f, 40.59f, 19.0f, 37.57f, 19.0f)
    curveTo(33.85f, 19.0f, 30.69f, 20.27f, 28.09f, 22.82f)
    curveTo(25.5f, 25.37f, 24.2f, 28.47f, 24.2f, 32.13f)
    curveTo(22.01f, 32.38f, 20.19f, 33.3f, 18.74f, 34.91f)
    curveTo(17.29f, 36.52f, 16.57f, 38.41f, 16.57f, 40.56f)
    curveTo(16.57f, 42.91f, 17.4f, 44.9f, 19.07f, 46.54f)
    curveTo(20.74f, 48.18f, 22.77f, 49.0f, 25.16f, 49.0f)
    horizontalLineTo(48.07f)
    close()
  }
  group {
    path(
      fill = SolidColor(Color(0xFFF58932)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(84.0f, 95.31f)
      curveTo(110.51f, 95.31f, 132.0f, 73.97f, 132.0f, 47.65f)
      curveTo(132.0f, 21.34f, 110.51f, 0.0f, 84.0f, 0.0f)
      curveTo(57.49f, 0.0f, 36.0f, 21.34f, 36.0f, 47.65f)
      curveTo(36.0f, 73.97f, 57.49f, 95.31f, 84.0f, 95.31f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFEC617)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(84.0f, 95.31f)
      curveTo(110.51f, 95.31f, 132.0f, 73.97f, 132.0f, 47.65f)
      curveTo(132.0f, 21.34f, 110.51f, 0.0f, 84.0f, 0.0f)
      curveTo(57.49f, 0.0f, 36.0f, 21.34f, 36.0f, 47.65f)
      curveTo(36.0f, 73.97f, 57.49f, 95.31f, 84.0f, 95.31f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFEDC3E)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(45.02f, 19.87f)
      lineTo(111.99f, 86.36f)
      curveTo(117.89f, 82.15f, 122.8f, 76.65f, 126.27f, 70.25f)
      lineTo(61.24f, 5.69f)
      curveTo(54.81f, 9.14f, 49.26f, 14.01f, 45.02f, 19.87f)
      verticalLineTo(19.87f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFEDC3E)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(39.86f, 28.91f)
      lineTo(102.88f, 91.48f)
      curveTo(105.03f, 90.56f, 107.1f, 89.5f, 109.07f, 88.29f)
      lineTo(43.07f, 22.76f)
      curveTo(41.85f, 24.73f, 40.78f, 26.78f, 39.86f, 28.91f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(87.22f, 81.33f)
      curveTo(105.2f, 81.33f, 119.77f, 66.86f, 119.77f, 49.01f)
      curveTo(119.77f, 31.16f, 105.2f, 16.69f, 87.22f, 16.69f)
      curveTo(69.24f, 16.69f, 54.67f, 31.16f, 54.67f, 49.01f)
      curveTo(54.67f, 66.86f, 69.24f, 81.33f, 87.22f, 81.33f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFEC617)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(84.78f, 78.9f)
      curveTo(102.76f, 78.9f, 117.33f, 64.43f, 117.33f, 46.59f)
      curveTo(117.33f, 28.74f, 102.76f, 14.27f, 84.78f, 14.27f)
      curveTo(66.8f, 14.27f, 52.23f, 28.74f, 52.23f, 46.59f)
      curveTo(52.23f, 64.43f, 66.8f, 78.9f, 84.78f, 78.9f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFA97F07)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(78.46f, 48.22f)
      lineTo(71.93f, 46.68f)
      lineTo(74.29f, 25.92f)
      horizontalLineTo(97.53f)
      verticalLineTo(32.47f)
      horizontalLineTo(81.02f)
      lineTo(80.0f, 41.56f)
      curveTo(80.55f, 41.24f, 81.38f, 40.9f, 82.5f, 40.54f)
      curveTo(83.62f, 40.17f, 84.87f, 39.98f, 86.25f, 39.98f)
      curveTo(88.26f, 39.98f, 90.04f, 40.29f, 91.59f, 40.91f)
      curveTo(93.14f, 41.53f, 94.46f, 42.43f, 95.54f, 43.61f)
      curveTo(96.64f, 44.79f, 97.47f, 46.24f, 98.04f, 47.94f)
      curveTo(98.61f, 49.65f, 98.89f, 51.58f, 98.89f, 53.74f)
      curveTo(98.89f, 55.55f, 98.61f, 57.29f, 98.04f, 58.94f)
      curveTo(97.47f, 60.57f, 96.61f, 62.03f, 95.45f, 63.33f)
      curveTo(94.3f, 64.6f, 92.85f, 65.6f, 91.11f, 66.34f)
      curveTo(89.36f, 67.07f, 87.3f, 67.43f, 84.91f, 67.43f)
      curveTo(83.13f, 67.43f, 81.41f, 67.17f, 79.74f, 66.64f)
      curveTo(78.1f, 66.12f, 76.61f, 65.34f, 75.28f, 64.31f)
      curveTo(73.98f, 63.28f, 72.93f, 62.03f, 72.13f, 60.57f)
      curveTo(71.35f, 59.09f, 70.95f, 57.4f, 70.91f, 55.51f)
      horizontalLineTo(79.03f)
      curveTo(79.15f, 56.67f, 79.45f, 57.67f, 79.94f, 58.52f)
      curveTo(80.45f, 59.34f, 81.13f, 59.98f, 81.96f, 60.43f)
      curveTo(82.79f, 60.88f, 83.77f, 61.1f, 84.89f, 61.1f)
      curveTo(85.93f, 61.1f, 86.82f, 60.91f, 87.56f, 60.51f)
      curveTo(88.29f, 60.12f, 88.89f, 59.58f, 89.35f, 58.88f)
      curveTo(89.8f, 58.17f, 90.13f, 57.34f, 90.34f, 56.41f)
      curveTo(90.57f, 55.45f, 90.68f, 54.42f, 90.68f, 53.31f)
      curveTo(90.68f, 52.21f, 90.55f, 51.2f, 90.28f, 50.3f)
      curveTo(90.02f, 49.4f, 89.61f, 48.63f, 89.06f, 47.97f)
      curveTo(88.51f, 47.31f, 87.81f, 46.81f, 86.96f, 46.45f)
      curveTo(86.13f, 46.09f, 85.15f, 45.92f, 84.03f, 45.92f)
      curveTo(82.52f, 45.92f, 81.34f, 46.15f, 80.51f, 46.62f)
      curveTo(79.7f, 47.09f, 79.01f, 47.62f, 78.46f, 48.22f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFA97F07)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(115.52f, 46.59f)
      curveTo(115.52f, 29.73f, 101.76f, 16.07f, 84.78f, 16.07f)
      curveTo(67.8f, 16.07f, 54.04f, 29.73f, 54.04f, 46.59f)
      curveTo(54.04f, 63.44f, 67.8f, 77.1f, 84.78f, 77.1f)
      curveTo(101.76f, 77.1f, 115.52f, 63.44f, 115.52f, 46.59f)
      close()
      moveTo(119.15f, 46.59f)
      curveTo(119.15f, 65.43f, 103.76f, 80.71f, 84.78f, 80.71f)
      curveTo(65.8f, 80.71f, 50.41f, 65.43f, 50.41f, 46.59f)
      curveTo(50.41f, 27.74f, 65.8f, 12.46f, 84.78f, 12.46f)
      curveTo(103.76f, 12.46f, 119.15f, 27.74f, 119.15f, 46.59f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(111.78f, 66.01f)
      lineTo(113.33f, 68.65f)
      lineTo(115.99f, 70.19f)
      curveTo(116.7f, 70.61f, 116.7f, 71.64f, 115.99f, 72.06f)
      lineTo(113.33f, 73.6f)
      lineTo(111.78f, 76.24f)
      curveTo(111.36f, 76.95f, 110.32f, 76.95f, 109.9f, 76.24f)
      lineTo(108.35f, 73.6f)
      lineTo(105.69f, 72.06f)
      curveTo(104.98f, 71.64f, 104.98f, 70.61f, 105.69f, 70.19f)
      lineTo(108.35f, 68.65f)
      lineTo(109.9f, 66.01f)
      curveTo(110.32f, 65.3f, 111.36f, 65.3f, 111.78f, 66.01f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(69.73f, 17.81f)
      lineTo(70.2f, 18.6f)
      lineTo(70.99f, 19.06f)
      curveTo(71.7f, 19.48f, 71.7f, 20.51f, 70.99f, 20.92f)
      lineTo(70.2f, 21.39f)
      lineTo(69.73f, 22.17f)
      curveTo(69.31f, 22.88f, 68.27f, 22.88f, 67.85f, 22.17f)
      lineTo(67.39f, 21.39f)
      lineTo(66.6f, 20.92f)
      curveTo(65.88f, 20.51f, 65.88f, 19.48f, 66.6f, 19.06f)
      lineTo(67.39f, 18.6f)
      lineTo(67.85f, 17.81f)
      curveTo(68.27f, 17.1f, 69.31f, 17.1f, 69.73f, 17.81f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(38.67f, 50.31f)
      curveTo(38.67f, 23.99f, 60.17f, 2.66f, 86.67f, 2.66f)
      curveTo(99.25f, 2.66f, 110.69f, 7.46f, 119.25f, 15.32f)
      curveTo(110.48f, 5.9f, 97.93f, 0.0f, 84.0f, 0.0f)
      curveTo(57.49f, 0.0f, 36.0f, 21.34f, 36.0f, 47.65f)
      curveTo(36.0f, 61.49f, 41.94f, 73.94f, 51.43f, 82.65f)
      curveTo(43.51f, 74.15f, 38.67f, 62.79f, 38.67f, 50.31f)
      close()
    }
    path(
      fill = linearGradient(
        0.2f to Color(0xFFFFFFFF), 1.0f to Color(0xFF999999),
        start = Offset(33.5f, 49.5f), end = Offset(33.5f, 89.0f)
      ), stroke = null,
      fillAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(16.75f, 89.0f)
      curveTo(12.13f, 89.0f, 8.18f, 87.43f, 4.91f, 84.28f)
      curveTo(1.64f, 81.13f, 0.0f, 77.28f, 0.0f, 72.72f)
      curveTo(0.0f, 68.82f, 1.19f, 65.35f, 3.58f, 62.3f)
      curveTo(5.96f, 59.25f, 9.09f, 57.3f, 12.94f, 56.45f)
      curveTo(14.21f, 51.85f, 16.75f, 48.13f, 20.56f, 45.28f)
      curveTo(24.36f, 42.42f, 28.68f, 41.0f, 33.5f, 41.0f)
      curveTo(39.44f, 41.0f, 44.48f, 43.04f, 48.61f, 47.11f)
      curveTo(52.75f, 51.19f, 54.82f, 56.15f, 54.82f, 62.0f)
      curveTo(58.32f, 62.4f, 61.23f, 63.89f, 63.54f, 66.46f)
      curveTo(65.85f, 69.04f, 67.0f, 72.05f, 67.0f, 75.5f)
      curveTo(67.0f, 79.25f, 65.67f, 82.44f, 63.0f, 85.06f)
      curveTo(60.34f, 87.69f, 57.1f, 89.0f, 53.3f, 89.0f)
      horizontalLineTo(16.75f)
      close()
    }
    path(
      fill = linearGradient(
        0.2f to Color(0xFFFFFFFF), 1.0f to Color(0xFF999999),
        start = Offset(146.14f, 30.31f), end = Offset(146.14f, 55.0f)
      ), stroke = null,
      strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
      strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(135.64f, 55.0f)
      curveTo(132.74f, 55.0f, 130.27f, 54.02f, 128.21f, 52.05f)
      curveTo(126.16f, 50.08f, 125.14f, 47.67f, 125.14f, 44.83f)
      curveTo(125.14f, 42.39f, 125.88f, 40.22f, 127.38f, 38.31f)
      curveTo(128.88f, 36.41f, 130.83f, 35.19f, 133.25f, 34.66f)
      curveTo(134.05f, 31.78f, 135.64f, 29.45f, 138.02f, 27.67f)
      curveTo(140.41f, 25.89f, 143.11f, 25.0f, 146.14f, 25.0f)
      curveTo(149.86f, 25.0f, 153.02f, 26.27f, 155.61f, 28.82f)
      curveTo(158.2f, 31.37f, 159.5f, 34.47f, 159.5f, 38.13f)
      curveTo(161.7f, 38.38f, 163.52f, 39.3f, 164.96f, 40.91f)
      curveTo(166.41f, 42.52f, 167.14f, 44.41f, 167.14f, 46.56f)
      curveTo(167.14f, 48.91f, 166.3f, 50.9f, 164.63f, 52.54f)
      curveTo(162.96f, 54.18f, 160.93f, 55.0f, 158.55f, 55.0f)
      horizontalLineTo(135.64f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFFC93E)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(134.06f, 50.46f)
      lineTo(138.3f, 57.7f)
      lineTo(145.54f, 61.94f)
      curveTo(147.49f, 63.08f, 147.49f, 65.92f, 145.54f, 67.06f)
      lineTo(138.3f, 71.3f)
      lineTo(134.06f, 78.54f)
      curveTo(132.92f, 80.49f, 130.08f, 80.49f, 128.94f, 78.54f)
      lineTo(124.7f, 71.3f)
      lineTo(117.46f, 67.06f)
      curveTo(115.51f, 65.92f, 115.51f, 63.08f, 117.46f, 61.94f)
      lineTo(124.7f, 57.7f)
      lineTo(128.94f, 50.46f)
      curveTo(130.08f, 48.51f, 132.92f, 48.51f, 134.06f, 50.46f)
      close()
    }
  }
}.build()
