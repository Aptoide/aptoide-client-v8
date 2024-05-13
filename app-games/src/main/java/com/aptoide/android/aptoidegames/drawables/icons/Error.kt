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
import com.aptoide.android.aptoidegames.theme.error

@Preview
@Composable
fun TestError() {
  Image(
    imageVector = getError(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getError(): ImageVector = ImageVector.Builder(
  name = "Error",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(error),
    fillAlpha = 0.933333f,
  ) {
    moveTo(12f, 17f)
    curveTo(12.2833f, 17f, 12.5208f, 16.9042f, 12.7125f, 16.7125f)
    curveTo(12.9042f, 16.5208f, 13f, 16.2833f, 13f, 16f)
    curveTo(13f, 15.7167f, 12.9042f, 15.4792f, 12.7125f, 15.2875f)
    curveTo(12.5208f, 15.0958f, 12.2833f, 15f, 12f, 15f)
    curveTo(11.7167f, 15f, 11.4792f, 15.0958f, 11.2875f, 15.2875f)
    curveTo(11.0958f, 15.4792f, 11f, 15.7167f, 11f, 16f)
    curveTo(11f, 16.2833f, 11.0958f, 16.5208f, 11.2875f, 16.7125f)
    curveTo(11.4792f, 16.9042f, 11.7167f, 17f, 12f, 17f)
    close()
    moveTo(11f, 13f)
    horizontalLineTo(13f)
    verticalLineTo(7f)
    horizontalLineTo(11f)
    verticalLineTo(13f)
    close()
    moveTo(12f, 22f)
    curveTo(10.6167f, 22f, 9.31667f, 21.7375f, 8.1f, 21.2125f)
    curveTo(6.88333f, 20.6875f, 5.825f, 19.975f, 4.925f, 19.075f)
    curveTo(4.025f, 18.175f, 3.3125f, 17.1167f, 2.7875f, 15.9f)
    curveTo(2.2625f, 14.6833f, 2f, 13.3833f, 2f, 12f)
    curveTo(2f, 10.6167f, 2.2625f, 9.31667f, 2.7875f, 8.1f)
    curveTo(3.3125f, 6.88333f, 4.025f, 5.825f, 4.925f, 4.925f)
    curveTo(5.825f, 4.025f, 6.88333f, 3.3125f, 8.1f, 2.7875f)
    curveTo(9.31667f, 2.2625f, 10.6167f, 2f, 12f, 2f)
    curveTo(13.3833f, 2f, 14.6833f, 2.2625f, 15.9f, 2.7875f)
    curveTo(17.1167f, 3.3125f, 18.175f, 4.025f, 19.075f, 4.925f)
    curveTo(19.975f, 5.825f, 20.6875f, 6.88333f, 21.2125f, 8.1f)
    curveTo(21.7375f, 9.31667f, 22f, 10.6167f, 22f, 12f)
    curveTo(22f, 13.3833f, 21.7375f, 14.6833f, 21.2125f, 15.9f)
    curveTo(20.6875f, 17.1167f, 19.975f, 18.175f, 19.075f, 19.075f)
    curveTo(18.175f, 19.975f, 17.1167f, 20.6875f, 15.9f, 21.2125f)
    curveTo(14.6833f, 21.7375f, 13.3833f, 22f, 12f, 22f)
    close()
  }
  path(
    stroke = SolidColor(error),
    fillAlpha = 0.933333f,
    strokeLineWidth = 0.025f,
  ) {
    moveTo(10.9875f, 13f)
    verticalLineTo(13.0125f)
    horizontalLineTo(11f)
    horizontalLineTo(13f)
    horizontalLineTo(13.0125f)
    verticalLineTo(13f)
    verticalLineTo(7f)
    verticalLineTo(6.9875f)
    horizontalLineTo(13f)
    horizontalLineTo(11f)
    horizontalLineTo(10.9875f)
    verticalLineTo(7f)
    verticalLineTo(13f)
    close()
    moveTo(12f, 17.0125f)
    curveTo(12.2865f, 17.0125f, 12.5272f, 16.9155f, 12.7213f, 16.7213f)
    curveTo(12.9155f, 16.5272f, 13.0125f, 16.2865f, 13.0125f, 16f)
    curveTo(13.0125f, 15.7135f, 12.9155f, 15.4728f, 12.7213f, 15.2787f)
    curveTo(12.5272f, 15.0845f, 12.2865f, 14.9875f, 12f, 14.9875f)
    curveTo(11.7135f, 14.9875f, 11.4728f, 15.0845f, 11.2787f, 15.2787f)
    curveTo(11.0845f, 15.4728f, 10.9875f, 15.7135f, 10.9875f, 16f)
    curveTo(10.9875f, 16.2865f, 11.0845f, 16.5272f, 11.2787f, 16.7213f)
    curveTo(11.4728f, 16.9155f, 11.7135f, 17.0125f, 12f, 17.0125f)
    close()
    moveTo(12f, 21.9875f)
    curveTo(10.6183f, 21.9875f, 9.31999f, 21.7253f, 8.10495f, 21.201f)
    curveTo(6.88968f, 20.6766f, 5.83268f, 19.965f, 4.93384f, 19.0662f)
    curveTo(4.035f, 18.1673f, 3.32338f, 17.1103f, 2.79898f, 15.895f)
    curveTo(2.27468f, 14.68f, 2.0125f, 13.3817f, 2.0125f, 12f)
    curveTo(2.0125f, 10.6183f, 2.27468f, 9.31999f, 2.79898f, 8.10495f)
    curveTo(3.32338f, 6.88968f, 4.035f, 5.83268f, 4.93384f, 4.93384f)
    curveTo(5.83268f, 4.035f, 6.88968f, 3.32338f, 8.10495f, 2.79898f)
    curveTo(9.31999f, 2.27468f, 10.6183f, 2.0125f, 12f, 2.0125f)
    curveTo(13.3817f, 2.0125f, 14.68f, 2.27468f, 15.895f, 2.79898f)
    curveTo(17.1103f, 3.32338f, 18.1673f, 4.035f, 19.0662f, 4.93384f)
    curveTo(19.965f, 5.83268f, 20.6766f, 6.88968f, 21.201f, 8.10495f)
    curveTo(21.7253f, 9.31999f, 21.9875f, 10.6183f, 21.9875f, 12f)
    curveTo(21.9875f, 13.3817f, 21.7253f, 14.68f, 21.201f, 15.895f)
    curveTo(20.6766f, 17.1103f, 19.965f, 18.1673f, 19.0662f, 19.0662f)
    curveTo(18.1673f, 19.965f, 17.1103f, 20.6766f, 15.895f, 21.201f)
    curveTo(14.68f, 21.7253f, 13.3817f, 21.9875f, 12f, 21.9875f)
    close()
  }
}.build()
