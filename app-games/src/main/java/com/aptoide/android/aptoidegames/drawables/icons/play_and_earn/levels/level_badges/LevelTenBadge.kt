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
private fun TestLevelTenBadge() {
  Image(
    imageVector = getLevelTenBadge(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getLevelTenBadge(): ImageVector = ImageVector.Builder(
  name = "LevelTenBadge", defaultWidth = 168.0.dp, defaultHeight = 96.0.dp, viewportWidth
  = 168.0f, viewportHeight = 96.0f
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
  path(
    fill = linearGradient(
      0.0f to Color(0xFFF9B622), 1.0f to Color(0xFFE3B95B), start =
        Offset(51.0f, 14.88f), end = Offset(118.03f, 87.69f)
    ), stroke = null,
    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
    strokeLineMiter = 4.0f, pathFillType = NonZero
  ) {
    moveTo(86.0f, 0.0f)
    lineTo(121.36f, 14.06f)
    lineTo(136.0f, 48.0f)
    lineTo(121.36f, 81.94f)
    lineTo(86.0f, 96.0f)
    lineTo(50.64f, 81.94f)
    lineTo(36.0f, 48.0f)
    lineTo(50.64f, 14.06f)
    lineTo(86.0f, 0.0f)
    close()
  }
  group {
    path(
      fill = SolidColor(Color(0xFFFFEDA3)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(46.32f, 18.97f)
      lineTo(125.5f, 95.18f)
      curveTo(131.58f, 90.97f, 136.64f, 85.46f, 140.23f, 79.07f)
      lineTo(53.5f, -4.36f)
      curveTo(46.87f, -0.91f, 41.15f, 3.95f, 36.77f, 9.81f)
      lineTo(46.32f, 18.97f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFFEDA3)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(32.22f, 19.59f)
      lineTo(117.11f, 100.14f)
      curveTo(119.33f, 99.23f, 121.47f, 98.16f, 123.5f, 96.96f)
      lineTo(35.53f, 13.44f)
      curveTo(34.28f, 15.4f, 33.17f, 17.46f, 32.22f, 19.59f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(122.64f, 14.26f)
      lineTo(112.45f, 24.45f)
      lineTo(111.74f, 23.74f)
      lineTo(121.93f, 13.55f)
      lineTo(122.64f, 14.26f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(136.03f, 49.51f)
      verticalLineTo(50.51f)
      horizontalLineTo(125.0f)
      verticalLineTo(49.51f)
      horizontalLineTo(136.03f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(86.49f, -0.54f)
      verticalLineTo(11.93f)
      horizontalLineTo(85.49f)
      verticalLineTo(-0.54f)
      horizontalLineTo(86.49f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(59.84f, 23.4f)
      lineTo(59.13f, 24.11f)
      lineTo(49.86f, 14.89f)
      lineTo(50.56f, 14.18f)
      lineTo(59.84f, 23.4f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(49.16f, 49.54f)
      verticalLineTo(50.54f)
      horizontalLineTo(34.27f)
      verticalLineTo(49.54f)
      horizontalLineTo(49.16f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(60.22f, 76.52f)
      lineTo(50.2f, 86.55f)
      lineTo(49.5f, 85.84f)
      lineTo(59.52f, 75.82f)
      lineTo(60.22f, 76.52f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(86.5f, 87.0f)
      verticalLineTo(101.82f)
      horizontalLineTo(85.5f)
      verticalLineTo(87.0f)
      horizontalLineTo(86.5f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(121.8f, 85.07f)
      lineTo(121.09f, 85.78f)
      lineTo(113.58f, 78.26f)
      lineTo(114.28f, 77.56f)
      lineTo(121.8f, 85.07f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(121.94f, 15.42f)
      lineTo(121.36f, 14.06f)
      lineTo(86.0f, 0.0f)
      lineTo(50.64f, 14.06f)
      lineTo(36.0f, 48.0f)
      lineTo(50.64f, 81.94f)
      lineTo(52.06f, 82.5f)
      lineTo(38.0f, 49.92f)
      lineTo(52.64f, 15.98f)
      lineTo(88.0f, 1.92f)
      lineTo(121.94f, 15.42f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(87.0f, 10.56f)
      lineTo(114.0f, 21.12f)
      lineTo(127.0f, 48.48f)
      lineTo(115.28f, 76.11f)
      lineTo(86.0f, 87.84f)
      lineTo(57.5f, 76.11f)
      lineTo(46.5f, 48.48f)
      lineTo(58.72f, 21.81f)
      lineTo(87.0f, 10.56f)
      close()
    }
    path(
      fill = linearGradient(
        0.0f to Color(0xFFF9B622), 1.0f to Color(0xFFE3B95B),
        start = Offset(110.5f, 72.0f), end = Offset(54.03f, 41.31f)
      ), stroke = null,
      strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
      strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(86.0f, 9.6f)
      lineTo(114.28f, 20.85f)
      lineTo(126.0f, 48.0f)
      lineTo(114.28f, 75.15f)
      lineTo(86.0f, 86.4f)
      lineTo(57.72f, 75.15f)
      lineTo(46.0f, 48.0f)
      lineTo(57.72f, 20.85f)
      lineTo(86.0f, 9.6f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFF876311)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(114.28f, 20.85f)
      lineTo(126.0f, 48.0f)
      lineTo(114.28f, 75.15f)
      lineTo(86.0f, 86.4f)
      lineTo(57.72f, 75.15f)
      lineTo(46.0f, 48.0f)
      lineTo(57.72f, 20.85f)
      lineTo(86.0f, 9.6f)
      lineTo(114.28f, 20.85f)
      close()
      moveTo(60.01f, 23.05f)
      lineTo(49.25f, 48.0f)
      lineTo(60.01f, 72.95f)
      lineTo(86.0f, 83.28f)
      lineTo(111.99f, 72.95f)
      lineTo(122.75f, 48.0f)
      lineTo(111.99f, 23.05f)
      lineTo(86.0f, 12.72f)
      lineTo(60.01f, 23.05f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(114.83f, 69.65f)
      lineTo(116.43f, 72.29f)
      lineTo(119.17f, 73.83f)
      curveTo(119.91f, 74.25f, 119.91f, 75.28f, 119.17f, 75.69f)
      lineTo(116.43f, 77.24f)
      lineTo(114.83f, 79.88f)
      curveTo(114.4f, 80.58f, 113.33f, 80.58f, 112.89f, 79.88f)
      lineTo(111.29f, 77.24f)
      lineTo(108.55f, 75.69f)
      curveTo(107.82f, 75.28f, 107.82f, 74.25f, 108.55f, 73.83f)
      lineTo(111.29f, 72.29f)
      lineTo(112.89f, 69.65f)
      curveTo(113.33f, 68.94f, 114.4f, 68.94f, 114.83f, 69.65f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(58.49f, 18.51f)
      lineTo(59.32f, 19.86f)
      lineTo(60.72f, 20.65f)
      curveTo(61.09f, 20.86f, 61.09f, 21.38f, 60.72f, 21.6f)
      lineTo(59.32f, 22.38f)
      lineTo(58.49f, 23.73f)
      curveTo(58.27f, 24.09f, 57.73f, 24.09f, 57.51f, 23.73f)
      lineTo(56.68f, 22.38f)
      lineTo(55.28f, 21.6f)
      curveTo(54.91f, 21.38f, 54.91f, 20.86f, 55.28f, 20.65f)
      lineTo(56.68f, 19.86f)
      lineTo(57.51f, 18.51f)
      curveTo(57.73f, 18.15f, 58.27f, 18.15f, 58.49f, 18.51f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFF876311)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(78.04f, 29.15f)
      verticalLineTo(66.77f)
      horizontalLineTo(70.3f)
      verticalLineTo(37.71f)
      lineTo(61.01f, 40.55f)
      verticalLineTo(34.7f)
      lineTo(77.21f, 29.15f)
      horizontalLineTo(78.04f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFF876311)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(110.49f, 44.7f)
      verticalLineTo(51.2f)
      curveTo(110.49f, 54.02f, 110.17f, 56.45f, 109.55f, 58.49f)
      curveTo(108.92f, 60.52f, 108.01f, 62.19f, 106.83f, 63.49f)
      curveTo(105.67f, 64.78f, 104.28f, 65.74f, 102.67f, 66.36f)
      curveTo(101.06f, 66.98f, 99.27f, 67.28f, 97.3f, 67.28f)
      curveTo(95.72f, 67.28f, 94.26f, 67.1f, 92.9f, 66.72f)
      curveTo(91.53f, 66.32f, 90.31f, 65.71f, 89.22f, 64.89f)
      curveTo(88.14f, 64.06f, 87.21f, 63.02f, 86.42f, 61.77f)
      curveTo(85.65f, 60.5f, 85.06f, 58.98f, 84.65f, 57.23f)
      curveTo(84.24f, 55.48f, 84.03f, 53.47f, 84.03f, 51.2f)
      verticalLineTo(44.7f)
      curveTo(84.03f, 41.88f, 84.35f, 39.47f, 84.97f, 37.46f)
      curveTo(85.62f, 35.43f, 86.52f, 33.77f, 87.69f, 32.48f)
      curveTo(88.87f, 31.19f, 90.26f, 30.25f, 91.88f, 29.64f)
      curveTo(93.49f, 29.03f, 95.28f, 28.72f, 97.25f, 28.72f)
      curveTo(98.82f, 28.72f, 100.28f, 28.91f, 101.62f, 29.31f)
      curveTo(102.98f, 29.69f, 104.21f, 30.28f, 105.3f, 31.09f)
      curveTo(106.39f, 31.9f, 107.33f, 32.94f, 108.1f, 34.21f)
      curveTo(108.87f, 35.46f, 109.46f, 36.97f, 109.87f, 38.72f)
      curveTo(110.28f, 40.45f, 110.49f, 42.45f, 110.49f, 44.7f)
      close()
      moveTo(102.72f, 52.18f)
      verticalLineTo(43.69f)
      curveTo(102.72f, 42.34f, 102.64f, 41.15f, 102.48f, 40.14f)
      curveTo(102.34f, 39.12f, 102.12f, 38.26f, 101.81f, 37.56f)
      curveTo(101.51f, 36.84f, 101.13f, 36.25f, 100.68f, 35.81f)
      curveTo(100.24f, 35.36f, 99.73f, 35.03f, 99.15f, 34.83f)
      curveTo(98.58f, 34.62f, 97.94f, 34.52f, 97.25f, 34.52f)
      curveTo(96.37f, 34.52f, 95.59f, 34.68f, 94.91f, 35.01f)
      curveTo(94.25f, 35.33f, 93.68f, 35.86f, 93.22f, 36.58f)
      curveTo(92.75f, 37.28f, 92.39f, 38.23f, 92.14f, 39.42f)
      curveTo(91.91f, 40.58f, 91.79f, 42.01f, 91.79f, 43.69f)
      verticalLineTo(52.18f)
      curveTo(91.79f, 53.53f, 91.87f, 54.73f, 92.01f, 55.76f)
      curveTo(92.17f, 56.79f, 92.4f, 57.68f, 92.71f, 58.42f)
      curveTo(93.03f, 59.14f, 93.41f, 59.73f, 93.84f, 60.19f)
      curveTo(94.28f, 60.64f, 94.79f, 60.97f, 95.37f, 61.17f)
      curveTo(95.96f, 61.38f, 96.6f, 61.48f, 97.3f, 61.48f)
      curveTo(98.16f, 61.48f, 98.92f, 61.32f, 99.58f, 60.99f)
      curveTo(100.26f, 60.65f, 100.84f, 60.12f, 101.3f, 59.4f)
      curveTo(101.79f, 58.66f, 102.14f, 57.69f, 102.38f, 56.51f)
      curveTo(102.61f, 55.32f, 102.72f, 53.88f, 102.72f, 52.18f)
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
