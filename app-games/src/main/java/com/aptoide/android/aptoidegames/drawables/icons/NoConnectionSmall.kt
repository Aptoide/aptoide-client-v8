package com.aptoide.android.aptoidegames.drawables.icons

import android.content.res.Configuration
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
import com.aptoide.android.aptoidegames.theme.blueGradientEnd
import com.aptoide.android.aptoidegames.theme.blueGradientStart
import com.aptoide.android.aptoidegames.theme.pureWhite

@Preview
@Composable
fun TestNoConnectionSmall() {
  Image(
    imageVector = getNoConnectionSmall(0.05f, 0.2f),
    contentDescription = null,
    modifier = Modifier.size(184.dp)
  )
}

@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun TestNoConnectionSmallDark() {
  Image(
    imageVector = getNoConnectionSmall(0.3f, 0.45f),
    contentDescription = null,
    modifier = Modifier.size(184.dp)
  )
}

fun getNoConnectionSmall(
  outerCircleOpacity: Float,
  middleCircleOpacity: Float,
): ImageVector = ImageVector.Builder(
  name = "NoConnectionSmall",
  defaultWidth = 184.dp,
  defaultHeight = 184.dp,
  viewportWidth = 184f,
  viewportHeight = 184f,
).apply {
  path(
    fillAlpha = outerCircleOpacity,
    fill = Brush.linearGradient(
      colors = listOf(blueGradientStart, blueGradientEnd),
      start = Offset(x = 15.6514f, y = 18.1881f),
      end = Offset(x = 201.007f, y = 111.141f)
    ),
  ) {
    moveTo(92f, 184f)
    curveTo(142.81f, 184f, 184f, 142.81f, 184f, 92f)
    curveTo(184f, 41.1898f, 142.81f, 0f, 92f, 0f)
    curveTo(41.1898f, 0f, 0f, 41.1898f, 0f, 92f)
    curveTo(0f, 142.81f, 41.1898f, 184f, 92f, 184f)
    close()
  }
  path(
    fillAlpha = middleCircleOpacity,
    fill = Brush.linearGradient(
      colors = listOf(blueGradientStart, blueGradientEnd),
      start = Offset(x = 48.8464f, y = 50.2802f),
      end = Offset(x = 153.612f, y = 102.819f)
    ),
  ) {
    moveTo(92f, 144f)
    curveTo(120.719f, 144f, 144f, 120.719f, 144f, 92f)
    curveTo(144f, 63.2812f, 120.719f, 40f, 92f, 40f)
    curveTo(63.2812f, 40f, 40f, 63.2812f, 40f, 92f)
    curveTo(40f, 120.719f, 63.2812f, 144f, 92f, 144f)
    close()
  }
  path(
    fill = Brush.linearGradient(
      colors = listOf(blueGradientStart, blueGradientEnd),
      start = Offset(x = 68.7635f, y = 69.5355f),
      end = Offset(x = 125.176f, y = 97.8257f)
    ),
  ) {
    moveTo(92f, 120f)
    curveTo(107.464f, 120f, 120f, 107.464f, 120f, 92f)
    curveTo(120f, 76.536f, 107.464f, 64f, 92f, 64f)
    curveTo(76.536f, 64f, 64f, 76.536f, 64f, 92f)
    curveTo(64f, 107.464f, 76.536f, 120f, 92f, 120f)
    close()
  }
  path(
    fill = SolidColor(pureWhite),
  ) {
    moveTo(104.162f, 90.6592f)
    curveTo(102.451f, 89.0259f, 100.58f, 87.7231f, 98.5479f, 86.7509f)
    curveTo(96.516f, 85.7787f, 94.3333f, 85.2926f, 92f, 85.2926f)
    curveTo(91.2806f, 85.2926f, 90.5903f, 85.3363f, 89.9292f, 85.4238f)
    curveTo(89.2681f, 85.5113f, 88.7139f, 85.6329f, 88.2667f, 85.7884f)
    lineTo(86.1375f, 83.6592f)
    curveTo(86.9931f, 83.3481f, 87.9215f, 83.1051f, 88.9229f, 82.9301f)
    curveTo(89.9243f, 82.7551f, 90.95f, 82.6676f, 92f, 82.6676f)
    curveTo(94.7222f, 82.6676f, 97.284f, 83.2315f, 99.6854f, 84.3592f)
    curveTo(102.087f, 85.487f, 104.192f, 86.9745f, 106f, 88.8217f)
    lineTo(104.162f, 90.6592f)
    close()
    moveTo(99.2333f, 95.5884f)
    curveTo(98.5917f, 94.9662f, 98.0083f, 94.4655f, 97.4833f, 94.0863f)
    curveTo(96.9583f, 93.7072f, 96.2875f, 93.3329f, 95.4708f, 92.9634f)
    lineTo(92.175f, 89.6676f)
    curveTo(94.0222f, 89.7065f, 95.6507f, 90.0856f, 97.0604f, 90.8051f)
    curveTo(98.4701f, 91.5245f, 99.8069f, 92.5065f, 101.071f, 93.7509f)
    lineTo(99.2333f, 95.5884f)
    close()
    moveTo(101.479f, 103.988f)
    lineTo(89.9875f, 92.4967f)
    curveTo(88.9375f, 92.7495f, 87.9701f, 93.1579f, 87.0854f, 93.7217f)
    curveTo(86.2007f, 94.2856f, 85.4278f, 94.9079f, 84.7667f, 95.5884f)
    lineTo(82.9292f, 93.7509f)
    curveTo(83.6486f, 93.0315f, 84.3924f, 92.3995f, 85.1604f, 91.8551f)
    curveTo(85.9285f, 91.3106f, 86.8472f, 90.8245f, 87.9167f, 90.3967f)
    lineTo(84.6792f, 87.1592f)
    curveTo(83.7653f, 87.6065f, 82.9f, 88.1363f, 82.0833f, 88.7488f)
    curveTo(81.2667f, 89.3613f, 80.5181f, 89.9981f, 79.8375f, 90.6592f)
    lineTo(78f, 88.8217f)
    curveTo(78.7f, 88.1023f, 79.4486f, 87.4315f, 80.2458f, 86.8092f)
    curveTo(81.0431f, 86.187f, 81.8597f, 85.6523f, 82.6958f, 85.2051f)
    lineTo(80.0125f, 82.5217f)
    lineTo(81.2667f, 81.2676f)
    lineTo(102.733f, 102.734f)
    lineTo(101.479f, 103.988f)
    close()
    moveTo(92f, 102.822f)
    lineTo(87.6833f, 98.4759f)
    curveTo(88.2472f, 97.912f, 88.8937f, 97.4697f, 89.6229f, 97.1488f)
    curveTo(90.3521f, 96.828f, 91.1444f, 96.6676f, 92f, 96.6676f)
    curveTo(92.8556f, 96.6676f, 93.6479f, 96.828f, 94.3771f, 97.1488f)
    curveTo(95.1062f, 97.4697f, 95.7528f, 97.912f, 96.3167f, 98.4759f)
    lineTo(92f, 102.822f)
    close()
  }
}.build()
