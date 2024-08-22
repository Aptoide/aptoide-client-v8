package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun testGetNotificationsPermissionIcon() {
  Image(
    imageVector = getNotificationsPermissionIcon(Color.Green, Color.White, Color.Red),
    contentDescription = null,
    modifier = Modifier
      .size(240.dp)
      .background(Palette.Black)
  )
}

fun getNotificationsPermissionIcon(
  controllerColor: Color,
  bellColor: Color,
  notificationColor: Color,
): ImageVector =
  ImageVector.Builder(
    name = "NotificationsPermissionIcon",
    defaultWidth = 280.0.dp,
    defaultHeight = 120.0.dp,
    viewportWidth = 280.0f,
    viewportHeight = 120.0f
  ).apply {
    path(
      fill = SolidColor(bellColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(24.0f, 28.0f)
      horizontalLineToRelative(16.0f)
      verticalLineToRelative(8.0f)
      horizontalLineToRelative(-16.0f)
      close()
    }
    path(
      fill = SolidColor(controllerColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(198.0f, 16.0f)
      horizontalLineToRelative(58.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-58.0f)
      close()
    }
    path(
      fill = SolidColor(bellColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(233.0f, 64.0f)
      horizontalLineToRelative(23.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-23.0f)
      close()
    }
    path(
      fill = SolidColor(notificationColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(40.0f, 0.0f)
      horizontalLineToRelative(23.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-23.0f)
      close()
    }
    path(
      fill = SolidColor(controllerColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(56.0f, 104.0f)
      horizontalLineToRelative(16.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-16.0f)
      close()
    }
    path(
      fill = SolidColor(controllerColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(101.958f, 93.667f)
      lineTo(109.108f, 42.333f)
      horizontalLineTo(170.891f)
      lineTo(178.041f, 93.667f)
      horizontalLineTo(163.466f)
      lineTo(152.466f, 82.667f)
      horizontalLineTo(127.533f)
      lineTo(116.533f, 93.667f)
      horizontalLineTo(101.958f)
      close()
      moveTo(158.333f, 71.667f)
      curveTo(159.372f, 71.667f, 160.243f, 71.315f, 160.946f, 70.613f)
      curveTo(161.648f, 69.91f, 162.0f, 69.039f, 162.0f, 68.0f)
      curveTo(162.0f, 66.961f, 161.648f, 66.09f, 160.946f, 65.387f)
      curveTo(160.243f, 64.685f, 159.372f, 64.333f, 158.333f, 64.333f)
      curveTo(157.294f, 64.333f, 156.423f, 64.685f, 155.721f, 65.387f)
      curveTo(155.018f, 66.09f, 154.666f, 66.961f, 154.666f, 68.0f)
      curveTo(154.666f, 69.039f, 155.018f, 69.91f, 155.721f, 70.613f)
      curveTo(156.423f, 71.315f, 157.294f, 71.667f, 158.333f, 71.667f)
      close()
      moveTo(151.0f, 60.667f)
      curveTo(152.039f, 60.667f, 152.909f, 60.315f, 153.612f, 59.612f)
      curveTo(154.315f, 58.91f, 154.666f, 58.039f, 154.666f, 57.0f)
      curveTo(154.666f, 55.961f, 154.315f, 55.09f, 153.612f, 54.388f)
      curveTo(152.909f, 53.685f, 152.039f, 53.333f, 151.0f, 53.333f)
      curveTo(149.961f, 53.333f, 149.09f, 53.685f, 148.387f, 54.388f)
      curveTo(147.684f, 55.09f, 147.333f, 55.961f, 147.333f, 57.0f)
      curveTo(147.333f, 58.039f, 147.684f, 58.91f, 148.387f, 59.612f)
      curveTo(149.09f, 60.315f, 149.961f, 60.667f, 151.0f, 60.667f)
      close()
      moveTo(124.416f, 71.667f)
      horizontalLineTo(129.916f)
      verticalLineTo(65.25f)
      horizontalLineTo(136.333f)
      verticalLineTo(59.75f)
      horizontalLineTo(129.916f)
      verticalLineTo(53.333f)
      horizontalLineTo(124.416f)
      verticalLineTo(59.75f)
      horizontalLineTo(118.0f)
      verticalLineTo(65.25f)
      horizontalLineTo(124.416f)
      verticalLineTo(71.667f)
      close()
    }
    path(
      fill = SolidColor(bellColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(177.0f, 108.333f)
      curveTo(175.716f, 108.333f, 174.618f, 107.876f, 173.704f, 106.962f)
      curveTo(172.79f, 106.049f, 172.333f, 104.95f, 172.333f, 103.667f)
      horizontalLineTo(181.666f)
      curveTo(181.666f, 104.95f, 181.209f, 106.049f, 180.296f, 106.962f)
      curveTo(179.382f, 107.876f, 178.283f, 108.333f, 177.0f, 108.333f)
      close()
      moveTo(158.333f, 101.333f)
      verticalLineTo(96.667f)
      horizontalLineTo(163.0f)
      verticalLineTo(80.333f)
      curveTo(163.0f, 77.105f, 163.972f, 74.238f, 165.916f, 71.729f)
      curveTo(167.861f, 69.221f, 170.389f, 67.578f, 173.5f, 66.8f)
      verticalLineTo(61.667f)
      horizontalLineTo(180.5f)
      verticalLineTo(65.925f)
      curveTo(180.111f, 66.703f, 179.819f, 67.519f, 179.625f, 68.375f)
      curveTo(179.43f, 69.23f, 179.333f, 70.105f, 179.333f, 71.0f)
      curveTo(179.333f, 74.228f, 180.471f, 76.979f, 182.746f, 79.254f)
      curveTo(185.021f, 81.529f, 187.772f, 82.667f, 191.0f, 82.667f)
      verticalLineTo(96.667f)
      horizontalLineTo(195.666f)
      verticalLineTo(101.333f)
      horizontalLineTo(158.333f)
      close()
    }
    path(
      fill = SolidColor(notificationColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(185.042f, 75.958f)
      curveTo(186.403f, 77.319f, 188.056f, 78.0f, 190.0f, 78.0f)
      curveTo(191.944f, 78.0f, 193.597f, 77.319f, 194.958f, 75.958f)
      curveTo(196.319f, 74.597f, 197.0f, 72.944f, 197.0f, 71.0f)
      curveTo(197.0f, 69.056f, 196.319f, 67.403f, 194.958f, 66.042f)
      curveTo(193.597f, 64.681f, 191.944f, 64.0f, 190.0f, 64.0f)
      curveTo(188.056f, 64.0f, 186.403f, 64.681f, 185.042f, 66.042f)
      curveTo(183.681f, 67.403f, 183.0f, 69.056f, 183.0f, 71.0f)
      curveTo(183.0f, 72.944f, 183.681f, 74.597f, 185.042f, 75.958f)
      close()
    }
  }.build()
