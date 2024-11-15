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
fun TestPromotionBonusIcon() {
  Image(
    imageVector = getPromotionBonusIcon(Color.Green, Color.Black, Color.White),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPromotionBonusIcon(
  color1: Color,
  color2: Color,
  color3: Color
): ImageVector = ImageVector.Builder(
  name = "Promotionbonusicon",
  defaultWidth = 136.0.dp,
  defaultHeight = 96.0.dp,
  viewportWidth = 136.0f,
  viewportHeight = 96.0f
).apply {
  path(
    fill = SolidColor(color1),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = EvenOdd
  ) {
    moveTo(52.3f, 21.8f)
    horizontalLineTo(59.7f)
    verticalLineTo(14.4f)
    lineTo(67.1f, 14.4f)
    verticalLineTo(21.8f)
    lineTo(63.4f, 21.8f)
    horizontalLineTo(48.6f)
    verticalLineTo(29.2f)
    horizontalLineTo(52.3f)
    lineTo(52.3f, 36.6f)
    horizontalLineTo(59.7f)
    lineTo(59.7f, 29.2f)
    horizontalLineTo(63.4f)
    lineTo(78.2f, 29.2f)
    lineTo(78.2f, 29.2f)
    horizontalLineTo(93.0f)
    lineTo(93.0f, 21.8f)
    horizontalLineTo(78.2f)
    lineTo(78.2f, 21.8f)
    lineTo(74.5f, 21.8f)
    lineTo(67.1f, 7.001f)
    verticalLineTo(7.0f)
    lineTo(67.1f, 7.0f)
    lineTo(59.7f, 7.0f)
    horizontalLineTo(52.3f)
    lineTo(44.9f, 7.0f)
    lineTo(37.5f, 21.8f)
    lineTo(44.9f, 21.8f)
    lineTo(44.9f, 14.4f)
    lineTo(52.3f, 14.4f)
    verticalLineTo(21.8f)
    close()
    moveTo(48.6f, 36.6f)
    horizontalLineTo(33.8f)
    verticalLineTo(36.6f)
    lineTo(26.4f, 36.6f)
    lineTo(26.4f, 29.2f)
    horizontalLineTo(33.8f)
    horizontalLineTo(33.8f)
    horizontalLineTo(48.6f)
    lineTo(48.6f, 21.8f)
    horizontalLineTo(33.8f)
    horizontalLineTo(33.8f)
    horizontalLineTo(19.0f)
    lineTo(19.0f, 29.2f)
    horizontalLineTo(19.0f)
    lineTo(19.0f, 36.6f)
    lineTo(19.0f, 36.6f)
    lineTo(19.0f, 44.0f)
    horizontalLineTo(26.4f)
    lineTo(26.4f, 58.8f)
    lineTo(26.4f, 73.6f)
    lineTo(26.4f, 81.0f)
    horizontalLineTo(41.2f)
    horizontalLineTo(41.2f)
    horizontalLineTo(56.0f)
    horizontalLineTo(56.0f)
    horizontalLineTo(70.8f)
    lineTo(85.6f, 81.0f)
    lineTo(85.6f, 73.6f)
    verticalLineTo(58.8f)
    lineTo(76.788f, 58.8f)
    horizontalLineTo(85.6f)
    lineTo(85.6f, 44.0f)
    lineTo(93.0f, 44.0f)
    lineTo(93.0f, 36.6f)
    lineTo(93.0f, 36.6f)
    verticalLineTo(29.2f)
    horizontalLineTo(85.6f)
    lineTo(85.6f, 36.6f)
    lineTo(78.2f, 36.6f)
    lineTo(78.2f, 36.6f)
    horizontalLineTo(63.4f)
    verticalLineTo(36.6f)
    horizontalLineTo(48.6f)
    verticalLineTo(44.0f)
    horizontalLineTo(48.6f)
    lineTo(48.6f, 36.6f)
    close()
  }
  path(
    fill = SolidColor(color2),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(40.819f, 48.56f)
    verticalLineTo(41.6f)
    horizontalLineTo(42.451f)
    verticalLineTo(47.996f)
    lineTo(43.063f, 48.608f)
    horizontalLineTo(45.415f)
    lineTo(46.027f, 47.996f)
    verticalLineTo(41.6f)
    horizontalLineTo(47.659f)
    verticalLineTo(48.56f)
    lineTo(46.219f, 50.0f)
    horizontalLineTo(42.259f)
    lineTo(40.819f, 48.56f)
    close()
    moveTo(49.161f, 44.024f)
    horizontalLineTo(50.625f)
    verticalLineTo(44.9f)
    lineTo(51.501f, 44.024f)
    horizontalLineTo(53.829f)
    lineTo(55.065f, 45.272f)
    verticalLineTo(48.752f)
    lineTo(53.829f, 50.0f)
    horizontalLineTo(51.513f)
    lineTo(50.745f, 49.364f)
    verticalLineTo(52.544f)
    horizontalLineTo(49.161f)
    verticalLineTo(44.024f)
    close()
    moveTo(53.061f, 48.656f)
    lineTo(53.481f, 48.236f)
    verticalLineTo(45.788f)
    lineTo(53.061f, 45.368f)
    horizontalLineTo(51.705f)
    lineTo(50.745f, 46.34f)
    verticalLineTo(47.804f)
    lineTo(51.693f, 48.656f)
    horizontalLineTo(53.061f)
    close()
    moveTo(61.618f, 42.98f)
    horizontalLineTo(59.11f)
    verticalLineTo(41.6f)
    horizontalLineTo(65.758f)
    verticalLineTo(42.98f)
    horizontalLineTo(63.25f)
    verticalLineTo(50.0f)
    horizontalLineTo(61.618f)
    verticalLineTo(42.98f)
    close()
    moveTo(65.459f, 48.752f)
    verticalLineTo(45.272f)
    lineTo(66.695f, 44.024f)
    horizontalLineTo(70.127f)
    lineTo(71.363f, 45.272f)
    verticalLineTo(48.752f)
    lineTo(70.127f, 50.0f)
    horizontalLineTo(66.695f)
    lineTo(65.459f, 48.752f)
    close()
    moveTo(69.359f, 48.68f)
    lineTo(69.779f, 48.26f)
    verticalLineTo(45.764f)
    lineTo(69.359f, 45.344f)
    horizontalLineTo(67.463f)
    lineTo(67.043f, 45.764f)
    verticalLineTo(48.26f)
    lineTo(67.463f, 48.68f)
    horizontalLineTo(69.359f)
    close()
  }
  path(
    fill = SolidColor(color2),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(37.991f, 72.3f)
    horizontalLineTo(42.82f)
    lineTo(43.92f, 73.4f)
    verticalLineTo(75.556f)
    lineTo(43.48f, 76.007f)
    lineTo(44.261f, 76.799f)
    verticalLineTo(78.812f)
    lineTo(43.073f, 80.0f)
    horizontalLineTo(37.991f)
    verticalLineTo(72.3f)
    close()
    moveTo(42.05f, 75.523f)
    lineTo(42.446f, 75.127f)
    verticalLineTo(73.939f)
    lineTo(42.061f, 73.554f)
    horizontalLineTo(39.454f)
    verticalLineTo(75.523f)
    horizontalLineTo(42.05f)
    close()
    moveTo(42.314f, 78.746f)
    lineTo(42.787f, 78.273f)
    verticalLineTo(77.195f)
    lineTo(42.314f, 76.722f)
    horizontalLineTo(39.454f)
    verticalLineTo(78.746f)
    horizontalLineTo(42.314f)
    close()
    moveTo(45.25f, 78.735f)
    verticalLineTo(73.565f)
    lineTo(46.515f, 72.3f)
    horizontalLineTo(50.475f)
    lineTo(51.741f, 73.565f)
    verticalLineTo(78.735f)
    lineTo(50.475f, 80.0f)
    horizontalLineTo(46.515f)
    lineTo(45.25f, 78.735f)
    close()
    moveTo(49.683f, 78.724f)
    lineTo(50.244f, 78.163f)
    verticalLineTo(74.137f)
    lineTo(49.683f, 73.576f)
    horizontalLineTo(47.307f)
    lineTo(46.746f, 74.137f)
    verticalLineTo(78.163f)
    lineTo(47.307f, 78.724f)
    horizontalLineTo(49.683f)
    close()
    moveTo(53.063f, 72.3f)
    horizontalLineTo(54.405f)
    lineTo(57.837f, 77.525f)
    horizontalLineTo(57.859f)
    verticalLineTo(72.3f)
    horizontalLineTo(59.3f)
    verticalLineTo(80.0f)
    horizontalLineTo(57.958f)
    lineTo(54.526f, 74.786f)
    horizontalLineTo(54.504f)
    verticalLineTo(80.0f)
    horizontalLineTo(53.063f)
    verticalLineTo(72.3f)
    close()
    moveTo(60.733f, 78.68f)
    verticalLineTo(72.3f)
    horizontalLineTo(62.229f)
    verticalLineTo(78.163f)
    lineTo(62.79f, 78.724f)
    horizontalLineTo(64.946f)
    lineTo(65.507f, 78.163f)
    verticalLineTo(72.3f)
    horizontalLineTo(67.003f)
    verticalLineTo(78.68f)
    lineTo(65.683f, 80.0f)
    horizontalLineTo(62.053f)
    lineTo(60.733f, 78.68f)
    close()
    moveTo(68.27f, 78.812f)
    verticalLineTo(77.712f)
    horizontalLineTo(69.744f)
    verticalLineTo(78.361f)
    lineTo(70.107f, 78.724f)
    horizontalLineTo(72.384f)
    lineTo(72.758f, 78.35f)
    verticalLineTo(77.074f)
    lineTo(72.395f, 76.711f)
    horizontalLineTo(69.48f)
    lineTo(68.292f, 75.523f)
    verticalLineTo(73.488f)
    lineTo(69.48f, 72.3f)
    horizontalLineTo(72.956f)
    lineTo(74.144f, 73.488f)
    verticalLineTo(74.599f)
    horizontalLineTo(72.67f)
    verticalLineTo(73.939f)
    lineTo(72.307f, 73.576f)
    horizontalLineTo(70.129f)
    lineTo(69.766f, 73.939f)
    verticalLineTo(75.072f)
    lineTo(70.129f, 75.435f)
    horizontalLineTo(73.044f)
    lineTo(74.232f, 76.623f)
    verticalLineTo(78.79f)
    lineTo(73.022f, 80.0f)
    horizontalLineTo(69.458f)
    lineTo(68.27f, 78.812f)
    close()
  }
  path(
    fill = SolidColor(color2),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(67.923f, 66.292f)
    verticalLineTo(64.42f)
    lineTo(68.643f, 63.7f)
    horizontalLineTo(70.209f)
    lineTo(70.928f, 64.42f)
    verticalLineTo(66.292f)
    lineTo(70.209f, 67.012f)
    horizontalLineTo(68.643f)
    lineTo(67.923f, 66.292f)
    close()
    moveTo(72.612f, 63.7f)
    horizontalLineTo(73.836f)
    lineTo(70.388f, 70.0f)
    horizontalLineTo(69.164f)
    lineTo(72.612f, 63.7f)
    close()
    moveTo(69.687f, 66.112f)
    lineTo(69.884f, 65.914f)
    verticalLineTo(64.798f)
    lineTo(69.687f, 64.6f)
    horizontalLineTo(69.164f)
    lineTo(68.966f, 64.798f)
    verticalLineTo(65.914f)
    lineTo(69.164f, 66.112f)
    horizontalLineTo(69.687f)
    close()
    moveTo(72.072f, 69.28f)
    verticalLineTo(67.408f)
    lineTo(72.791f, 66.688f)
    horizontalLineTo(74.357f)
    lineTo(75.077f, 67.408f)
    verticalLineTo(69.28f)
    lineTo(74.357f, 70.0f)
    horizontalLineTo(72.791f)
    lineTo(72.072f, 69.28f)
    close()
    moveTo(73.836f, 69.1f)
    lineTo(74.034f, 68.902f)
    verticalLineTo(67.786f)
    lineTo(73.836f, 67.588f)
    horizontalLineTo(73.313f)
    lineTo(73.116f, 67.786f)
    verticalLineTo(68.902f)
    lineTo(73.313f, 69.1f)
    horizontalLineTo(73.836f)
    close()
  }
  path(
    fill = SolidColor(color2),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(38.936f, 66.328f)
    lineTo(47.576f, 58.48f)
    verticalLineTo(56.776f)
    lineTo(46.784f, 55.984f)
    horizontalLineTo(42.896f)
    lineTo(42.104f, 56.776f)
    verticalLineTo(58.48f)
    horizontalLineTo(38.84f)
    verticalLineTo(55.792f)
    lineTo(41.432f, 53.2f)
    horizontalLineTo(48.248f)
    lineTo(50.84f, 55.792f)
    verticalLineTo(59.44f)
    lineTo(42.32f, 67.024f)
    verticalLineTo(67.264f)
    horizontalLineTo(50.936f)
    verticalLineTo(70.0f)
    horizontalLineTo(38.936f)
    verticalLineTo(66.328f)
    close()
    moveTo(53.101f, 67.216f)
    verticalLineTo(55.984f)
    lineTo(55.885f, 53.2f)
    horizontalLineTo(63.325f)
    lineTo(66.109f, 55.984f)
    verticalLineTo(67.216f)
    lineTo(63.325f, 70.0f)
    horizontalLineTo(55.885f)
    lineTo(53.101f, 67.216f)
    close()
    moveTo(61.789f, 67.216f)
    lineTo(62.845f, 66.16f)
    verticalLineTo(57.04f)
    lineTo(61.789f, 55.984f)
    horizontalLineTo(57.421f)
    lineTo(56.365f, 57.04f)
    verticalLineTo(66.16f)
    lineTo(57.421f, 67.216f)
    horizontalLineTo(61.789f)
    close()
  }
  path(
    fill = SolidColor(color1),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(112.0f, 64.0f)
    lineToRelative(0.0f, -15.0f)
    lineToRelative(8.0f, -0.0f)
    lineToRelative(0.0f, 15.0f)
    close()
  }
  path(
    fill = SolidColor(color3),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(104.0f, 89.0f)
    horizontalLineToRelative(32.0f)
    verticalLineToRelative(7.0f)
    horizontalLineToRelative(-32.0f)
    close()
  }
  path(
    fill = SolidColor(color3),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 33.0f)
    horizontalLineToRelative(8.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-8.0f)
    close()
  }
}
  .build()
