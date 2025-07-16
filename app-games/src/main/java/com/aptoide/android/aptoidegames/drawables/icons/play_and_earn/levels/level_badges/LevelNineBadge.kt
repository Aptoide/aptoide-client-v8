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
private fun TestLevelNineBadge() {
  Image(
    imageVector = getLevelNineBadge(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getLevelNineBadge(): ImageVector = ImageVector.Builder(
  name = "LevelNineBadge", defaultWidth = 168.0.dp, defaultHeight = 96.0.dp, viewportWidth =
    168.0f, viewportHeight = 96.0f
).apply {
  path(
    fill = linearGradient(
      0.2f to Color(0xFFFFFFFF), 1.0f to Color(0xFF999999), start =
        Offset(139.57f, 73.42f), end = Offset(139.57f, 90.18f)
    ), stroke = null, fillAlpha
    = 0.9f, strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
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
      0.63f, strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
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
      0.54f, strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
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
    ), stroke = null, fillAlpha =
      0.9f, strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
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
      fill = SolidColor(Color(0xFFFFEDA3)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
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
      fill = SolidColor(Color(0xFFFFEDA3)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(32.22f, 19.59f)
      lineTo(117.11f, 100.14f)
      curveTo(119.33f, 99.23f, 121.47f, 98.16f, 123.5f, 96.96f)
      lineTo(35.53f, 13.44f)
      curveTo(34.28f, 15.4f, 33.17f, 17.46f, 32.22f, 19.59f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(122.64f, 14.26f)
      lineTo(112.45f, 24.45f)
      lineTo(111.74f, 23.74f)
      lineTo(121.93f, 13.55f)
      lineTo(122.64f, 14.26f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(136.03f, 49.51f)
      verticalLineTo(50.51f)
      horizontalLineTo(125.0f)
      verticalLineTo(49.51f)
      horizontalLineTo(136.03f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(86.49f, -0.54f)
      verticalLineTo(11.93f)
      horizontalLineTo(85.49f)
      verticalLineTo(-0.54f)
      horizontalLineTo(86.49f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(59.84f, 23.4f)
      lineTo(59.13f, 24.11f)
      lineTo(49.86f, 14.89f)
      lineTo(50.56f, 14.18f)
      lineTo(59.84f, 23.4f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(49.16f, 49.54f)
      verticalLineTo(50.54f)
      horizontalLineTo(34.27f)
      verticalLineTo(49.54f)
      horizontalLineTo(49.16f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(60.22f, 76.52f)
      lineTo(50.2f, 86.55f)
      lineTo(49.5f, 85.84f)
      lineTo(59.52f, 75.82f)
      lineTo(60.22f, 76.52f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(86.5f, 87.0f)
      verticalLineTo(101.82f)
      horizontalLineTo(85.5f)
      verticalLineTo(87.0f)
      horizontalLineTo(86.5f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(121.8f, 85.07f)
      lineTo(121.09f, 85.78f)
      lineTo(113.58f, 78.26f)
      lineTo(114.28f, 77.56f)
      lineTo(121.8f, 85.07f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
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
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
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
      fillAlpha = 0.9f, strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap
      = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType =
        NonZero
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
      fill = SolidColor(Color(0xFF876311)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
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
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
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
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
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
      fill = SolidColor(Color(0xFF876311)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(77.91f, 61.04f)
      horizontalLineTo(78.46f)
      curveTo(80.81f, 61.04f, 82.82f, 60.77f, 84.5f, 60.22f)
      curveTo(86.2f, 59.66f, 87.59f, 58.86f, 88.69f, 57.81f)
      curveTo(89.78f, 56.76f, 90.59f, 55.47f, 91.12f, 53.95f)
      curveTo(91.65f, 52.42f, 91.91f, 50.67f, 91.91f, 48.72f)
      verticalLineTo(40.96f)
      curveTo(91.91f, 39.48f, 91.75f, 38.17f, 91.44f, 37.05f)
      curveTo(91.15f, 35.92f, 90.72f, 35.0f, 90.15f, 34.27f)
      curveTo(89.61f, 33.52f, 88.96f, 32.95f, 88.22f, 32.58f)
      curveTo(87.5f, 32.2f, 86.7f, 32.02f, 85.82f, 32.02f)
      curveTo(84.88f, 32.02f, 84.05f, 32.24f, 83.33f, 32.69f)
      curveTo(82.6f, 33.12f, 81.99f, 33.71f, 81.48f, 34.46f)
      curveTo(80.99f, 35.21f, 80.61f, 36.07f, 80.34f, 37.05f)
      curveTo(80.08f, 38.01f, 79.96f, 39.01f, 79.96f, 40.06f)
      curveTo(79.96f, 41.11f, 80.08f, 42.11f, 80.34f, 43.07f)
      curveTo(80.59f, 44.01f, 80.97f, 44.84f, 81.48f, 45.57f)
      curveTo(81.99f, 46.28f, 82.62f, 46.86f, 83.38f, 47.29f)
      curveTo(84.15f, 47.7f, 85.04f, 47.91f, 86.08f, 47.91f)
      curveTo(87.08f, 47.91f, 87.96f, 47.73f, 88.75f, 47.37f)
      curveTo(89.55f, 47.0f, 90.22f, 46.51f, 90.77f, 45.91f)
      curveTo(91.33f, 45.31f, 91.76f, 44.64f, 92.06f, 43.91f)
      curveTo(92.37f, 43.18f, 92.53f, 42.44f, 92.53f, 41.69f)
      lineTo(95.31f, 43.15f)
      curveTo(95.31f, 44.47f, 95.02f, 45.76f, 94.43f, 47.03f)
      curveTo(93.84f, 48.31f, 93.02f, 49.47f, 91.97f, 50.52f)
      curveTo(90.93f, 51.55f, 89.73f, 52.38f, 88.37f, 53.0f)
      curveTo(87.0f, 53.62f, 85.53f, 53.92f, 83.97f, 53.92f)
      curveTo(81.98f, 53.92f, 80.21f, 53.58f, 78.67f, 52.88f)
      curveTo(77.13f, 52.17f, 75.82f, 51.2f, 74.74f, 49.96f)
      curveTo(73.69f, 48.7f, 72.89f, 47.24f, 72.34f, 45.57f)
      curveTo(71.79f, 43.9f, 71.52f, 42.09f, 71.52f, 40.14f)
      curveTo(71.52f, 38.16f, 71.85f, 36.29f, 72.52f, 34.55f)
      curveTo(73.2f, 32.8f, 74.17f, 31.27f, 75.42f, 29.93f)
      curveTo(76.69f, 28.6f, 78.2f, 27.56f, 79.96f, 26.81f)
      curveTo(81.73f, 26.04f, 83.71f, 25.66f, 85.88f, 25.66f)
      curveTo(88.04f, 25.66f, 90.02f, 26.06f, 91.79f, 26.87f)
      curveTo(93.57f, 27.67f, 95.09f, 28.82f, 96.36f, 30.3f)
      curveTo(97.63f, 31.76f, 98.61f, 33.52f, 99.29f, 35.56f)
      curveTo(100.0f, 37.6f, 100.35f, 39.87f, 100.35f, 42.37f)
      verticalLineTo(45.18f)
      curveTo(100.35f, 47.84f, 100.04f, 50.32f, 99.44f, 52.6f)
      curveTo(98.85f, 54.89f, 97.96f, 56.96f, 96.77f, 58.82f)
      curveTo(95.6f, 60.66f, 94.14f, 62.23f, 92.38f, 63.54f)
      curveTo(90.64f, 64.86f, 88.62f, 65.86f, 86.31f, 66.55f)
      curveTo(84.01f, 67.25f, 81.43f, 67.59f, 78.58f, 67.59f)
      horizontalLineTo(77.91f)
      verticalLineTo(61.04f)
      close()
    }
    path(
      fill = linearGradient(
        0.2f to Color(0xFFFFFFFF), 1.0f to Color(0xFF999999),
        start = Offset(33.5f, 49.5f), end = Offset(33.5f, 89.0f)
      ), stroke = null,
      fillAlpha = 0.81f, strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap
      = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType =
        NonZero
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
      fillAlpha = 0.9f, strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap
      = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType =
        NonZero
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
      fill = SolidColor(Color(0xFFFFC93E)), stroke = null, fillAlpha = 0.9f,
      strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
      strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
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
