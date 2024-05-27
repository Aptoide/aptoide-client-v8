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
fun TestPaymentsErrorIcon() {
  Image(
    imageVector = getPaymentsErrorIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPaymentsErrorIcon(): ImageVector = ImageVector.Builder(
  name = "PaymentsErrorIcon",
  defaultWidth = 64.dp,
  defaultHeight = 64.dp,
  viewportWidth = 64f,
  viewportHeight = 64f,
).apply {
  path(
    fill = SolidColor(Palette.Error),
  ) {
    moveTo(28.8f, 41.6f)
    horizontalLineTo(35.2f)
    verticalLineTo(48f)
    horizontalLineTo(28.8f)
    verticalLineTo(41.6f)
    close()
    moveTo(28.8f, 16f)
    horizontalLineTo(35.2f)
    verticalLineTo(35.2f)
    horizontalLineTo(28.8f)
    verticalLineTo(16f)
    close()
    moveTo(32f, 0f)
    curveTo(25.671f, 0f, 19.4841f, 1.87677f, 14.2218f, 5.39297f)
    curveTo(8.95939f, 8.90918f, 4.85787f, 13.9069f, 2.43587f, 19.7541f)
    curveTo(0.0138651f, 25.6014f, -0.619842f, 32.0355f, 0.614886f, 38.2429f)
    curveTo(1.84961f, 44.4503f, 4.89732f, 50.1521f, 9.3726f, 54.6274f)
    curveTo(13.8479f, 59.1027f, 19.5497f, 62.1504f, 25.7571f, 63.3851f)
    curveTo(31.9645f, 64.6199f, 38.3987f, 63.9862f, 44.2459f, 61.5642f)
    curveTo(50.0931f, 59.1421f, 55.0908f, 55.0406f, 58.607f, 49.7783f)
    curveTo(62.1232f, 44.5159f, 64f, 38.329f, 64f, 32f)
    curveTo(64f, 27.7977f, 63.1723f, 23.6366f, 61.5642f, 19.7541f)
    curveTo(59.956f, 15.8717f, 57.5989f, 12.3441f, 54.6274f, 9.37258f)
    curveTo(51.656f, 6.40111f, 48.1283f, 4.04401f, 44.2459f, 2.43586f)
    curveTo(40.3635f, 0.827705f, 36.2023f, 0f, 32f, 0f)
    close()
    moveTo(32f, 57.6f)
    curveTo(26.9368f, 57.6f, 21.9873f, 56.0986f, 17.7774f, 53.2856f)
    curveTo(13.5675f, 50.4727f, 10.2863f, 46.4745f, 8.3487f, 41.7967f)
    curveTo(6.4111f, 37.1189f, 5.90413f, 31.9716f, 6.89191f, 27.0057f)
    curveTo(7.87969f, 22.0398f, 10.3179f, 17.4783f, 13.8981f, 13.8981f)
    curveTo(17.4783f, 10.3178f, 22.0398f, 7.87968f, 27.0057f, 6.8919f)
    curveTo(31.9716f, 5.90412f, 37.1189f, 6.41108f, 41.7967f, 8.34869f)
    curveTo(46.4745f, 10.2863f, 50.4727f, 13.5675f, 53.2856f, 17.7774f)
    curveTo(56.0986f, 21.9873f, 57.6f, 26.9368f, 57.6f, 32f)
    curveTo(57.6f, 38.7895f, 54.9029f, 45.301f, 50.102f, 50.1019f)
    curveTo(45.301f, 54.9029f, 38.7896f, 57.6f, 32f, 57.6f)
    close()
  }
}.build()
