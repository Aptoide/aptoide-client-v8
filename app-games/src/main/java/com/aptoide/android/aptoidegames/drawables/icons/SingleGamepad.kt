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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestSingleGamepad() {
  Image(
    imageVector = getSingleGamepad(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getSingleGamepad(): ImageVector = ImageVector.Builder(
  name = "Vector",
  defaultWidth = 54.0.dp,
  defaultHeight = 37.0.dp,
  viewportWidth = 54.0f,
  viewportHeight = 37.0f
).apply {
  path(
    fill = SolidColor(Color(0xFF818181)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(5.0332f, 37.0f)
    curveTo(3.3613f, 37.0f, 2.0493f, 36.442f, 1.1089f, 35.3145f)
    curveTo(0.1684f, 34.1869f, -0.1799f, 32.6409f, 0.0872f, 30.6532f)
    lineTo(3.2452f, 8.5206f)
    curveTo(3.5935f, 6.1841f, 4.6849f, 4.1847f, 6.5077f, 2.5108f)
    curveTo(8.3305f, 0.8369f, 10.4088f, 0.0f, 12.7425f, 0.0f)
    horizontalLineTo(41.3156f)
    curveTo(43.6493f, 0.0f, 45.7275f, 0.8369f, 47.5504f, 2.5108f)
    curveTo(49.3732f, 4.1847f, 50.4646f, 6.1841f, 50.8129f, 8.5206f)
    lineTo(53.9128f, 30.6532f)
    curveTo(54.1799f, 32.6293f, 53.8316f, 34.1869f, 52.8911f, 35.3145f)
    curveTo(51.9507f, 36.442f, 50.6387f, 37.0f, 48.9668f, 37.0f)
    curveTo(47.9567f, 37.0f, 47.0976f, 36.8373f, 46.3893f, 36.5002f)
    curveTo(45.6811f, 36.1631f, 45.089f, 35.7678f, 44.6129f, 35.2796f)
    lineTo(37.7512f, 28.4097f)
    horizontalLineTo(16.2488f)
    lineTo(9.3871f, 35.2796f)
    curveTo(8.8994f, 35.7678f, 8.3073f, 36.1747f, 7.6107f, 36.5002f)
    curveTo(6.9024f, 36.8256f, 6.0433f, 37.0f, 5.0332f, 37.0f)
    close()
    moveTo(41.5246f, 21.1445f)
    curveTo(42.2328f, 21.1445f, 42.8482f, 20.8772f, 43.3706f, 20.3541f)
    curveTo(43.8931f, 19.831f, 44.1601f, 19.2033f, 44.1601f, 18.5058f)
    curveTo(44.1601f, 17.8084f, 43.8931f, 17.1806f, 43.3706f, 16.6576f)
    curveTo(42.8482f, 16.1345f, 42.2212f, 15.8671f, 41.5246f, 15.8671f)
    curveTo(40.828f, 15.8671f, 40.201f, 16.1345f, 39.6785f, 16.6576f)
    curveTo(39.1561f, 17.1806f, 38.889f, 17.8084f, 38.889f, 18.5058f)
    curveTo(38.889f, 19.2033f, 39.1561f, 19.831f, 39.6785f, 20.3541f)
    curveTo(40.201f, 20.8772f, 40.828f, 21.1445f, 41.5246f, 21.1445f)
    close()
    moveTo(35.9168f, 12.5542f)
    curveTo(36.625f, 12.5542f, 37.2403f, 12.2868f, 37.7628f, 11.7637f)
    curveTo(38.2853f, 11.2407f, 38.5523f, 10.6129f, 38.5523f, 9.9155f)
    curveTo(38.5523f, 9.218f, 38.2853f, 8.5903f, 37.7628f, 8.0672f)
    curveTo(37.2403f, 7.5441f, 36.6134f, 7.2768f, 35.9168f, 7.2768f)
    curveTo(35.2201f, 7.2768f, 34.5932f, 7.5441f, 34.0707f, 8.0672f)
    curveTo(33.5483f, 8.5903f, 33.2812f, 9.218f, 33.2812f, 9.9155f)
    curveTo(33.2812f, 10.6129f, 33.5483f, 11.2407f, 34.0707f, 11.7637f)
    curveTo(34.5932f, 12.2868f, 35.2201f, 12.5542f, 35.9168f, 12.5542f)
    close()
    moveTo(15.1342f, 20.8074f)
    horizontalLineTo(18.4315f)
    verticalLineTo(15.8555f)
    horizontalLineTo(23.3776f)
    verticalLineTo(12.5542f)
    horizontalLineTo(18.4315f)
    verticalLineTo(7.6023f)
    horizontalLineTo(15.1342f)
    verticalLineTo(12.5542f)
    horizontalLineTo(10.1882f)
    verticalLineTo(15.8555f)
    horizontalLineTo(15.1342f)
    verticalLineTo(20.8074f)
    close()
  }
}.build()
