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
private fun Testplay_circle() {
  Image(
    imageVector = playCircleIcon(Color.White),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun playCircleIcon(
  color: Color
): ImageVector = ImageVector.Builder(
  name = "play_circle",
  defaultWidth = 40.dp,
  defaultHeight = 40.dp,
  viewportWidth = 40f,
  viewportHeight = 40f,
  tintColor = color,
).apply {
  path(
    fill = SolidColor(color),
  ) {
    moveTo(15.2632f, 28.5021f)
    lineTo(28.5021f, 20f)
    lineTo(15.2632f, 11.4979f)
    verticalLineTo(28.5021f)
    close()
    moveTo(20.0037f, 40f)
    curveTo(17.2374f, 40f, 14.6372f, 39.4751f, 12.2032f, 38.4253f)
    curveTo(9.76912f, 37.3754f, 7.65193f, 35.9507f, 5.85158f, 34.1511f)
    curveTo(4.05123f, 32.3514f, 2.62579f, 30.2351f, 1.57526f, 27.8021f)
    curveTo(0.525087f, 25.3691f, 0f, 22.7696f, 0f, 20.0037f)
    curveTo(0f, 17.2374f, 0.524912f, 14.6372f, 1.57474f, 12.2032f)
    curveTo(2.62456f, 9.76912f, 4.0493f, 7.65193f, 5.84895f, 5.85158f)
    curveTo(7.6486f, 4.05123f, 9.76491f, 2.62579f, 12.1979f, 1.57526f)
    curveTo(14.6309f, 0.525087f, 17.2304f, 0f, 19.9963f, 0f)
    curveTo(22.7626f, 0f, 25.3628f, 0.524911f, 27.7968f, 1.57474f)
    curveTo(30.2309f, 2.62456f, 32.3481f, 4.0493f, 34.1484f, 5.84895f)
    curveTo(35.9488f, 7.6486f, 37.3742f, 9.76491f, 38.4247f, 12.1979f)
    curveTo(39.4749f, 14.6309f, 40f, 17.2304f, 40f, 19.9963f)
    curveTo(40f, 22.7626f, 39.4751f, 25.3628f, 38.4253f, 27.7968f)
    curveTo(37.3754f, 30.2309f, 35.9507f, 32.3481f, 34.1511f, 34.1484f)
    curveTo(32.3514f, 35.9488f, 30.2351f, 37.3742f, 27.8021f, 38.4247f)
    curveTo(25.3691f, 39.4749f, 22.7696f, 40f, 20.0037f, 40f)
    close()
    moveTo(20f, 36.8421f)
    curveTo(24.7018f, 36.8421f, 28.6842f, 35.2105f, 31.9474f, 31.9474f)
    curveTo(35.2105f, 28.6842f, 36.8421f, 24.7018f, 36.8421f, 20f)
    curveTo(36.8421f, 15.2982f, 35.2105f, 11.3158f, 31.9474f, 8.05263f)
    curveTo(28.6842f, 4.78947f, 24.7018f, 3.15789f, 20f, 3.15789f)
    curveTo(15.2982f, 3.15789f, 11.3158f, 4.78947f, 8.05263f, 8.05263f)
    curveTo(4.78947f, 11.3158f, 3.15789f, 15.2982f, 3.15789f, 20f)
    curveTo(3.15789f, 24.7018f, 4.78947f, 28.6842f, 8.05263f, 31.9474f)
    curveTo(11.3158f, 35.2105f, 15.2982f, 36.8421f, 20f, 36.8421f)
    close()
  }
}.build()
