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
fun TestNoConnection() {
  Image(
    imageVector = getNoConnection(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getNoConnection(): ImageVector = ImageVector.Builder(
  name = "NoConnection",
  defaultWidth = 248.dp,
  defaultHeight = 248.dp,
  viewportWidth = 248f,
  viewportHeight = 248f,
).apply {
  path(
    fillAlpha = 0.1f,
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
    fillAlpha = 0.2f,
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
    moveTo(147.456f, 121.413f)
    curveTo(144.156f, 118.263f, 140.547f, 115.75f, 136.628f, 113.875f)
    curveTo(132.709f, 112f, 128.5f, 111.063f, 124f, 111.063f)
    curveTo(122.612f, 111.063f, 121.281f, 111.147f, 120.006f, 111.316f)
    curveTo(118.731f, 111.484f, 117.663f, 111.719f, 116.8f, 112.019f)
    lineTo(112.694f, 107.913f)
    curveTo(114.344f, 107.313f, 116.134f, 106.844f, 118.066f, 106.506f)
    curveTo(119.997f, 106.169f, 121.975f, 106f, 124f, 106f)
    curveTo(129.25f, 106f, 134.191f, 107.088f, 138.822f, 109.263f)
    curveTo(143.453f, 111.438f, 147.513f, 114.306f, 151f, 117.869f)
    lineTo(147.456f, 121.413f)
    close()
    moveTo(137.95f, 130.919f)
    curveTo(136.713f, 129.719f, 135.588f, 128.753f, 134.575f, 128.022f)
    curveTo(133.562f, 127.291f, 132.269f, 126.569f, 130.694f, 125.856f)
    lineTo(124.337f, 119.5f)
    curveTo(127.9f, 119.575f, 131.041f, 120.306f, 133.759f, 121.694f)
    curveTo(136.478f, 123.081f, 139.056f, 124.975f, 141.494f, 127.375f)
    lineTo(137.95f, 130.919f)
    close()
    moveTo(142.281f, 147.119f)
    lineTo(120.119f, 124.956f)
    curveTo(118.094f, 125.444f, 116.228f, 126.231f, 114.522f, 127.319f)
    curveTo(112.816f, 128.406f, 111.325f, 129.606f, 110.05f, 130.919f)
    lineTo(106.506f, 127.375f)
    curveTo(107.894f, 125.988f, 109.328f, 124.769f, 110.809f, 123.719f)
    curveTo(112.291f, 122.669f, 114.062f, 121.731f, 116.125f, 120.906f)
    lineTo(109.881f, 114.663f)
    curveTo(108.119f, 115.525f, 106.45f, 116.547f, 104.875f, 117.728f)
    curveTo(103.3f, 118.909f, 101.856f, 120.138f, 100.544f, 121.413f)
    lineTo(97f, 117.869f)
    curveTo(98.35f, 116.481f, 99.7937f, 115.188f, 101.331f, 113.988f)
    curveTo(102.869f, 112.788f, 104.444f, 111.756f, 106.056f, 110.894f)
    lineTo(100.881f, 105.719f)
    lineTo(103.3f, 103.3f)
    lineTo(144.7f, 144.7f)
    lineTo(142.281f, 147.119f)
    close()
    moveTo(124f, 144.869f)
    lineTo(115.675f, 136.487f)
    curveTo(116.763f, 135.4f, 118.009f, 134.547f, 119.416f, 133.928f)
    curveTo(120.822f, 133.309f, 122.35f, 133f, 124f, 133f)
    curveTo(125.65f, 133f, 127.178f, 133.309f, 128.584f, 133.928f)
    curveTo(129.991f, 134.547f, 131.237f, 135.4f, 132.325f, 136.487f)
    lineTo(124f, 144.869f)
    close()
  }
}.build()
