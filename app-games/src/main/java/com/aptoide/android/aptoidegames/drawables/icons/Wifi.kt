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
import com.aptoide.android.aptoidegames.theme.primary

@Preview
@Composable
fun TestWifi() {
  Image(
    imageVector = getWifi(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getWifi(): ImageVector = ImageVector.Builder(
  name = "Wifi",
  defaultWidth = 88.dp,
  defaultHeight = 88.dp,
  viewportWidth = 88f,
  viewportHeight = 88f,
).apply {
  path(
    fill = SolidColor(primary),
  ) {
    moveTo(23.2833f, 56.2834f)
    lineTo(15.5833f, 48.4f)
    curveTo(19.3722f, 44.6111f, 23.7111f, 41.7084f, 28.6f, 39.6917f)
    curveTo(33.4889f, 37.675f, 38.6222f, 36.6667f, 44f, 36.6667f)
    curveTo(49.3778f, 36.6667f, 54.5264f, 37.6903f, 59.4458f, 39.7375f)
    curveTo(64.3653f, 41.7848f, 68.6889f, 44.7334f, 72.4167f, 48.5834f)
    lineTo(64.7167f, 56.2834f)
    curveTo(61.9667f, 53.5334f, 58.8042f, 51.4098f, 55.2292f, 49.9125f)
    curveTo(51.6542f, 48.4153f, 47.9111f, 47.6667f, 44f, 47.6667f)
    curveTo(40.0889f, 47.6667f, 36.3458f, 48.4153f, 32.7708f, 49.9125f)
    curveTo(29.1958f, 51.4098f, 26.0333f, 53.5334f, 23.2833f, 56.2834f)
    close()
    moveTo(7.7f, 40.7f)
    lineTo(0f, 33f)
    curveTo(5.80556f, 27.0723f, 12.5125f, 22.5348f, 20.1208f, 19.3875f)
    curveTo(27.7292f, 16.2403f, 35.6889f, 14.6667f, 44f, 14.6667f)
    curveTo(52.3111f, 14.6667f, 60.2708f, 16.2403f, 67.8792f, 19.3875f)
    curveTo(75.4875f, 22.5348f, 82.1944f, 27.0723f, 88f, 33f)
    lineTo(80.3f, 40.7f)
    curveTo(75.4722f, 35.8723f, 69.9264f, 32.1598f, 63.6625f, 29.5625f)
    curveTo(57.3986f, 26.9653f, 50.8444f, 25.6667f, 44f, 25.6667f)
    curveTo(37.1556f, 25.6667f, 30.6014f, 26.9653f, 24.3375f, 29.5625f)
    curveTo(18.0736f, 32.1598f, 12.5278f, 35.8723f, 7.7f, 40.7f)
    close()
    moveTo(44f, 77f)
    lineTo(31.075f, 63.9834f)
    curveTo(32.7861f, 62.2723f, 34.7569f, 60.9584f, 36.9875f, 60.0417f)
    curveTo(39.2181f, 59.125f, 41.5556f, 58.6667f, 44f, 58.6667f)
    curveTo(46.4444f, 58.6667f, 48.7819f, 59.125f, 51.0125f, 60.0417f)
    curveTo(53.2431f, 60.9584f, 55.2139f, 62.2723f, 56.925f, 63.9834f)
    lineTo(44f, 77f)
    close()
  }
}.build()
