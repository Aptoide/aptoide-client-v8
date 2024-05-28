package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestNoWifi() {
  Image(
    imageVector = getNoWifi(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getNoWifi(): ImageVector = ImageVector.Builder(
  name = "wifi_off",
  defaultWidth = 88.dp,
  defaultHeight = 88.dp,
  viewportWidth = 88f,
  viewportHeight = 88f,
).apply {
  path(
    fill = SolidColor(Palette.Primary),
  ) {
    moveTo(72.4167f, 82.8667f)
    lineTo(37.95f, 48.2167f)
    curveTo(35.0778f, 48.8889f, 32.4042f, 49.8972f, 29.9292f, 51.2417f)
    curveTo(27.4542f, 52.5861f, 25.2389f, 54.2667f, 23.2833f, 56.2833f)
    lineTo(15.5833f, 48.4f)
    curveTo(17.5389f, 46.4445f, 19.6472f, 44.7333f, 21.9083f, 43.2667f)
    curveTo(24.1694f, 41.8f, 26.5833f, 40.5167f, 29.15f, 39.4167f)
    lineTo(20.9f, 31.1667f)
    curveTo(18.3944f, 32.45f, 16.0569f, 33.8708f, 13.8875f, 35.4292f)
    curveTo(11.7181f, 36.9875f, 9.65556f, 38.7445f, 7.7f, 40.7f)
    lineTo(0f, 32.8167f)
    curveTo(1.95556f, 30.8611f, 3.9875f, 29.1042f, 6.09583f, 27.5458f)
    curveTo(8.20417f, 25.9875f, 10.45f, 24.5056f, 12.8333f, 23.1f)
    lineTo(5.13333f, 15.4f)
    lineTo(10.2667f, 10.2667f)
    lineTo(77.7333f, 77.7334f)
    lineTo(72.4167f, 82.8667f)
    close()
    moveTo(65.6333f, 55.1833f)
    lineTo(47.1167f, 36.6667f)
    curveTo(52.0667f, 37.1556f, 56.6958f, 38.4083f, 61.0042f, 40.425f)
    curveTo(65.3125f, 42.4417f, 69.1167f, 45.1f, 72.4167f, 48.4f)
    lineTo(65.6333f, 55.1833f)
    close()
    moveTo(80.3f, 40.7f)
    curveTo(75.5944f, 35.9945f, 70.1403f, 32.3125f, 63.9375f, 29.6542f)
    curveTo(57.7347f, 26.9958f, 51.0889f, 25.6667f, 44f, 25.6667f)
    curveTo(42.7167f, 25.6667f, 41.4792f, 25.7125f, 40.2875f, 25.8042f)
    curveTo(39.0958f, 25.8958f, 37.8889f, 26.0333f, 36.6667f, 26.2167f)
    lineTo(27.3167f, 16.8667f)
    curveTo(30.0056f, 16.1333f, 32.7403f, 15.5833f, 35.5208f, 15.2167f)
    curveTo(38.3014f, 14.85f, 41.1278f, 14.6667f, 44f, 14.6667f)
    curveTo(52.6778f, 14.6667f, 60.775f, 16.2861f, 68.2917f, 19.525f)
    curveTo(75.8083f, 22.7639f, 82.3778f, 27.1945f, 88f, 32.8167f)
    lineTo(80.3f, 40.7f)
    close()
    moveTo(44f, 77f)
    lineTo(31.075f, 63.9834f)
    curveTo(32.7861f, 62.2722f, 34.7569f, 60.9583f, 36.9875f, 60.0417f)
    curveTo(39.2181f, 59.125f, 41.5556f, 58.6667f, 44f, 58.6667f)
    curveTo(46.4444f, 58.6667f, 48.7819f, 59.125f, 51.0125f, 60.0417f)
    curveTo(53.2431f, 60.9583f, 55.2139f, 62.2722f, 56.925f, 63.9834f)
    lineTo(44f, 77f)
    close()
  }
}.build()
