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
fun TestFireIcon() {
  Image(
    imageVector = getFireIcon(Color.Green),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getFireIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "fire_icon",
  defaultWidth = 32.dp,
  defaultHeight = 32.dp,
  viewportWidth = 32f,
  viewportHeight = 32f,
  tintColor = Color.Unspecified,
).apply {
//<mask id="mask0_21163_68099" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="32" height="32">
//<rect width="32" height="32" fill="#D9D9D9"/>
//</mask>
//<g mask="url(#mask0_21163_68099)">
  path(
    fill = SolidColor(color),
  ) {
    moveTo(8.00016f, 18.6667f)
    curveTo(8.00016f, 19.8222f, 8.2335f, 20.9167f, 8.70016f, 21.95f)
    curveTo(9.16683f, 22.9833f, 9.8335f, 23.8889f, 10.7002f, 24.6667f)
    curveTo(10.6779f, 24.5556f, 10.6668f, 24.4556f, 10.6668f, 24.3667f)
    verticalLineTo(24.0667f)
    curveTo(10.6668f, 23.3556f, 10.8002f, 22.6889f, 11.0668f, 22.0667f)
    curveTo(11.3335f, 21.4444f, 11.7224f, 20.8778f, 12.2335f, 20.3667f)
    lineTo(16.0002f, 16.6667f)
    lineTo(19.7668f, 20.3667f)
    curveTo(20.2779f, 20.8778f, 20.6668f, 21.4444f, 20.9335f, 22.0667f)
    curveTo(21.2002f, 22.6889f, 21.3335f, 23.3556f, 21.3335f, 24.0667f)
    verticalLineTo(24.3667f)
    curveTo(21.3335f, 24.4556f, 21.3224f, 24.5556f, 21.3002f, 24.6667f)
    curveTo(22.1668f, 23.8889f, 22.8335f, 22.9833f, 23.3002f, 21.95f)
    curveTo(23.7668f, 20.9167f, 24.0002f, 19.8222f, 24.0002f, 18.6667f)
    curveTo(24.0002f, 17.5556f, 23.7946f, 16.5056f, 23.3835f, 15.5167f)
    curveTo(22.9724f, 14.5278f, 22.3779f, 13.6444f, 21.6002f, 12.8667f)
    curveTo(21.1557f, 13.1556f, 20.6891f, 13.3722f, 20.2002f, 13.5167f)
    curveTo(19.7113f, 13.6611f, 19.2113f, 13.7333f, 18.7002f, 13.7333f)
    curveTo(17.3224f, 13.7333f, 16.1279f, 13.2778f, 15.1168f, 12.3667f)
    curveTo(14.1057f, 11.4556f, 13.5224f, 10.3333f, 13.3668f, 9f)
    curveTo(12.5002f, 9.73333f, 11.7335f, 10.4944f, 11.0668f, 11.2833f)
    curveTo(10.4002f, 12.0722f, 9.83905f, 12.8722f, 9.3835f, 13.6833f)
    curveTo(8.92794f, 14.4944f, 8.5835f, 15.3222f, 8.35016f, 16.1667f)
    curveTo(8.11683f, 17.0111f, 8.00016f, 17.8444f, 8.00016f, 18.6667f)
    close()
    moveTo(16.0002f, 4f)
    verticalLineTo(8.4f)
    curveTo(16.0002f, 9.15556f, 16.2613f, 9.78889f, 16.7835f, 10.3f)
    curveTo(17.3057f, 10.8111f, 17.9446f, 11.0667f, 18.7002f, 11.0667f)
    curveTo(19.1002f, 11.0667f, 19.4724f, 10.9833f, 19.8168f, 10.8167f)
    curveTo(20.1613f, 10.65f, 20.4668f, 10.4f, 20.7335f, 10.0667f)
    lineTo(21.3335f, 9.33333f)
    curveTo(22.9779f, 10.2667f, 24.2779f, 11.5667f, 25.2335f, 13.2333f)
    curveTo(26.1891f, 14.9f, 26.6668f, 16.7111f, 26.6668f, 18.6667f)
    curveTo(26.6668f, 21.6444f, 25.6335f, 24.1667f, 23.5668f, 26.2333f)
    curveTo(21.5002f, 28.3f, 18.9779f, 29.3333f, 16.0002f, 29.3333f)
    curveTo(13.0224f, 29.3333f, 10.5002f, 28.3f, 8.4335f, 26.2333f)
    curveTo(6.36683f, 24.1667f, 5.3335f, 21.6444f, 5.3335f, 18.6667f)
    curveTo(5.3335f, 15.8f, 6.29461f, 13.0778f, 8.21683f, 10.5f)
    curveTo(10.1391f, 7.92222f, 12.7335f, 5.75556f, 16.0002f, 4f)
    close()
  }
//</g>
}.build()
