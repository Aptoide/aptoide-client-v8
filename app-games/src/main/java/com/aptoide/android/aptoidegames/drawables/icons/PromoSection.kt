package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestPromoSection() {
  Image(
    imageVector = getPromoSection(Color.Green, Color.Black, Color.Magenta, Color.Gray),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPromoSection(
  iconColor: Color,
  outlineColor: Color,
  backgroundColor: Color,
  themeColor: Color,
): ImageVector = ImageVector.Builder(
  name = "PromoSection",
  defaultWidth = 360.0.dp,
  defaultHeight = 146.0.dp,
  viewportWidth = 360.0f,
  viewportHeight = 146.0f
).apply {
  group {
    path(
      fill = SolidColor(themeColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(0.0f, 0.0f)
      horizontalLineToRelative(360.0f)
      verticalLineToRelative(146.0f)
      horizontalLineToRelative(-360.0f)
      close()
    }
    path(
      fill = SolidColor(backgroundColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(0.0f, 0.0f)
      horizontalLineToRelative(360.0f)
      verticalLineToRelative(140.0f)
      horizontalLineToRelative(-360.0f)
      close()
    }
    path(
      fill = SolidColor(backgroundColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = EvenOdd
    ) {
      moveTo(350.0f, 140.0f)
      verticalLineTo(119.0f)
      horizontalLineTo(345.0f)
      verticalLineTo(106.0f)
      horizontalLineTo(310.0f)
      verticalLineTo(140.0f)
      horizontalLineTo(316.0f)
      verticalLineTo(146.0f)
      horizontalLineTo(337.0f)
      verticalLineTo(140.0f)
      horizontalLineTo(350.0f)
      close()
    }
  }
  group {
    path(
      fill = SolidColor(iconColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(338.889f, 124.889f)
      verticalLineTo(137.111f)
      horizontalLineTo(321.111f)
      verticalLineTo(124.889f)
      horizontalLineTo(318.889f)
      verticalLineTo(118.222f)
      horizontalLineTo(324.667f)
      curveTo(324.593f, 118.037f, 324.537f, 117.857f, 324.5f, 117.681f)
      curveTo(324.463f, 117.505f, 324.445f, 117.315f, 324.445f, 117.111f)
      curveTo(324.445f, 116.185f, 324.769f, 115.398f, 325.417f, 114.75f)
      curveTo(326.065f, 114.102f, 326.852f, 113.778f, 327.778f, 113.778f)
      curveTo(328.204f, 113.778f, 328.602f, 113.852f, 328.972f, 114.0f)
      curveTo(329.343f, 114.148f, 329.685f, 114.37f, 330.0f, 114.667f)
      curveTo(330.315f, 114.389f, 330.658f, 114.171f, 331.028f, 114.014f)
      curveTo(331.398f, 113.857f, 331.797f, 113.778f, 332.222f, 113.778f)
      curveTo(333.148f, 113.778f, 333.935f, 114.102f, 334.584f, 114.75f)
      curveTo(335.232f, 115.398f, 335.556f, 116.185f, 335.556f, 117.111f)
      curveTo(335.556f, 117.315f, 335.542f, 117.509f, 335.514f, 117.694f)
      curveTo(335.486f, 117.88f, 335.426f, 118.056f, 335.334f, 118.222f)
      horizontalLineTo(341.111f)
      verticalLineTo(124.889f)
      horizontalLineTo(338.889f)
      close()
    }
    path(
      fill = SolidColor(outlineColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(338.889f, 137.111f)
      verticalLineTo(124.889f)
      horizontalLineTo(341.111f)
      verticalLineTo(118.222f)
      horizontalLineTo(335.334f)
      curveTo(335.426f, 118.056f, 335.486f, 117.88f, 335.514f, 117.694f)
      curveTo(335.542f, 117.509f, 335.556f, 117.315f, 335.556f, 117.111f)
      curveTo(335.556f, 116.185f, 335.232f, 115.398f, 334.584f, 114.75f)
      curveTo(333.935f, 114.102f, 333.148f, 113.778f, 332.222f, 113.778f)
      curveTo(331.797f, 113.778f, 331.398f, 113.857f, 331.028f, 114.014f)
      curveTo(330.658f, 114.171f, 330.315f, 114.389f, 330.0f, 114.667f)
      curveTo(329.685f, 114.37f, 329.343f, 114.148f, 328.972f, 114.0f)
      curveTo(328.602f, 113.852f, 328.204f, 113.778f, 327.778f, 113.778f)
      curveTo(326.852f, 113.778f, 326.065f, 114.102f, 325.417f, 114.75f)
      curveTo(324.769f, 115.398f, 324.445f, 116.185f, 324.445f, 117.111f)
      curveTo(324.445f, 117.315f, 324.463f, 117.505f, 324.5f, 117.681f)
      curveTo(324.537f, 117.857f, 324.593f, 118.037f, 324.667f, 118.222f)
      horizontalLineTo(318.889f)
      verticalLineTo(124.889f)
      horizontalLineTo(321.111f)
      verticalLineTo(137.111f)
      horizontalLineTo(338.889f)
      close()
      moveTo(327.778f, 116.0f)
      curveTo(328.093f, 116.0f, 328.357f, 116.107f, 328.57f, 116.319f)
      curveTo(328.783f, 116.532f, 328.889f, 116.796f, 328.889f, 117.111f)
      curveTo(328.889f, 117.426f, 328.783f, 117.69f, 328.57f, 117.903f)
      curveTo(328.357f, 118.116f, 328.093f, 118.222f, 327.778f, 118.222f)
      curveTo(327.463f, 118.222f, 327.199f, 118.116f, 326.986f, 117.903f)
      curveTo(326.773f, 117.69f, 326.667f, 117.426f, 326.667f, 117.111f)
      curveTo(326.667f, 116.796f, 326.773f, 116.532f, 326.986f, 116.319f)
      curveTo(327.199f, 116.107f, 327.463f, 116.0f, 327.778f, 116.0f)
      close()
      moveTo(333.334f, 117.111f)
      curveTo(333.334f, 117.426f, 333.227f, 117.69f, 333.014f, 117.903f)
      curveTo(332.801f, 118.116f, 332.537f, 118.222f, 332.222f, 118.222f)
      curveTo(331.908f, 118.222f, 331.644f, 118.116f, 331.431f, 117.903f)
      curveTo(331.218f, 117.69f, 331.111f, 117.426f, 331.111f, 117.111f)
      curveTo(331.111f, 116.796f, 331.218f, 116.532f, 331.431f, 116.319f)
      curveTo(331.644f, 116.107f, 331.908f, 116.0f, 332.222f, 116.0f)
      curveTo(332.537f, 116.0f, 332.801f, 116.107f, 333.014f, 116.319f)
      curveTo(333.227f, 116.532f, 333.334f, 116.796f, 333.334f, 117.111f)
      close()
      moveTo(338.889f, 120.444f)
      verticalLineTo(122.667f)
      horizontalLineTo(331.111f)
      verticalLineTo(120.444f)
      horizontalLineTo(338.889f)
      close()
      moveTo(331.111f, 134.889f)
      verticalLineTo(124.889f)
      horizontalLineTo(336.667f)
      verticalLineTo(134.889f)
      horizontalLineTo(331.111f)
      close()
      moveTo(328.889f, 134.889f)
      horizontalLineTo(323.334f)
      verticalLineTo(124.889f)
      horizontalLineTo(328.889f)
      verticalLineTo(134.889f)
      close()
      moveTo(321.111f, 122.667f)
      verticalLineTo(120.444f)
      horizontalLineTo(328.889f)
      verticalLineTo(122.667f)
      horizontalLineTo(321.111f)
      close()
    }
    path(
      fill = SolidColor(themeColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(0.0f, 0.0f)
      horizontalLineToRelative(16.0f)
      verticalLineToRelative(13.0f)
      horizontalLineToRelative(-16.0f)
      close()
    }
    path(
      fill = SolidColor(themeColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(56.0f, 0.0f)
      horizontalLineToRelative(80.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-80.0f)
      close()
    }
    path(
      fill = SolidColor(themeColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(284.0f, 124.0f)
      horizontalLineToRelative(20.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-20.0f)
      close()
    }
    path(
      fill = SolidColor(themeColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(128.0f, 132.0f)
      horizontalLineToRelative(32.0f)
      verticalLineToRelative(8.0f)
      horizontalLineToRelative(-32.0f)
      close()
    }
    path(
      fill = SolidColor(themeColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(120.0f, 16.0f)
      horizontalLineToRelative(16.0f)
      verticalLineToRelative(16.0f)
      horizontalLineToRelative(-16.0f)
      close()
    }
  }
  group {
    path(
      fill = SolidColor(backgroundColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = EvenOdd
    ) {
      moveTo(56.0f, 34.0f)
      verticalLineTo(13.0f)
      horizontalLineTo(51.0f)
      verticalLineTo(0.0f)
      horizontalLineTo(16.0f)
      verticalLineTo(34.0f)
      horizontalLineTo(22.0f)
      verticalLineTo(40.0f)
      horizontalLineTo(43.0f)
      verticalLineTo(34.0f)
      horizontalLineTo(56.0f)
      close()
    }
  }
  group {
    path(
      fill = SolidColor(iconColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(44.889f, 18.889f)
      verticalLineTo(31.111f)
      horizontalLineTo(27.111f)
      verticalLineTo(18.889f)
      horizontalLineTo(24.889f)
      verticalLineTo(12.222f)
      horizontalLineTo(30.667f)
      curveTo(30.593f, 12.037f, 30.537f, 11.856f, 30.5f, 11.681f)
      curveTo(30.463f, 11.505f, 30.445f, 11.315f, 30.445f, 11.111f)
      curveTo(30.445f, 10.185f, 30.769f, 9.398f, 31.417f, 8.75f)
      curveTo(32.065f, 8.102f, 32.852f, 7.778f, 33.778f, 7.778f)
      curveTo(34.204f, 7.778f, 34.602f, 7.852f, 34.972f, 8.0f)
      curveTo(35.343f, 8.148f, 35.685f, 8.37f, 36.0f, 8.667f)
      curveTo(36.315f, 8.389f, 36.658f, 8.171f, 37.028f, 8.014f)
      curveTo(37.398f, 7.857f, 37.797f, 7.778f, 38.222f, 7.778f)
      curveTo(39.148f, 7.778f, 39.935f, 8.102f, 40.584f, 8.75f)
      curveTo(41.232f, 9.398f, 41.556f, 10.185f, 41.556f, 11.111f)
      curveTo(41.556f, 11.315f, 41.542f, 11.509f, 41.514f, 11.694f)
      curveTo(41.486f, 11.88f, 41.426f, 12.056f, 41.333f, 12.222f)
      horizontalLineTo(47.111f)
      verticalLineTo(18.889f)
      horizontalLineTo(44.889f)
      close()
    }
    path(
      fill = SolidColor(outlineColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(44.889f, 31.111f)
      verticalLineTo(18.889f)
      horizontalLineTo(47.111f)
      verticalLineTo(12.222f)
      horizontalLineTo(41.333f)
      curveTo(41.426f, 12.056f, 41.486f, 11.88f, 41.514f, 11.694f)
      curveTo(41.542f, 11.509f, 41.556f, 11.315f, 41.556f, 11.111f)
      curveTo(41.556f, 10.185f, 41.232f, 9.398f, 40.584f, 8.75f)
      curveTo(39.935f, 8.102f, 39.148f, 7.778f, 38.222f, 7.778f)
      curveTo(37.797f, 7.778f, 37.398f, 7.857f, 37.028f, 8.014f)
      curveTo(36.658f, 8.171f, 36.315f, 8.389f, 36.0f, 8.667f)
      curveTo(35.685f, 8.37f, 35.343f, 8.148f, 34.972f, 8.0f)
      curveTo(34.602f, 7.852f, 34.204f, 7.778f, 33.778f, 7.778f)
      curveTo(32.852f, 7.778f, 32.065f, 8.102f, 31.417f, 8.75f)
      curveTo(30.769f, 9.398f, 30.445f, 10.185f, 30.445f, 11.111f)
      curveTo(30.445f, 11.315f, 30.463f, 11.505f, 30.5f, 11.681f)
      curveTo(30.537f, 11.856f, 30.593f, 12.037f, 30.667f, 12.222f)
      horizontalLineTo(24.889f)
      verticalLineTo(18.889f)
      horizontalLineTo(27.111f)
      verticalLineTo(31.111f)
      horizontalLineTo(44.889f)
      close()
      moveTo(33.778f, 10.0f)
      curveTo(34.093f, 10.0f, 34.357f, 10.106f, 34.57f, 10.319f)
      curveTo(34.783f, 10.533f, 34.889f, 10.796f, 34.889f, 11.111f)
      curveTo(34.889f, 11.426f, 34.783f, 11.69f, 34.57f, 11.903f)
      curveTo(34.357f, 12.116f, 34.093f, 12.222f, 33.778f, 12.222f)
      curveTo(33.463f, 12.222f, 33.199f, 12.116f, 32.986f, 11.903f)
      curveTo(32.773f, 11.69f, 32.667f, 11.426f, 32.667f, 11.111f)
      curveTo(32.667f, 10.796f, 32.773f, 10.533f, 32.986f, 10.319f)
      curveTo(33.199f, 10.106f, 33.463f, 10.0f, 33.778f, 10.0f)
      close()
      moveTo(39.334f, 11.111f)
      curveTo(39.334f, 11.426f, 39.227f, 11.69f, 39.014f, 11.903f)
      curveTo(38.801f, 12.116f, 38.537f, 12.222f, 38.222f, 12.222f)
      curveTo(37.908f, 12.222f, 37.644f, 12.116f, 37.431f, 11.903f)
      curveTo(37.218f, 11.69f, 37.111f, 11.426f, 37.111f, 11.111f)
      curveTo(37.111f, 10.796f, 37.218f, 10.533f, 37.431f, 10.319f)
      curveTo(37.644f, 10.106f, 37.908f, 10.0f, 38.222f, 10.0f)
      curveTo(38.537f, 10.0f, 38.801f, 10.106f, 39.014f, 10.319f)
      curveTo(39.227f, 10.533f, 39.334f, 10.796f, 39.334f, 11.111f)
      close()
      moveTo(44.889f, 14.444f)
      verticalLineTo(16.667f)
      horizontalLineTo(37.111f)
      verticalLineTo(14.444f)
      horizontalLineTo(44.889f)
      close()
      moveTo(37.111f, 28.889f)
      verticalLineTo(18.889f)
      horizontalLineTo(42.667f)
      verticalLineTo(28.889f)
      horizontalLineTo(37.111f)
      close()
      moveTo(34.889f, 28.889f)
      horizontalLineTo(29.334f)
      verticalLineTo(18.889f)
      horizontalLineTo(34.889f)
      verticalLineTo(28.889f)
      close()
      moveTo(27.111f, 16.667f)
      verticalLineTo(14.444f)
      horizontalLineTo(34.889f)
      verticalLineTo(16.667f)
      horizontalLineTo(27.111f)
      close()
    }
  }
  group {
    path(
      fill = SolidColor(iconColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(257.334f, -2.083f)
      verticalLineTo(9.834f)
      horizontalLineTo(240.0f)
      verticalLineTo(-2.083f)
      horizontalLineTo(237.834f)
      verticalLineTo(-8.583f)
      horizontalLineTo(243.467f)
      curveTo(243.395f, -8.763f, 243.341f, -8.939f, 243.305f, -9.111f)
      curveTo(243.269f, -9.282f, 243.25f, -9.467f, 243.25f, -9.666f)
      curveTo(243.25f, -10.569f, 243.566f, -11.336f, 244.198f, -11.968f)
      curveTo(244.83f, -12.6f, 245.598f, -12.916f, 246.5f, -12.916f)
      curveTo(246.916f, -12.916f, 247.304f, -12.844f, 247.665f, -12.699f)
      curveTo(248.026f, -12.555f, 248.36f, -12.338f, 248.667f, -12.049f)
      curveTo(248.974f, -12.32f, 249.308f, -12.532f, 249.669f, -12.686f)
      curveTo(250.03f, -12.839f, 250.419f, -12.916f, 250.834f, -12.916f)
      curveTo(251.737f, -12.916f, 252.504f, -12.6f, 253.136f, -11.968f)
      curveTo(253.768f, -11.336f, 254.084f, -10.569f, 254.084f, -9.666f)
      curveTo(254.084f, -9.467f, 254.07f, -9.278f, 254.043f, -9.097f)
      curveTo(254.016f, -8.917f, 253.957f, -8.745f, 253.867f, -8.583f)
      horizontalLineTo(259.5f)
      verticalLineTo(-2.083f)
      horizontalLineTo(257.334f)
      close()
    }
    path(
      fill = SolidColor(outlineColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(257.334f, 9.834f)
      verticalLineTo(-2.083f)
      horizontalLineTo(259.5f)
      verticalLineTo(-8.583f)
      horizontalLineTo(253.867f)
      curveTo(253.957f, -8.745f, 254.016f, -8.917f, 254.043f, -9.097f)
      curveTo(254.07f, -9.278f, 254.084f, -9.467f, 254.084f, -9.666f)
      curveTo(254.084f, -10.569f, 253.768f, -11.336f, 253.136f, -11.968f)
      curveTo(252.504f, -12.6f, 251.737f, -12.916f, 250.834f, -12.916f)
      curveTo(250.419f, -12.916f, 250.03f, -12.839f, 249.669f, -12.686f)
      curveTo(249.308f, -12.532f, 248.974f, -12.32f, 248.667f, -12.049f)
      curveTo(248.36f, -12.338f, 248.026f, -12.555f, 247.665f, -12.699f)
      curveTo(247.304f, -12.844f, 246.916f, -12.916f, 246.5f, -12.916f)
      curveTo(245.598f, -12.916f, 244.83f, -12.6f, 244.198f, -11.968f)
      curveTo(243.566f, -11.336f, 243.25f, -10.569f, 243.25f, -9.666f)
      curveTo(243.25f, -9.467f, 243.269f, -9.282f, 243.305f, -9.111f)
      curveTo(243.341f, -8.939f, 243.395f, -8.763f, 243.467f, -8.583f)
      horizontalLineTo(237.834f)
      verticalLineTo(-2.083f)
      horizontalLineTo(240.0f)
      verticalLineTo(9.834f)
      horizontalLineTo(257.334f)
      close()
      moveTo(246.5f, -10.749f)
      curveTo(246.807f, -10.749f, 247.065f, -10.646f, 247.272f, -10.438f)
      curveTo(247.48f, -10.23f, 247.584f, -9.973f, 247.584f, -9.666f)
      curveTo(247.584f, -9.359f, 247.48f, -9.102f, 247.272f, -8.894f)
      curveTo(247.065f, -8.686f, 246.807f, -8.583f, 246.5f, -8.583f)
      curveTo(246.194f, -8.583f, 245.936f, -8.686f, 245.729f, -8.894f)
      curveTo(245.521f, -9.102f, 245.417f, -9.359f, 245.417f, -9.666f)
      curveTo(245.417f, -9.973f, 245.521f, -10.23f, 245.729f, -10.438f)
      curveTo(245.936f, -10.646f, 246.194f, -10.749f, 246.5f, -10.749f)
      close()
      moveTo(251.917f, -9.666f)
      curveTo(251.917f, -9.359f, 251.813f, -9.102f, 251.606f, -8.894f)
      curveTo(251.398f, -8.686f, 251.141f, -8.583f, 250.834f, -8.583f)
      curveTo(250.527f, -8.583f, 250.27f, -8.686f, 250.062f, -8.894f)
      curveTo(249.854f, -9.102f, 249.75f, -9.359f, 249.75f, -9.666f)
      curveTo(249.75f, -9.973f, 249.854f, -10.23f, 250.062f, -10.438f)
      curveTo(250.27f, -10.646f, 250.527f, -10.749f, 250.834f, -10.749f)
      curveTo(251.141f, -10.749f, 251.398f, -10.646f, 251.606f, -10.438f)
      curveTo(251.813f, -10.23f, 251.917f, -9.973f, 251.917f, -9.666f)
      close()
      moveTo(257.334f, -6.416f)
      verticalLineTo(-4.249f)
      horizontalLineTo(249.75f)
      verticalLineTo(-6.416f)
      horizontalLineTo(257.334f)
      close()
      moveTo(249.75f, 7.667f)
      verticalLineTo(-2.083f)
      horizontalLineTo(255.167f)
      verticalLineTo(7.667f)
      horizontalLineTo(249.75f)
      close()
      moveTo(247.584f, 7.667f)
      horizontalLineTo(242.167f)
      verticalLineTo(-2.083f)
      horizontalLineTo(247.584f)
      verticalLineTo(7.667f)
      close()
      moveTo(240.0f, -4.249f)
      verticalLineTo(-6.416f)
      horizontalLineTo(247.584f)
      verticalLineTo(-4.249f)
      horizontalLineTo(240.0f)
      close()
    }
  }
  group {
    path(
      fill = SolidColor(iconColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(342.334f, 24.917f)
      verticalLineTo(36.834f)
      horizontalLineTo(325.001f)
      verticalLineTo(24.917f)
      horizontalLineTo(322.834f)
      verticalLineTo(18.417f)
      horizontalLineTo(328.468f)
      curveTo(328.395f, 18.237f, 328.341f, 18.061f, 328.305f, 17.889f)
      curveTo(328.269f, 17.718f, 328.251f, 17.533f, 328.251f, 17.334f)
      curveTo(328.251f, 16.431f, 328.567f, 15.664f, 329.199f, 15.032f)
      curveTo(329.831f, 14.4f, 330.598f, 14.084f, 331.501f, 14.084f)
      curveTo(331.916f, 14.084f, 332.304f, 14.156f, 332.665f, 14.301f)
      curveTo(333.027f, 14.445f, 333.361f, 14.662f, 333.668f, 14.951f)
      curveTo(333.974f, 14.68f, 334.308f, 14.468f, 334.67f, 14.314f)
      curveTo(335.031f, 14.161f, 335.419f, 14.084f, 335.834f, 14.084f)
      curveTo(336.737f, 14.084f, 337.504f, 14.4f, 338.136f, 15.032f)
      curveTo(338.768f, 15.664f, 339.084f, 16.431f, 339.084f, 17.334f)
      curveTo(339.084f, 17.533f, 339.071f, 17.722f, 339.044f, 17.903f)
      curveTo(339.016f, 18.083f, 338.958f, 18.255f, 338.868f, 18.417f)
      horizontalLineTo(344.501f)
      verticalLineTo(24.917f)
      horizontalLineTo(342.334f)
      close()
    }
    path(
      fill = SolidColor(outlineColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(342.334f, 36.834f)
      verticalLineTo(24.917f)
      horizontalLineTo(344.501f)
      verticalLineTo(18.417f)
      horizontalLineTo(338.868f)
      curveTo(338.958f, 18.255f, 339.016f, 18.083f, 339.044f, 17.903f)
      curveTo(339.071f, 17.722f, 339.084f, 17.533f, 339.084f, 17.334f)
      curveTo(339.084f, 16.431f, 338.768f, 15.664f, 338.136f, 15.032f)
      curveTo(337.504f, 14.4f, 336.737f, 14.084f, 335.834f, 14.084f)
      curveTo(335.419f, 14.084f, 335.031f, 14.161f, 334.67f, 14.314f)
      curveTo(334.308f, 14.468f, 333.974f, 14.68f, 333.668f, 14.951f)
      curveTo(333.361f, 14.662f, 333.027f, 14.445f, 332.665f, 14.301f)
      curveTo(332.304f, 14.156f, 331.916f, 14.084f, 331.501f, 14.084f)
      curveTo(330.598f, 14.084f, 329.831f, 14.4f, 329.199f, 15.032f)
      curveTo(328.567f, 15.664f, 328.251f, 16.431f, 328.251f, 17.334f)
      curveTo(328.251f, 17.533f, 328.269f, 17.718f, 328.305f, 17.889f)
      curveTo(328.341f, 18.061f, 328.395f, 18.237f, 328.468f, 18.417f)
      horizontalLineTo(322.834f)
      verticalLineTo(24.917f)
      horizontalLineTo(325.001f)
      verticalLineTo(36.834f)
      horizontalLineTo(342.334f)
      close()
      moveTo(331.501f, 16.251f)
      curveTo(331.808f, 16.251f, 332.065f, 16.354f, 332.273f, 16.562f)
      curveTo(332.48f, 16.77f, 332.584f, 17.027f, 332.584f, 17.334f)
      curveTo(332.584f, 17.641f, 332.48f, 17.898f, 332.273f, 18.106f)
      curveTo(332.065f, 18.313f, 331.808f, 18.417f, 331.501f, 18.417f)
      curveTo(331.194f, 18.417f, 330.937f, 18.313f, 330.729f, 18.106f)
      curveTo(330.521f, 17.898f, 330.418f, 17.641f, 330.418f, 17.334f)
      curveTo(330.418f, 17.027f, 330.521f, 16.77f, 330.729f, 16.562f)
      curveTo(330.937f, 16.354f, 331.194f, 16.251f, 331.501f, 16.251f)
      close()
      moveTo(336.918f, 17.334f)
      curveTo(336.918f, 17.641f, 336.814f, 17.898f, 336.606f, 18.106f)
      curveTo(336.398f, 18.313f, 336.141f, 18.417f, 335.834f, 18.417f)
      curveTo(335.527f, 18.417f, 335.27f, 18.313f, 335.062f, 18.106f)
      curveTo(334.855f, 17.898f, 334.751f, 17.641f, 334.751f, 17.334f)
      curveTo(334.751f, 17.027f, 334.855f, 16.77f, 335.062f, 16.562f)
      curveTo(335.27f, 16.354f, 335.527f, 16.251f, 335.834f, 16.251f)
      curveTo(336.141f, 16.251f, 336.398f, 16.354f, 336.606f, 16.562f)
      curveTo(336.814f, 16.77f, 336.918f, 17.027f, 336.918f, 17.334f)
      close()
      moveTo(342.334f, 20.584f)
      verticalLineTo(22.751f)
      horizontalLineTo(334.751f)
      verticalLineTo(20.584f)
      horizontalLineTo(342.334f)
      close()
      moveTo(334.751f, 34.667f)
      verticalLineTo(24.917f)
      horizontalLineTo(340.168f)
      verticalLineTo(34.667f)
      horizontalLineTo(334.751f)
      close()
      moveTo(332.584f, 34.667f)
      horizontalLineTo(327.168f)
      verticalLineTo(24.917f)
      horizontalLineTo(332.584f)
      verticalLineTo(34.667f)
      close()
      moveTo(325.001f, 22.751f)
      verticalLineTo(20.584f)
      horizontalLineTo(332.584f)
      verticalLineTo(22.751f)
      horizontalLineTo(325.001f)
      close()
    }
  }
  group {
    path(
      fill = SolidColor(iconColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(301.334f, 57.917f)
      verticalLineTo(69.834f)
      horizontalLineTo(284.0f)
      verticalLineTo(57.917f)
      horizontalLineTo(281.834f)
      verticalLineTo(51.417f)
      horizontalLineTo(287.467f)
      curveTo(287.395f, 51.237f, 287.341f, 51.061f, 287.305f, 50.889f)
      curveTo(287.269f, 50.718f, 287.25f, 50.533f, 287.25f, 50.334f)
      curveTo(287.25f, 49.431f, 287.566f, 48.664f, 288.198f, 48.032f)
      curveTo(288.83f, 47.4f, 289.598f, 47.084f, 290.5f, 47.084f)
      curveTo(290.916f, 47.084f, 291.304f, 47.156f, 291.665f, 47.301f)
      curveTo(292.026f, 47.445f, 292.36f, 47.662f, 292.667f, 47.951f)
      curveTo(292.974f, 47.68f, 293.308f, 47.468f, 293.669f, 47.314f)
      curveTo(294.03f, 47.161f, 294.419f, 47.084f, 294.834f, 47.084f)
      curveTo(295.737f, 47.084f, 296.504f, 47.4f, 297.136f, 48.032f)
      curveTo(297.768f, 48.664f, 298.084f, 49.431f, 298.084f, 50.334f)
      curveTo(298.084f, 50.533f, 298.07f, 50.722f, 298.043f, 50.903f)
      curveTo(298.016f, 51.083f, 297.957f, 51.255f, 297.867f, 51.417f)
      horizontalLineTo(303.5f)
      verticalLineTo(57.917f)
      horizontalLineTo(301.334f)
      close()
    }
    path(
      fill = SolidColor(outlineColor), stroke = null, strokeLineWidth = 0.0f,
      strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(301.334f, 69.834f)
      verticalLineTo(57.917f)
      horizontalLineTo(303.5f)
      verticalLineTo(51.417f)
      horizontalLineTo(297.867f)
      curveTo(297.957f, 51.255f, 298.016f, 51.083f, 298.043f, 50.903f)
      curveTo(298.07f, 50.722f, 298.084f, 50.533f, 298.084f, 50.334f)
      curveTo(298.084f, 49.431f, 297.768f, 48.664f, 297.136f, 48.032f)
      curveTo(296.504f, 47.4f, 295.737f, 47.084f, 294.834f, 47.084f)
      curveTo(294.419f, 47.084f, 294.03f, 47.161f, 293.669f, 47.314f)
      curveTo(293.308f, 47.468f, 292.974f, 47.68f, 292.667f, 47.951f)
      curveTo(292.36f, 47.662f, 292.026f, 47.445f, 291.665f, 47.301f)
      curveTo(291.304f, 47.156f, 290.916f, 47.084f, 290.5f, 47.084f)
      curveTo(289.598f, 47.084f, 288.83f, 47.4f, 288.198f, 48.032f)
      curveTo(287.566f, 48.664f, 287.25f, 49.431f, 287.25f, 50.334f)
      curveTo(287.25f, 50.533f, 287.269f, 50.718f, 287.305f, 50.889f)
      curveTo(287.341f, 51.061f, 287.395f, 51.237f, 287.467f, 51.417f)
      horizontalLineTo(281.834f)
      verticalLineTo(57.917f)
      horizontalLineTo(284.0f)
      verticalLineTo(69.834f)
      horizontalLineTo(301.334f)
      close()
      moveTo(290.5f, 49.251f)
      curveTo(290.807f, 49.251f, 291.065f, 49.354f, 291.272f, 49.562f)
      curveTo(291.48f, 49.77f, 291.584f, 50.027f, 291.584f, 50.334f)
      curveTo(291.584f, 50.641f, 291.48f, 50.898f, 291.272f, 51.106f)
      curveTo(291.065f, 51.313f, 290.807f, 51.417f, 290.5f, 51.417f)
      curveTo(290.194f, 51.417f, 289.936f, 51.313f, 289.729f, 51.106f)
      curveTo(289.521f, 50.898f, 289.417f, 50.641f, 289.417f, 50.334f)
      curveTo(289.417f, 50.027f, 289.521f, 49.77f, 289.729f, 49.562f)
      curveTo(289.936f, 49.354f, 290.194f, 49.251f, 290.5f, 49.251f)
      close()
      moveTo(295.917f, 50.334f)
      curveTo(295.917f, 50.641f, 295.813f, 50.898f, 295.606f, 51.106f)
      curveTo(295.398f, 51.313f, 295.141f, 51.417f, 294.834f, 51.417f)
      curveTo(294.527f, 51.417f, 294.27f, 51.313f, 294.062f, 51.106f)
      curveTo(293.854f, 50.898f, 293.75f, 50.641f, 293.75f, 50.334f)
      curveTo(293.75f, 50.027f, 293.854f, 49.77f, 294.062f, 49.562f)
      curveTo(294.27f, 49.354f, 294.527f, 49.251f, 294.834f, 49.251f)
      curveTo(295.141f, 49.251f, 295.398f, 49.354f, 295.606f, 49.562f)
      curveTo(295.813f, 49.77f, 295.917f, 50.027f, 295.917f, 50.334f)
      close()
      moveTo(301.334f, 53.584f)
      verticalLineTo(55.751f)
      horizontalLineTo(293.75f)
      verticalLineTo(53.584f)
      horizontalLineTo(301.334f)
      close()
      moveTo(293.75f, 67.667f)
      verticalLineTo(57.917f)
      horizontalLineTo(299.167f)
      verticalLineTo(67.667f)
      horizontalLineTo(293.75f)
      close()
      moveTo(291.584f, 67.667f)
      horizontalLineTo(286.167f)
      verticalLineTo(57.917f)
      horizontalLineTo(291.584f)
      verticalLineTo(67.667f)
      close()
      moveTo(284.0f, 55.751f)
      verticalLineTo(53.584f)
      horizontalLineTo(291.584f)
      verticalLineTo(55.751f)
      horizontalLineTo(284.0f)
      close()
    }
  }
}.build()
