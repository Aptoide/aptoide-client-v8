package cm.aptoide.pt.app_games.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.theme.alertRed
import cm.aptoide.pt.app_games.theme.pureBlack

@Preview
@Composable
fun TestNotificationBell() {
  Image(
    imageVector = getNotificationBell(pureBlack),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getNotificationBell(bellColor: Color): ImageVector = ImageVector.Builder(
  name = "NotificationBell",
  defaultWidth = 13.dp,
  defaultHeight = 17.dp,
  viewportWidth = 13f,
  viewportHeight = 17f,
).apply {
  path(
    pathFillType = PathFillType.EvenOdd,
    fill = SolidColor(bellColor),
  ) {
    moveTo(0.172656f, 14.2694f)
    curveTo(0.28776f, 14.3898f, 0.433333f, 14.45f, 0.609375f, 14.45f)
    horizontalLineTo(12.3906f)
    curveTo(12.5667f, 14.45f, 12.7122f, 14.3898f, 12.8273f, 14.2694f)
    curveTo(12.9424f, 14.149f, 13f, 13.9967f, 13f, 13.8125f)
    curveTo(13f, 13.6283f, 12.9424f, 13.476f, 12.8273f, 13.3556f)
    curveTo(12.7122f, 13.2352f, 12.5667f, 13.175f, 12.3906f, 13.175f)
    curveTo(11.7961f, 13.175f, 11.3141f, 12.693f, 11.3141f, 12.0984f)
    verticalLineTo(6.98356f)
    curveTo(11.2108f, 6.99443f, 11.106f, 7f, 11f, 7f)
    curveTo(9.35f, 7f, 8f, 5.65f, 8f, 4f)
    curveTo(8f, 3.28371f, 8.25442f, 2.62396f, 8.67734f, 2.10665f)
    curveTo(8.35468f, 1.94525f, 8.00807f, 1.82386f, 7.6375f, 1.7425f)
    verticalLineTo(1.12625f)
    curveTo(7.6375f, 0.800417f, 7.52578f, 0.53125f, 7.30234f, 0.31875f)
    curveTo(7.07891f, 0.10625f, 6.81146f, 0f, 6.5f, 0f)
    curveTo(6.18854f, 0f, 5.92109f, 0.10625f, 5.69766f, 0.31875f)
    curveTo(5.47422f, 0.53125f, 5.3625f, 0.800417f, 5.3625f, 1.12625f)
    verticalLineTo(1.7425f)
    curveTo(4.26562f, 1.98333f, 3.38203f, 2.57479f, 2.71172f, 3.51688f)
    curveTo(2.04141f, 4.45896f, 1.70625f, 5.51083f, 1.70625f, 6.6725f)
    verticalLineTo(12.0781f)
    curveTo(1.70625f, 12.6839f, 1.21516f, 13.175f, 0.609375f, 13.175f)
    curveTo(0.433333f, 13.175f, 0.28776f, 13.2352f, 0.172656f, 13.3556f)
    curveTo(0.0575521f, 13.476f, 0f, 13.6283f, 0f, 13.8125f)
    curveTo(0f, 13.9967f, 0.0575521f, 14.149f, 0.172656f, 14.2694f)
    close()
    moveTo(5.3625f, 16.5006f)
    curveTo(5.6875f, 16.8335f, 6.06667f, 17f, 6.5f, 17f)
    curveTo(6.94688f, 17f, 7.32943f, 16.8335f, 7.64766f, 16.5006f)
    curveTo(7.96589f, 16.1677f, 8.125f, 15.7675f, 8.125f, 15.3f)
    horizontalLineTo(4.875f)
    curveTo(4.875f, 15.7675f, 5.0375f, 16.1677f, 5.3625f, 16.5006f)
    close()
  }
  path(
    fill = SolidColor(alertRed),
  ) {
    moveTo(11f, 6f)
    curveTo(12.1f, 6f, 13f, 5.1f, 13f, 4f)
    curveTo(13f, 2.9f, 12.1f, 2f, 11f, 2f)
    curveTo(9.9f, 2f, 9f, 2.9f, 9f, 4f)
    curveTo(9f, 5.1f, 9.9f, 6f, 11f, 6f)
    close()
  }
}.build()
