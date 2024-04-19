package cm.aptoide.pt.app_games.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.theme.blueGradientEnd
import cm.aptoide.pt.app_games.theme.blueGradientStart
import cm.aptoide.pt.app_games.theme.pureWhite

@Preview
@Composable
fun TestGamepad() {
  Image(
    imageVector = getGamepad(0.4f, 0.2f),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getGamepad(
  outerLayerAlpha: Float = 0.1f,
  middleLayerAlpha: Float = 0.2f,
): ImageVector = ImageVector.Builder(
  name = "Gamepad",
  defaultWidth = 248.dp,
  defaultHeight = 248.dp,
  viewportWidth = 248f,
  viewportHeight = 248f,
).apply {
  path(
    fillAlpha = outerLayerAlpha,
    fill = Brush.linearGradient(
      colors = listOf(blueGradientStart, blueGradientEnd),
      start = Offset(x = 21.0953f, y = 24.5143f),
      end = Offset(x = 270.922f, y = 149.799f)
    ),
  ) {
    moveTo(124f, 248f)
    curveTo(192.483f, 248f, 248f, 192.483f, 248f, 124f)
    curveTo(248f, 55.5167f, 192.483f, 0f, 124f, 0f)
    curveTo(55.5167f, 0f, 0f, 55.5167f, 0f, 124f)
    curveTo(0f, 192.483f, 55.5167f, 248f, 124f, 248f)
    close()
  }
  path(
    fillAlpha = middleLayerAlpha,
    fill = Brush.linearGradient(
      colors = listOf(blueGradientStart, blueGradientEnd),
      start = Offset(x = 54.2904f, y = 56.6065f),
      end = Offset(x = 223.528f, y = 141.477f)
    ),
  ) {
    moveTo(124f, 208f)
    curveTo(170.392f, 208f, 208f, 170.392f, 208f, 124f)
    curveTo(208f, 77.6081f, 170.392f, 40f, 124f, 40f)
    curveTo(77.6081f, 40f, 40f, 77.6081f, 40f, 124f)
    curveTo(40f, 170.392f, 77.6081f, 208f, 124f, 208f)
    close()
  }
  path(
    fill = Brush.linearGradient(
      colors = listOf(blueGradientStart, blueGradientEnd),
      start = Offset(x = 80.8464f, y = 82.2802f),
      end = Offset(x = 185.612f, y = 134.819f)
    ),
  ) {
    moveTo(124f, 176f)
    curveTo(152.719f, 176f, 176f, 152.719f, 176f, 124f)
    curveTo(176f, 95.2812f, 152.719f, 72f, 124f, 72f)
    curveTo(95.2812f, 72f, 72f, 95.2812f, 72f, 124f)
    curveTo(72f, 152.719f, 95.2812f, 176f, 124f, 176f)
    close()
  }
  path(
    fill = SolidColor(pureWhite),
  ) {
    moveTo(102.033f, 143f)
    curveTo(100.361f, 143f, 99.0493f, 142.442f, 98.1089f, 141.314f)
    curveTo(97.1684f, 140.187f, 96.8201f, 138.641f, 97.0872f, 136.653f)
    lineTo(100.245f, 114.521f)
    curveTo(100.593f, 112.184f, 101.685f, 110.185f, 103.508f, 108.511f)
    curveTo(105.331f, 106.837f, 107.409f, 106f, 109.742f, 106f)
    horizontalLineTo(138.316f)
    curveTo(140.649f, 106f, 142.728f, 106.837f, 144.55f, 108.511f)
    curveTo(146.373f, 110.185f, 147.465f, 112.184f, 147.813f, 114.521f)
    lineTo(150.913f, 136.653f)
    curveTo(151.18f, 138.629f, 150.832f, 140.187f, 149.891f, 141.314f)
    curveTo(148.951f, 142.442f, 147.639f, 143f, 145.967f, 143f)
    curveTo(144.957f, 143f, 144.098f, 142.837f, 143.389f, 142.5f)
    curveTo(142.681f, 142.163f, 142.089f, 141.768f, 141.613f, 141.28f)
    lineTo(134.751f, 134.41f)
    horizontalLineTo(113.249f)
    lineTo(106.387f, 141.28f)
    curveTo(105.899f, 141.768f, 105.307f, 142.175f, 104.611f, 142.5f)
    curveTo(103.902f, 142.826f, 103.043f, 143f, 102.033f, 143f)
    verticalLineTo(143f)
    close()
    moveTo(138.525f, 127.145f)
    curveTo(139.233f, 127.145f, 139.848f, 126.877f, 140.371f, 126.354f)
    curveTo(140.893f, 125.831f, 141.16f, 125.203f, 141.16f, 124.506f)
    curveTo(141.16f, 123.808f, 140.893f, 123.181f, 140.371f, 122.658f)
    curveTo(139.848f, 122.134f, 139.221f, 121.867f, 138.525f, 121.867f)
    curveTo(137.828f, 121.867f, 137.201f, 122.134f, 136.679f, 122.658f)
    curveTo(136.156f, 123.181f, 135.889f, 123.808f, 135.889f, 124.506f)
    curveTo(135.889f, 125.203f, 136.156f, 125.831f, 136.679f, 126.354f)
    curveTo(137.201f, 126.877f, 137.828f, 127.145f, 138.525f, 127.145f)
    close()
    moveTo(132.917f, 118.554f)
    curveTo(133.625f, 118.554f, 134.24f, 118.287f, 134.763f, 117.764f)
    curveTo(135.285f, 117.241f, 135.552f, 116.613f, 135.552f, 115.915f)
    curveTo(135.552f, 115.218f, 135.285f, 114.59f, 134.763f, 114.067f)
    curveTo(134.24f, 113.544f, 133.613f, 113.277f, 132.917f, 113.277f)
    curveTo(132.22f, 113.277f, 131.593f, 113.544f, 131.071f, 114.067f)
    curveTo(130.548f, 114.59f, 130.281f, 115.218f, 130.281f, 115.915f)
    curveTo(130.281f, 116.613f, 130.548f, 117.241f, 131.071f, 117.764f)
    curveTo(131.593f, 118.287f, 132.22f, 118.554f, 132.917f, 118.554f)
    verticalLineTo(118.554f)
    close()
    moveTo(112.134f, 126.807f)
    horizontalLineTo(115.432f)
    verticalLineTo(121.855f)
    horizontalLineTo(120.378f)
    verticalLineTo(118.554f)
    horizontalLineTo(115.432f)
    verticalLineTo(113.602f)
    horizontalLineTo(112.134f)
    verticalLineTo(118.554f)
    horizontalLineTo(107.188f)
    verticalLineTo(121.855f)
    horizontalLineTo(112.134f)
    verticalLineTo(126.807f)
    verticalLineTo(126.807f)
    close()
  }
}.build()
