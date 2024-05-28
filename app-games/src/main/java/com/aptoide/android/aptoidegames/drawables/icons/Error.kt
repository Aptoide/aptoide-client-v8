package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestError() {
  Image(
    imageVector = getError(Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getError(color: Color): ImageVector = ImageVector.Builder(
  name = "Error",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(color),
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
}.build()
