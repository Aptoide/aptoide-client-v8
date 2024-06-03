package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

@Preview
@Composable
fun TestBonus() {
  Image(
    imageVector = getBonus(Color.Cyan, Color.DarkGray),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getBonus(
  iconColor: Color,
  textColor: Color,
): ImageVector = ImageVector.Builder(
  name = "Bonus",
  defaultWidth = 325.0.dp,
  defaultHeight = 88.0.dp,
  viewportWidth = 325.0f, viewportHeight = 88.0f
).apply {
  path(
    fill = SolidColor(iconColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(216.0f, 56.0f)
    horizontalLineToRelative(32.0f)
    verticalLineToRelative(7.0f)
    horizontalLineToRelative(-32.0f)
    close()
  }
  path(
    fill = SolidColor(textColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(224.0f, 31.0f)
    lineToRelative(0.0f, -15.0f)
    lineToRelative(8.0f, -0.0f)
    lineToRelative(0.0f, 15.0f)
    close()
  }
  path(
    fill = SolidColor(textColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(88.0f, 64.0f)
    horizontalLineToRelative(24.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-24.0f)
    close()
  }
  path(
    fill = SolidColor(iconColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(112.0f, 0.0f)
    horizontalLineToRelative(8.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-8.0f)
    close()
  }
  path(
    fill = SolidColor(iconColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = EvenOdd
  ) {
    moveTo(160.3f, 21.8f)
    horizontalLineTo(167.7f)
    verticalLineTo(14.4f)
    lineTo(175.1f, 14.4f)
    verticalLineTo(21.8f)
    lineTo(171.4f, 21.8f)
    horizontalLineTo(156.6f)
    verticalLineTo(29.2f)
    horizontalLineTo(160.3f)
    lineTo(160.3f, 36.6f)
    horizontalLineTo(167.7f)
    lineTo(167.7f, 29.2f)
    horizontalLineTo(171.4f)
    lineTo(186.2f, 29.2f)
    lineTo(186.2f, 29.2f)
    horizontalLineTo(201.0f)
    lineTo(201.0f, 21.8f)
    horizontalLineTo(186.2f)
    lineTo(186.2f, 21.8f)
    lineTo(182.5f, 21.8f)
    lineTo(175.1f, 7.001f)
    verticalLineTo(7.0f)
    lineTo(175.1f, 7.0f)
    lineTo(167.7f, 7.0f)
    horizontalLineTo(160.3f)
    lineTo(152.9f, 7.0f)
    lineTo(145.5f, 21.8f)
    lineTo(152.9f, 21.8f)
    lineTo(152.9f, 14.4f)
    lineTo(160.3f, 14.4f)
    verticalLineTo(21.8f)
    close()
    moveTo(156.6f, 36.6f)
    horizontalLineTo(141.8f)
    verticalLineTo(36.6f)
    lineTo(134.4f, 36.6f)
    lineTo(134.4f, 29.2f)
    horizontalLineTo(141.8f)
    horizontalLineTo(141.8f)
    horizontalLineTo(156.6f)
    lineTo(156.6f, 21.8f)
    horizontalLineTo(141.8f)
    horizontalLineTo(141.8f)
    horizontalLineTo(127.0f)
    lineTo(127.0f, 29.2f)
    horizontalLineTo(127.0f)
    lineTo(127.0f, 36.6f)
    lineTo(127.0f, 36.6f)
    lineTo(127.0f, 44.0f)
    horizontalLineTo(134.4f)
    lineTo(134.4f, 58.8f)
    lineTo(134.4f, 73.6f)
    lineTo(134.4f, 81.0f)
    horizontalLineTo(149.2f)
    horizontalLineTo(149.2f)
    horizontalLineTo(164.0f)
    horizontalLineTo(164.0f)
    horizontalLineTo(178.8f)
    lineTo(193.6f, 81.0f)
    lineTo(193.6f, 73.6f)
    verticalLineTo(58.8f)
    lineTo(184.788f, 58.8f)
    horizontalLineTo(193.6f)
    lineTo(193.6f, 44.0f)
    lineTo(201.0f, 44.0f)
    lineTo(201.0f, 36.6f)
    lineTo(201.0f, 36.6f)
    verticalLineTo(29.2f)
    horizontalLineTo(193.6f)
    lineTo(193.6f, 36.6f)
    lineTo(186.2f, 36.6f)
    lineTo(186.2f, 36.6f)
    horizontalLineTo(171.4f)
    verticalLineTo(36.6f)
    horizontalLineTo(156.6f)
    verticalLineTo(44.0f)
    horizontalLineTo(156.6f)
    lineTo(156.6f, 36.6f)
    close()
  }
  path(
    fill = SolidColor(textColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(148.819f, 48.56f)
    verticalLineTo(41.6f)
    horizontalLineTo(150.451f)
    verticalLineTo(47.996f)
    lineTo(151.063f, 48.608f)
    horizontalLineTo(153.415f)
    lineTo(154.027f, 47.996f)
    verticalLineTo(41.6f)
    horizontalLineTo(155.659f)
    verticalLineTo(48.56f)
    lineTo(154.219f, 50.0f)
    horizontalLineTo(150.259f)
    lineTo(148.819f, 48.56f)
    close()
    moveTo(157.161f, 44.024f)
    horizontalLineTo(158.625f)
    verticalLineTo(44.9f)
    lineTo(159.501f, 44.024f)
    horizontalLineTo(161.829f)
    lineTo(163.065f, 45.272f)
    verticalLineTo(48.752f)
    lineTo(161.829f, 50.0f)
    horizontalLineTo(159.513f)
    lineTo(158.745f, 49.364f)
    verticalLineTo(52.544f)
    horizontalLineTo(157.161f)
    verticalLineTo(44.024f)
    close()
    moveTo(161.061f, 48.656f)
    lineTo(161.481f, 48.236f)
    verticalLineTo(45.788f)
    lineTo(161.061f, 45.368f)
    horizontalLineTo(159.705f)
    lineTo(158.745f, 46.34f)
    verticalLineTo(47.804f)
    lineTo(159.693f, 48.656f)
    horizontalLineTo(161.061f)
    close()
    moveTo(169.618f, 42.98f)
    horizontalLineTo(167.11f)
    verticalLineTo(41.6f)
    horizontalLineTo(173.758f)
    verticalLineTo(42.98f)
    horizontalLineTo(171.25f)
    verticalLineTo(50.0f)
    horizontalLineTo(169.618f)
    verticalLineTo(42.98f)
    close()
    moveTo(173.459f, 48.752f)
    verticalLineTo(45.272f)
    lineTo(174.695f, 44.024f)
    horizontalLineTo(178.127f)
    lineTo(179.363f, 45.272f)
    verticalLineTo(48.752f)
    lineTo(178.127f, 50.0f)
    horizontalLineTo(174.695f)
    lineTo(173.459f, 48.752f)
    close()
    moveTo(177.359f, 48.68f)
    lineTo(177.779f, 48.26f)
    verticalLineTo(45.764f)
    lineTo(177.359f, 45.344f)
    horizontalLineTo(175.463f)
    lineTo(175.043f, 45.764f)
    verticalLineTo(48.26f)
    lineTo(175.463f, 48.68f)
    horizontalLineTo(177.359f)
    close()
  }
  path(
    fill = SolidColor(textColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(145.991f, 72.3f)
    horizontalLineTo(150.82f)
    lineTo(151.92f, 73.4f)
    verticalLineTo(75.556f)
    lineTo(151.48f, 76.007f)
    lineTo(152.261f, 76.799f)
    verticalLineTo(78.812f)
    lineTo(151.073f, 80.0f)
    horizontalLineTo(145.991f)
    verticalLineTo(72.3f)
    close()
    moveTo(150.05f, 75.523f)
    lineTo(150.446f, 75.127f)
    verticalLineTo(73.939f)
    lineTo(150.061f, 73.554f)
    horizontalLineTo(147.454f)
    verticalLineTo(75.523f)
    horizontalLineTo(150.05f)
    close()
    moveTo(150.314f, 78.746f)
    lineTo(150.787f, 78.273f)
    verticalLineTo(77.195f)
    lineTo(150.314f, 76.722f)
    horizontalLineTo(147.454f)
    verticalLineTo(78.746f)
    horizontalLineTo(150.314f)
    close()
    moveTo(153.251f, 78.735f)
    verticalLineTo(73.565f)
    lineTo(154.516f, 72.3f)
    horizontalLineTo(158.476f)
    lineTo(159.741f, 73.565f)
    verticalLineTo(78.735f)
    lineTo(158.476f, 80.0f)
    horizontalLineTo(154.516f)
    lineTo(153.251f, 78.735f)
    close()
    moveTo(157.684f, 78.724f)
    lineTo(158.245f, 78.163f)
    verticalLineTo(74.137f)
    lineTo(157.684f, 73.576f)
    horizontalLineTo(155.308f)
    lineTo(154.747f, 74.137f)
    verticalLineTo(78.163f)
    lineTo(155.308f, 78.724f)
    horizontalLineTo(157.684f)
    close()
    moveTo(161.063f, 72.3f)
    horizontalLineTo(162.405f)
    lineTo(165.837f, 77.525f)
    horizontalLineTo(165.859f)
    verticalLineTo(72.3f)
    horizontalLineTo(167.3f)
    verticalLineTo(80.0f)
    horizontalLineTo(165.958f)
    lineTo(162.526f, 74.786f)
    horizontalLineTo(162.504f)
    verticalLineTo(80.0f)
    horizontalLineTo(161.063f)
    verticalLineTo(72.3f)
    close()
    moveTo(168.733f, 78.68f)
    verticalLineTo(72.3f)
    horizontalLineTo(170.229f)
    verticalLineTo(78.163f)
    lineTo(170.79f, 78.724f)
    horizontalLineTo(172.946f)
    lineTo(173.507f, 78.163f)
    verticalLineTo(72.3f)
    horizontalLineTo(175.003f)
    verticalLineTo(78.68f)
    lineTo(173.683f, 80.0f)
    horizontalLineTo(170.053f)
    lineTo(168.733f, 78.68f)
    close()
    moveTo(176.27f, 78.812f)
    verticalLineTo(77.712f)
    horizontalLineTo(177.744f)
    verticalLineTo(78.361f)
    lineTo(178.107f, 78.724f)
    horizontalLineTo(180.384f)
    lineTo(180.758f, 78.35f)
    verticalLineTo(77.074f)
    lineTo(180.395f, 76.711f)
    horizontalLineTo(177.48f)
    lineTo(176.292f, 75.523f)
    verticalLineTo(73.488f)
    lineTo(177.48f, 72.3f)
    horizontalLineTo(180.956f)
    lineTo(182.144f, 73.488f)
    verticalLineTo(74.599f)
    horizontalLineTo(180.67f)
    verticalLineTo(73.939f)
    lineTo(180.307f, 73.576f)
    horizontalLineTo(178.129f)
    lineTo(177.766f, 73.939f)
    verticalLineTo(75.072f)
    lineTo(178.129f, 75.435f)
    horizontalLineTo(181.044f)
    lineTo(182.232f, 76.623f)
    verticalLineTo(78.79f)
    lineTo(181.022f, 80.0f)
    horizontalLineTo(177.458f)
    lineTo(176.27f, 78.812f)
    close()
  }
  path(
    fill = SolidColor(textColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(175.923f, 66.292f)
    verticalLineTo(64.42f)
    lineTo(176.643f, 63.7f)
    horizontalLineTo(178.208f)
    lineTo(178.929f, 64.42f)
    verticalLineTo(66.292f)
    lineTo(178.208f, 67.012f)
    horizontalLineTo(176.643f)
    lineTo(175.923f, 66.292f)
    close()
    moveTo(180.612f, 63.7f)
    horizontalLineTo(181.836f)
    lineTo(178.389f, 70.0f)
    horizontalLineTo(177.165f)
    lineTo(180.612f, 63.7f)
    close()
    moveTo(177.687f, 66.112f)
    lineTo(177.885f, 65.914f)
    verticalLineTo(64.798f)
    lineTo(177.687f, 64.6f)
    horizontalLineTo(177.165f)
    lineTo(176.967f, 64.798f)
    verticalLineTo(65.914f)
    lineTo(177.165f, 66.112f)
    horizontalLineTo(177.687f)
    close()
    moveTo(180.072f, 69.28f)
    verticalLineTo(67.408f)
    lineTo(180.792f, 66.688f)
    horizontalLineTo(182.358f)
    lineTo(183.078f, 67.408f)
    verticalLineTo(69.28f)
    lineTo(182.358f, 70.0f)
    horizontalLineTo(180.792f)
    lineTo(180.072f, 69.28f)
    close()
    moveTo(181.836f, 69.1f)
    lineTo(182.034f, 68.902f)
    verticalLineTo(67.786f)
    lineTo(181.836f, 67.588f)
    horizontalLineTo(181.314f)
    lineTo(181.116f, 67.786f)
    verticalLineTo(68.902f)
    lineTo(181.314f, 69.1f)
    horizontalLineTo(181.836f)
    close()
  }
  path(
    fill = SolidColor(textColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(146.936f, 66.328f)
    lineTo(155.576f, 58.48f)
    verticalLineTo(56.776f)
    lineTo(154.784f, 55.984f)
    horizontalLineTo(150.896f)
    lineTo(150.104f, 56.776f)
    verticalLineTo(58.48f)
    horizontalLineTo(146.84f)
    verticalLineTo(55.792f)
    lineTo(149.432f, 53.2f)
    horizontalLineTo(156.248f)
    lineTo(158.84f, 55.792f)
    verticalLineTo(59.44f)
    lineTo(150.32f, 67.024f)
    verticalLineTo(67.264f)
    horizontalLineTo(158.936f)
    verticalLineTo(70.0f)
    horizontalLineTo(146.936f)
    verticalLineTo(66.328f)
    close()
    moveTo(161.101f, 67.216f)
    verticalLineTo(55.984f)
    lineTo(163.885f, 53.2f)
    horizontalLineTo(171.325f)
    lineTo(174.109f, 55.984f)
    verticalLineTo(67.216f)
    lineTo(171.325f, 70.0f)
    horizontalLineTo(163.885f)
    lineTo(161.101f, 67.216f)
    close()
    moveTo(169.789f, 67.216f)
    lineTo(170.845f, 66.16f)
    verticalLineTo(57.04f)
    lineTo(169.789f, 55.984f)
    horizontalLineTo(165.421f)
    lineTo(164.365f, 57.04f)
    verticalLineTo(66.16f)
    lineTo(165.421f, 67.216f)
    horizontalLineTo(169.789f)
    close()
  }
}.build()
