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
fun TestNotificationBell() {
  Image(
    imageVector = getNotificationBell(Color.White, Color.Red),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getNotificationBell(
  bellColor: Color,
  notificationColor: Color
): ImageVector = ImageVector.Builder(
  name = "NotificationBell",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(bellColor),
  ) {
    moveTo(12f, 22f)
    curveTo(11.45f, 22f, 10.9792f, 21.8042f, 10.5875f, 21.4125f)
    curveTo(10.1958f, 21.0208f, 10f, 20.55f, 10f, 20f)
    horizontalLineTo(14f)
    curveTo(14f, 20.55f, 13.8042f, 21.0208f, 13.4125f, 21.4125f)
    curveTo(13.0208f, 21.8042f, 12.55f, 22f, 12f, 22f)
    close()
    moveTo(4f, 19f)
    verticalLineTo(17f)
    horizontalLineTo(6f)
    verticalLineTo(10f)
    curveTo(6f, 8.61667f, 6.41667f, 7.3875f, 7.25f, 6.3125f)
    curveTo(8.08333f, 5.2375f, 9.16667f, 4.53333f, 10.5f, 4.2f)
    verticalLineTo(2f)
    horizontalLineTo(13.5f)
    verticalLineTo(3.825f)
    curveTo(13.3333f, 4.15833f, 13.2083f, 4.50833f, 13.125f, 4.875f)
    curveTo(13.0417f, 5.24167f, 13f, 5.61667f, 13f, 6f)
    curveTo(13f, 7.38333f, 13.4875f, 8.5625f, 14.4625f, 9.5375f)
    curveTo(15.4375f, 10.5125f, 16.6167f, 11f, 18f, 11f)
    verticalLineTo(17f)
    horizontalLineTo(20f)
    verticalLineTo(19f)
    horizontalLineTo(4f)
    close()
  }
  path(
    fill = SolidColor(notificationColor),
  ) {
    moveTo(15.875f, 8.125f)
    curveTo(16.4583f, 8.70833f, 17.1667f, 9f, 18f, 9f)
    curveTo(18.8333f, 9f, 19.5417f, 8.70833f, 20.125f, 8.125f)
    curveTo(20.7083f, 7.54167f, 21f, 6.83333f, 21f, 6f)
    curveTo(21f, 5.16667f, 20.7083f, 4.45833f, 20.125f, 3.875f)
    curveTo(19.5417f, 3.29167f, 18.8333f, 3f, 18f, 3f)
    curveTo(17.1667f, 3f, 16.4583f, 3.29167f, 15.875f, 3.875f)
    curveTo(15.2917f, 4.45833f, 15f, 5.16667f, 15f, 6f)
    curveTo(15f, 6.83333f, 15.2917f, 7.54167f, 15.875f, 8.125f)
    close()
  }
}.build()
