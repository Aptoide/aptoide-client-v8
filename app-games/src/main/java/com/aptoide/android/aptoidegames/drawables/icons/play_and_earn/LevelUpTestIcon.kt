package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn

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
private fun TestLevelUpTestIcon() {
  Image(
    imageVector = getLevelUpTestIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getLevelUpTestIcon(): ImageVector = ImageVector.Builder(
  name = "LevelUpTestIcon",
  defaultWidth = 168.0.dp,
  defaultHeight = 118.0.dp,
  viewportWidth = 168.0f,
  viewportHeight = 118.0f
).apply {
  path(
    fill = linearGradient(
      0.2f to Color(0xFFFFFFFF),
      1.0f to Color(0xFF999999),
      start =
        Offset(139.57f, 73.42f),
      end = Offset(139.57f, 90.18f)
    ),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
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
    ),
    stroke = null,
    fillAlpha =
      0.7f,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
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
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.2f,
      strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
      strokeLineMiter = 4.0f, pathFillType = NonZero
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
      fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.2f,
      strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
      strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(39.86f, 28.91f)
      lineTo(102.88f, 91.48f)
      curveTo(105.03f, 90.56f, 107.1f, 89.49f, 109.07f, 88.29f)
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
      fill = SolidColor(Color(0xFFF58932)), stroke = null, strokeLineWidth = 0.0f,
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
      fill = SolidColor(Color(0xFFC04D07)), stroke = null, strokeLineWidth = 0.0f,
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
      fill = SolidColor(Color(0xFFC04D07)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(89.81f, 26.88f)
      verticalLineTo(67.91f)
      horizontalLineTo(81.63f)
      verticalLineTo(36.22f)
      lineTo(71.8f, 39.31f)
      verticalLineTo(32.93f)
      lineTo(88.93f, 26.88f)
      horizontalLineTo(89.81f)
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
      fill = SolidColor(Color(0xFF18181E)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(133.94f, 83.0f)
      verticalLineTo(117.0f)
      horizontalLineTo(33.5f)
      verticalLineTo(83.0f)
      horizontalLineTo(133.94f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFF58932)), stroke = null, fillAlpha = 0.2f,
      strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
      strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(133.94f, 83.0f)
      verticalLineTo(117.0f)
      horizontalLineTo(33.5f)
      verticalLineTo(83.0f)
      horizontalLineTo(133.94f)
      close()
    }
    path(
      fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFC04D07)),
      strokeLineWidth = 2.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
      strokeLineMiter = 4.0f, pathFillType = NonZero
    ) {
      moveTo(133.94f, 83.0f)
      verticalLineTo(117.0f)
      horizontalLineTo(33.5f)
      verticalLineTo(83.0f)
      horizontalLineTo(133.94f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFC04D07)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(48.5f, 96.89f)
      lineTo(54.72f, 93.0f)
      lineTo(60.94f, 96.89f)
      verticalLineTo(103.11f)
      lineTo(54.72f, 107.0f)
      lineTo(48.5f, 103.11f)
      verticalLineTo(96.89f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFFFC93E)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(54.72f, 95.33f)
      verticalLineTo(93.0f)
      lineTo(48.5f, 96.89f)
      verticalLineTo(103.11f)
      lineTo(54.72f, 107.0f)
      verticalLineTo(104.67f)
      lineTo(50.83f, 101.94f)
      verticalLineTo(98.06f)
      lineTo(54.72f, 95.33f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFF58932)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(54.72f, 95.33f)
      verticalLineTo(93.0f)
      lineTo(60.94f, 96.89f)
      verticalLineTo(103.11f)
      lineTo(54.72f, 107.0f)
      verticalLineTo(104.67f)
      lineTo(58.61f, 101.94f)
      verticalLineTo(98.06f)
      lineTo(54.72f, 95.33f)
      close()
    }
    path(
      fill = SolidColor(Color(0xFFF58932)), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(65.99f, 93.8f)
      horizontalLineTo(73.01f)
      lineTo(74.61f, 95.4f)
      verticalLineTo(98.54f)
      lineTo(73.97f, 99.19f)
      lineTo(75.11f, 100.34f)
      verticalLineTo(103.27f)
      lineTo(73.38f, 105.0f)
      horizontalLineTo(65.99f)
      verticalLineTo(93.8f)
      close()
      moveTo(71.89f, 98.49f)
      lineTo(72.47f, 97.91f)
      verticalLineTo(96.18f)
      lineTo(71.91f, 95.62f)
      horizontalLineTo(68.11f)
      verticalLineTo(98.49f)
      horizontalLineTo(71.89f)
      close()
      moveTo(72.27f, 103.18f)
      lineTo(72.96f, 102.49f)
      verticalLineTo(100.92f)
      lineTo(72.27f, 100.23f)
      horizontalLineTo(68.11f)
      verticalLineTo(103.18f)
      horizontalLineTo(72.27f)
      close()
      moveTo(76.62f, 97.03f)
      horizontalLineTo(78.58f)
      verticalLineTo(98.36f)
      lineTo(79.86f, 97.03f)
      horizontalLineTo(82.13f)
      verticalLineTo(98.82f)
      horizontalLineTo(80.29f)
      lineTo(78.74f, 100.42f)
      verticalLineTo(105.0f)
      horizontalLineTo(76.62f)
      verticalLineTo(97.03f)
      close()
      moveTo(82.6f, 103.34f)
      verticalLineTo(98.7f)
      lineTo(84.25f, 97.03f)
      horizontalLineTo(88.83f)
      lineTo(90.48f, 98.7f)
      verticalLineTo(103.34f)
      lineTo(88.83f, 105.0f)
      horizontalLineTo(84.25f)
      lineTo(82.6f, 103.34f)
      close()
      moveTo(87.8f, 103.24f)
      lineTo(88.36f, 102.68f)
      verticalLineTo(99.35f)
      lineTo(87.8f, 98.79f)
      horizontalLineTo(85.28f)
      lineTo(84.72f, 99.35f)
      verticalLineTo(102.68f)
      lineTo(85.28f, 103.24f)
      horizontalLineTo(87.8f)
      close()
      moveTo(92.23f, 97.03f)
      horizontalLineTo(94.19f)
      verticalLineTo(98.36f)
      lineTo(95.51f, 97.03f)
      horizontalLineTo(98.09f)
      lineTo(99.87f, 98.81f)
      verticalLineTo(105.0f)
      horizontalLineTo(97.75f)
      verticalLineTo(99.38f)
      lineTo(97.21f, 98.82f)
      horizontalLineTo(95.87f)
      lineTo(94.35f, 100.36f)
      verticalLineTo(105.0f)
      horizontalLineTo(92.23f)
      verticalLineTo(97.03f)
      close()
      moveTo(101.14f, 103.37f)
      lineTo(105.57f, 98.78f)
      verticalLineTo(98.74f)
      horizontalLineTo(101.3f)
      verticalLineTo(97.03f)
      horizontalLineTo(108.31f)
      verticalLineTo(98.66f)
      lineTo(103.88f, 103.26f)
      verticalLineTo(103.29f)
      horizontalLineTo(108.31f)
      verticalLineTo(105.0f)
      horizontalLineTo(101.14f)
      verticalLineTo(103.37f)
      close()
      moveTo(109.43f, 103.37f)
      verticalLineTo(98.7f)
      lineTo(111.08f, 97.03f)
      horizontalLineTo(115.58f)
      lineTo(117.24f, 98.7f)
      verticalLineTo(101.72f)
      horizontalLineTo(111.54f)
      verticalLineTo(102.74f)
      lineTo(112.06f, 103.27f)
      horizontalLineTo(114.67f)
      lineTo(115.14f, 102.78f)
      verticalLineTo(102.36f)
      horizontalLineTo(117.22f)
      verticalLineTo(103.4f)
      lineTo(115.64f, 105.0f)
      horizontalLineTo(111.05f)
      lineTo(109.43f, 103.37f)
      close()
      moveTo(115.13f, 100.25f)
      verticalLineTo(99.32f)
      lineTo(114.58f, 98.76f)
      horizontalLineTo(112.09f)
      lineTo(111.54f, 99.32f)
      verticalLineTo(100.25f)
      horizontalLineTo(115.13f)
      close()
    }
  }
}.build()
