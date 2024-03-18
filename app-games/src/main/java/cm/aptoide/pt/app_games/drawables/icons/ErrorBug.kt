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
fun TestErrorBug() {
  Image(
    imageVector = getErrorBug(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getErrorBug(): ImageVector = ImageVector.Builder(
  name = "ErrorBug",
  defaultWidth = 104.dp,
  defaultHeight = 104.dp,
  viewportWidth = 104f,
  viewportHeight = 104f,
).apply {
  path(
    fill = Brush.linearGradient(
      colors = listOf(blueGradientStart, blueGradientEnd),
      start = Offset(x = 8.84642f, y = 10.2802f),
      end = Offset(x = 113.612f, y = 62.8191f)
    ),
  ) {
    moveTo(52f, 104f)
    curveTo(80.7188f, 104f, 104f, 80.7188f, 104f, 52f)
    curveTo(104f, 23.2812f, 80.7188f, 0f, 52f, 0f)
    curveTo(23.2812f, 0f, 0f, 23.2812f, 0f, 52f)
    curveTo(0f, 80.7188f, 23.2812f, 104f, 52f, 104f)
    close()
  }
  path(
    fill = SolidColor(pureWhite),
  ) {
    moveTo(52.0001f, 72.25f)
    curveTo(49.5626f, 72.25f, 47.2938f, 71.6688f, 45.1938f, 70.5063f)
    curveTo(43.0938f, 69.3438f, 41.5376f, 67.675f, 40.5251f, 65.5f)
    horizontalLineTo(34.0001f)
    verticalLineTo(62.125f)
    horizontalLineTo(39.1751f)
    curveTo(38.9126f, 61.15f, 38.7813f, 60.1656f, 38.7813f, 59.1719f)
    verticalLineTo(56.1625f)
    horizontalLineTo(33.9438f)
    verticalLineTo(52.7875f)
    horizontalLineTo(38.7813f)
    curveTo(38.7813f, 51.7f, 38.7907f, 50.6219f, 38.8095f, 49.5531f)
    curveTo(38.8282f, 48.4844f, 38.9876f, 47.425f, 39.2876f, 46.375f)
    horizontalLineTo(34.0001f)
    verticalLineTo(43f)
    horizontalLineTo(40.7501f)
    curveTo(41.2751f, 41.95f, 41.9688f, 41.0312f, 42.8313f, 40.2438f)
    curveTo(43.6938f, 39.4563f, 44.6501f, 38.8f, 45.7001f, 38.275f)
    lineTo(41.3688f, 34f)
    lineTo(43.6188f, 31.75f)
    lineTo(48.9063f, 37.0375f)
    curveTo(49.9563f, 36.6625f, 51.0157f, 36.475f, 52.0845f, 36.475f)
    curveTo(53.1532f, 36.475f, 54.2126f, 36.6625f, 55.2626f, 37.0375f)
    lineTo(60.5501f, 31.75f)
    lineTo(62.8001f, 34f)
    lineTo(58.5251f, 38.275f)
    curveTo(59.5751f, 38.8f, 60.5032f, 39.4656f, 61.3095f, 40.2719f)
    curveTo(62.1157f, 41.0781f, 62.8188f, 41.9875f, 63.4188f, 43f)
    horizontalLineTo(70.0563f)
    verticalLineTo(46.375f)
    horizontalLineTo(64.7126f)
    curveTo(65.0501f, 47.425f, 65.2095f, 48.4844f, 65.1907f, 49.5531f)
    curveTo(65.172f, 50.6219f, 65.1626f, 51.7f, 65.1626f, 52.7875f)
    horizontalLineTo(70.0563f)
    verticalLineTo(56.1625f)
    horizontalLineTo(65.1626f)
    curveTo(65.1626f, 57.175f, 65.172f, 58.1781f, 65.1907f, 59.1719f)
    curveTo(65.2095f, 60.1656f, 65.0876f, 61.15f, 64.8251f, 62.125f)
    horizontalLineTo(70.0563f)
    verticalLineTo(65.5f)
    horizontalLineTo(63.5313f)
    curveTo(62.5563f, 67.7125f, 61.0095f, 69.3906f, 58.8907f, 70.5344f)
    curveTo(56.772f, 71.6781f, 54.4751f, 72.25f, 52.0001f, 72.25f)
    close()
    moveTo(52.0001f, 68.875f)
    curveTo(54.7001f, 68.875f, 57.0063f, 67.9281f, 58.9188f, 66.0344f)
    curveTo(60.8313f, 64.1406f, 61.7876f, 61.8438f, 61.7876f, 59.1438f)
    verticalLineTo(49.75f)
    curveTo(61.7876f, 47.05f, 60.8313f, 44.7531f, 58.9188f, 42.8594f)
    curveTo(57.0063f, 40.9656f, 54.7001f, 40.0188f, 52.0001f, 40.0188f)
    curveTo(49.3001f, 40.0188f, 46.9938f, 40.9656f, 45.0813f, 42.8594f)
    curveTo(43.1688f, 44.7531f, 42.2126f, 47.05f, 42.2126f, 49.75f)
    verticalLineTo(59.1438f)
    curveTo(42.2126f, 61.8438f, 43.1688f, 64.1406f, 45.0813f, 66.0344f)
    curveTo(46.9938f, 67.9281f, 49.3001f, 68.875f, 52.0001f, 68.875f)
    close()
    moveTo(47.5001f, 61f)
    horizontalLineTo(56.5001f)
    verticalLineTo(57.625f)
    horizontalLineTo(47.5001f)
    verticalLineTo(61f)
    close()
    moveTo(47.5001f, 51.2688f)
    horizontalLineTo(56.5001f)
    verticalLineTo(47.8938f)
    horizontalLineTo(47.5001f)
    verticalLineTo(51.2688f)
    close()
    moveTo(52.0001f, 54.475f)
    horizontalLineTo(52.0282f)
    horizontalLineTo(52.0001f)
    horizontalLineTo(52.0282f)
    horizontalLineTo(52.0001f)
    horizontalLineTo(52.0282f)
    horizontalLineTo(52.0001f)
    horizontalLineTo(52.0282f)
    horizontalLineTo(52.0001f)
    close()
  }
}.build()
