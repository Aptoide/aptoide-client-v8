package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestBonusTextIcon() {
  Image(
    imageVector = getBonusTextIcon(Color.Magenta, Color.Green, Color.Black),
    contentDescription = null,
    modifier = Modifier
      .size(240.dp)
      .background(Color.White)
  )
}

fun getBonusTextIcon(color1: Color, color2: Color, color3: Color): ImageVector =
  Builder(
    name = "Bonustexticon",
    defaultWidth = 328.0.dp,
    defaultHeight = 88.0.dp,
    viewportWidth = 328.0f,
    viewportHeight = 88.0f
  ).apply {
    path(
      fill = SolidColor(color1),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(216.0f, 56.0f)
      horizontalLineToRelative(32.0f)
      verticalLineToRelative(7.0f)
      horizontalLineToRelative(-32.0f)
      close()
    }
    path(
      fill = SolidColor(color1),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(104.0f, 41.0f)
      lineToRelative(0.0f, -15.0f)
      lineToRelative(8.0f, -0.0f)
      lineToRelative(0.0f, 15.0f)
      close()
    }
    path(
      fill = SolidColor(color2),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(88.0f, 64.0f)
      horizontalLineToRelative(24.0f)
      verticalLineToRelative(8.0f)
      horizontalLineToRelative(-24.0f)
      close()
    }
    path(
      fill = SolidColor(color3),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(84.0f, 8.0f)
      horizontalLineToRelative(8.0f)
      verticalLineToRelative(8.0f)
      horizontalLineToRelative(-8.0f)
      close()
    }
    path(
      fill = SolidColor(color2),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = EvenOdd
    ) {
      moveTo(126.0f, 73.0f)
      verticalLineTo(34.0f)
      horizontalLineTo(135.75f)
      verticalLineTo(8.0f)
      horizontalLineTo(204.0f)
      verticalLineTo(73.0f)
      horizontalLineTo(191.0f)
      verticalLineTo(86.0f)
      horizontalLineTo(152.0f)
      verticalLineTo(73.0f)
      horizontalLineTo(126.0f)
      close()
    }
    path(
      fill = SolidColor(color3),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(149.819f, 30.56f)
      verticalLineTo(23.6f)
      horizontalLineTo(151.451f)
      verticalLineTo(29.996f)
      lineTo(152.063f, 30.608f)
      horizontalLineTo(154.415f)
      lineTo(155.027f, 29.996f)
      verticalLineTo(23.6f)
      horizontalLineTo(156.659f)
      verticalLineTo(30.56f)
      lineTo(155.219f, 32.0f)
      horizontalLineTo(151.259f)
      lineTo(149.819f, 30.56f)
      close()
      moveTo(158.161f, 26.024f)
      horizontalLineTo(159.625f)
      verticalLineTo(26.9f)
      lineTo(160.501f, 26.024f)
      horizontalLineTo(162.829f)
      lineTo(164.065f, 27.272f)
      verticalLineTo(30.752f)
      lineTo(162.829f, 32.0f)
      horizontalLineTo(160.513f)
      lineTo(159.745f, 31.364f)
      verticalLineTo(34.544f)
      horizontalLineTo(158.161f)
      verticalLineTo(26.024f)
      close()
      moveTo(162.061f, 30.656f)
      lineTo(162.481f, 30.236f)
      verticalLineTo(27.788f)
      lineTo(162.061f, 27.368f)
      horizontalLineTo(160.705f)
      lineTo(159.745f, 28.34f)
      verticalLineTo(29.804f)
      lineTo(160.693f, 30.656f)
      horizontalLineTo(162.061f)
      close()
      moveTo(170.618f, 24.98f)
      horizontalLineTo(168.11f)
      verticalLineTo(23.6f)
      horizontalLineTo(174.758f)
      verticalLineTo(24.98f)
      horizontalLineTo(172.25f)
      verticalLineTo(32.0f)
      horizontalLineTo(170.618f)
      verticalLineTo(24.98f)
      close()
      moveTo(174.459f, 30.752f)
      verticalLineTo(27.272f)
      lineTo(175.695f, 26.024f)
      horizontalLineTo(179.127f)
      lineTo(180.363f, 27.272f)
      verticalLineTo(30.752f)
      lineTo(179.127f, 32.0f)
      horizontalLineTo(175.695f)
      lineTo(174.459f, 30.752f)
      close()
      moveTo(178.359f, 30.68f)
      lineTo(178.779f, 30.26f)
      verticalLineTo(27.764f)
      lineTo(178.359f, 27.344f)
      horizontalLineTo(176.463f)
      lineTo(176.043f, 27.764f)
      verticalLineTo(30.26f)
      lineTo(176.463f, 30.68f)
      horizontalLineTo(178.359f)
      close()
    }
    path(
      fill = SolidColor(color3),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(146.991f, 54.3f)
      horizontalLineTo(151.82f)
      lineTo(152.92f, 55.4f)
      verticalLineTo(57.556f)
      lineTo(152.48f, 58.007f)
      lineTo(153.261f, 58.799f)
      verticalLineTo(60.812f)
      lineTo(152.073f, 62.0f)
      horizontalLineTo(146.991f)
      verticalLineTo(54.3f)
      close()
      moveTo(151.05f, 57.523f)
      lineTo(151.446f, 57.127f)
      verticalLineTo(55.939f)
      lineTo(151.061f, 55.554f)
      horizontalLineTo(148.454f)
      verticalLineTo(57.523f)
      horizontalLineTo(151.05f)
      close()
      moveTo(151.314f, 60.746f)
      lineTo(151.787f, 60.273f)
      verticalLineTo(59.195f)
      lineTo(151.314f, 58.722f)
      horizontalLineTo(148.454f)
      verticalLineTo(60.746f)
      horizontalLineTo(151.314f)
      close()
      moveTo(154.251f, 60.735f)
      verticalLineTo(55.565f)
      lineTo(155.516f, 54.3f)
      horizontalLineTo(159.476f)
      lineTo(160.741f, 55.565f)
      verticalLineTo(60.735f)
      lineTo(159.476f, 62.0f)
      horizontalLineTo(155.516f)
      lineTo(154.251f, 60.735f)
      close()
      moveTo(158.684f, 60.724f)
      lineTo(159.245f, 60.163f)
      verticalLineTo(56.137f)
      lineTo(158.684f, 55.576f)
      horizontalLineTo(156.308f)
      lineTo(155.747f, 56.137f)
      verticalLineTo(60.163f)
      lineTo(156.308f, 60.724f)
      horizontalLineTo(158.684f)
      close()
      moveTo(162.063f, 54.3f)
      horizontalLineTo(163.405f)
      lineTo(166.837f, 59.525f)
      horizontalLineTo(166.859f)
      verticalLineTo(54.3f)
      horizontalLineTo(168.3f)
      verticalLineTo(62.0f)
      horizontalLineTo(166.958f)
      lineTo(163.526f, 56.786f)
      horizontalLineTo(163.504f)
      verticalLineTo(62.0f)
      horizontalLineTo(162.063f)
      verticalLineTo(54.3f)
      close()
      moveTo(169.733f, 60.68f)
      verticalLineTo(54.3f)
      horizontalLineTo(171.229f)
      verticalLineTo(60.163f)
      lineTo(171.79f, 60.724f)
      horizontalLineTo(173.946f)
      lineTo(174.507f, 60.163f)
      verticalLineTo(54.3f)
      horizontalLineTo(176.003f)
      verticalLineTo(60.68f)
      lineTo(174.683f, 62.0f)
      horizontalLineTo(171.053f)
      lineTo(169.733f, 60.68f)
      close()
      moveTo(177.27f, 60.812f)
      verticalLineTo(59.712f)
      horizontalLineTo(178.744f)
      verticalLineTo(60.361f)
      lineTo(179.107f, 60.724f)
      horizontalLineTo(181.384f)
      lineTo(181.758f, 60.35f)
      verticalLineTo(59.074f)
      lineTo(181.395f, 58.711f)
      horizontalLineTo(178.48f)
      lineTo(177.292f, 57.523f)
      verticalLineTo(55.488f)
      lineTo(178.48f, 54.3f)
      horizontalLineTo(181.956f)
      lineTo(183.144f, 55.488f)
      verticalLineTo(56.599f)
      horizontalLineTo(181.67f)
      verticalLineTo(55.939f)
      lineTo(181.307f, 55.576f)
      horizontalLineTo(179.129f)
      lineTo(178.766f, 55.939f)
      verticalLineTo(57.072f)
      lineTo(179.129f, 57.435f)
      horizontalLineTo(182.044f)
      lineTo(183.232f, 58.623f)
      verticalLineTo(60.79f)
      lineTo(182.022f, 62.0f)
      horizontalLineTo(178.458f)
      lineTo(177.27f, 60.812f)
      close()
    }
    path(
      fill = SolidColor(color3),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(176.923f, 48.292f)
      verticalLineTo(46.42f)
      lineTo(177.643f, 45.7f)
      horizontalLineTo(179.208f)
      lineTo(179.929f, 46.42f)
      verticalLineTo(48.292f)
      lineTo(179.208f, 49.012f)
      horizontalLineTo(177.643f)
      lineTo(176.923f, 48.292f)
      close()
      moveTo(181.612f, 45.7f)
      horizontalLineTo(182.836f)
      lineTo(179.389f, 52.0f)
      horizontalLineTo(178.165f)
      lineTo(181.612f, 45.7f)
      close()
      moveTo(178.687f, 48.112f)
      lineTo(178.885f, 47.914f)
      verticalLineTo(46.798f)
      lineTo(178.687f, 46.6f)
      horizontalLineTo(178.165f)
      lineTo(177.967f, 46.798f)
      verticalLineTo(47.914f)
      lineTo(178.165f, 48.112f)
      horizontalLineTo(178.687f)
      close()
      moveTo(181.072f, 51.28f)
      verticalLineTo(49.408f)
      lineTo(181.792f, 48.688f)
      horizontalLineTo(183.358f)
      lineTo(184.078f, 49.408f)
      verticalLineTo(51.28f)
      lineTo(183.358f, 52.0f)
      horizontalLineTo(181.792f)
      lineTo(181.072f, 51.28f)
      close()
      moveTo(182.836f, 51.1f)
      lineTo(183.034f, 50.902f)
      verticalLineTo(49.786f)
      lineTo(182.836f, 49.588f)
      horizontalLineTo(182.314f)
      lineTo(182.116f, 49.786f)
      verticalLineTo(50.902f)
      lineTo(182.314f, 51.1f)
      horizontalLineTo(182.836f)
      close()
    }
    path(
      fill = SolidColor(color3),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(147.936f, 48.328f)
      lineTo(156.576f, 40.48f)
      verticalLineTo(38.776f)
      lineTo(155.784f, 37.984f)
      horizontalLineTo(151.896f)
      lineTo(151.104f, 38.776f)
      verticalLineTo(40.48f)
      horizontalLineTo(147.84f)
      verticalLineTo(37.792f)
      lineTo(150.432f, 35.2f)
      horizontalLineTo(157.248f)
      lineTo(159.84f, 37.792f)
      verticalLineTo(41.44f)
      lineTo(151.32f, 49.024f)
      verticalLineTo(49.264f)
      horizontalLineTo(159.936f)
      verticalLineTo(52.0f)
      horizontalLineTo(147.936f)
      verticalLineTo(48.328f)
      close()
      moveTo(162.101f, 49.216f)
      verticalLineTo(37.984f)
      lineTo(164.885f, 35.2f)
      horizontalLineTo(172.325f)
      lineTo(175.109f, 37.984f)
      verticalLineTo(49.216f)
      lineTo(172.325f, 52.0f)
      horizontalLineTo(164.885f)
      lineTo(162.101f, 49.216f)
      close()
      moveTo(170.789f, 49.216f)
      lineTo(171.845f, 48.16f)
      verticalLineTo(39.04f)
      lineTo(170.789f, 37.984f)
      horizontalLineTo(166.421f)
      lineTo(165.365f, 39.04f)
      verticalLineTo(48.16f)
      lineTo(166.421f, 49.216f)
      horizontalLineTo(170.789f)
      close()
    }
    path(
      fill = SolidColor(color1),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = EvenOdd
    ) {
      moveTo(193.0f, 34.667f)
      verticalLineTo(18.667f)
      horizontalLineTo(197.0f)
      verticalLineTo(8.0f)
      horizontalLineTo(225.0f)
      verticalLineTo(34.667f)
      horizontalLineTo(219.667f)
      verticalLineTo(40.0f)
      horizontalLineTo(225.0f)
      verticalLineTo(40.0f)
      horizontalLineTo(203.667f)
      verticalLineTo(34.667f)
      horizontalLineTo(193.0f)
      close()
    }
    group {
      path(
        fill = SolidColor(color2),
        stroke = null,
        strokeLineWidth = 0.0f,
        strokeLineCap = Butt,
        strokeLineJoin = Miter,
        strokeLineMiter = 4.0f,
        pathFillType = NonZero
      ) {
        moveTo(201.889f, 23.111f)
        verticalLineTo(32.889f)
        horizontalLineTo(216.111f)
        verticalLineTo(23.111f)
        horizontalLineTo(217.889f)
        verticalLineTo(17.778f)
        horizontalLineTo(213.267f)
        curveTo(213.326f, 17.63f, 213.371f, 17.485f, 213.4f, 17.344f)
        curveTo(213.43f, 17.204f, 213.445f, 17.052f, 213.445f, 16.889f)
        curveTo(213.445f, 16.148f, 213.185f, 15.519f, 212.667f, 15.0f)
        curveTo(212.148f, 14.481f, 211.519f, 14.222f, 210.778f, 14.222f)
        curveTo(210.437f, 14.222f, 210.119f, 14.281f, 209.822f, 14.4f)
        curveTo(209.526f, 14.519f, 209.252f, 14.696f, 209.0f, 14.933f)
        curveTo(208.748f, 14.711f, 208.474f, 14.537f, 208.178f, 14.411f)
        curveTo(207.882f, 14.285f, 207.563f, 14.222f, 207.222f, 14.222f)
        curveTo(206.482f, 14.222f, 205.852f, 14.481f, 205.334f, 15.0f)
        curveTo(204.815f, 15.519f, 204.556f, 16.148f, 204.556f, 16.889f)
        curveTo(204.556f, 17.052f, 204.567f, 17.207f, 204.589f, 17.355f)
        curveTo(204.611f, 17.504f, 204.659f, 17.644f, 204.734f, 17.778f)
        horizontalLineTo(200.111f)
        verticalLineTo(23.111f)
        horizontalLineTo(201.889f)
        close()
      }
      path(
        fill = SolidColor(color3),
        stroke = null,
        strokeLineWidth = 0.0f,
        strokeLineCap = Butt,
        strokeLineJoin = Miter,
        strokeLineMiter = 4.0f,
        pathFillType = NonZero
      ) {
        moveTo(201.889f, 32.889f)
        verticalLineTo(23.111f)
        horizontalLineTo(200.111f)
        verticalLineTo(17.778f)
        horizontalLineTo(204.734f)
        curveTo(204.659f, 17.644f, 204.611f, 17.504f, 204.589f, 17.355f)
        curveTo(204.567f, 17.207f, 204.556f, 17.052f, 204.556f, 16.889f)
        curveTo(204.556f, 16.148f, 204.815f, 15.519f, 205.334f, 15.0f)
        curveTo(205.852f, 14.481f, 206.482f, 14.222f, 207.222f, 14.222f)
        curveTo(207.563f, 14.222f, 207.882f, 14.285f, 208.178f, 14.411f)
        curveTo(208.474f, 14.537f, 208.748f, 14.711f, 209.0f, 14.933f)
        curveTo(209.252f, 14.696f, 209.526f, 14.519f, 209.822f, 14.4f)
        curveTo(210.119f, 14.281f, 210.437f, 14.222f, 210.778f, 14.222f)
        curveTo(211.519f, 14.222f, 212.148f, 14.481f, 212.667f, 15.0f)
        curveTo(213.185f, 15.519f, 213.445f, 16.148f, 213.445f, 16.889f)
        curveTo(213.445f, 17.052f, 213.43f, 17.204f, 213.4f, 17.344f)
        curveTo(213.371f, 17.485f, 213.326f, 17.63f, 213.267f, 17.778f)
        horizontalLineTo(217.889f)
        verticalLineTo(23.111f)
        horizontalLineTo(216.111f)
        verticalLineTo(32.889f)
        horizontalLineTo(201.889f)
        close()
        moveTo(210.778f, 16.0f)
        curveTo(210.526f, 16.0f, 210.315f, 16.085f, 210.145f, 16.256f)
        curveTo(209.974f, 16.426f, 209.889f, 16.637f, 209.889f, 16.889f)
        curveTo(209.889f, 17.141f, 209.974f, 17.352f, 210.145f, 17.522f)
        curveTo(210.315f, 17.692f, 210.526f, 17.778f, 210.778f, 17.778f)
        curveTo(211.03f, 17.778f, 211.241f, 17.692f, 211.411f, 17.522f)
        curveTo(211.582f, 17.352f, 211.667f, 17.141f, 211.667f, 16.889f)
        curveTo(211.667f, 16.637f, 211.582f, 16.426f, 211.411f, 16.256f)
        curveTo(211.241f, 16.085f, 211.03f, 16.0f, 210.778f, 16.0f)
        close()
        moveTo(206.334f, 16.889f)
        curveTo(206.334f, 17.141f, 206.419f, 17.352f, 206.589f, 17.522f)
        curveTo(206.759f, 17.692f, 206.971f, 17.778f, 207.222f, 17.778f)
        curveTo(207.474f, 17.778f, 207.685f, 17.692f, 207.856f, 17.522f)
        curveTo(208.026f, 17.352f, 208.111f, 17.141f, 208.111f, 16.889f)
        curveTo(208.111f, 16.637f, 208.026f, 16.426f, 207.856f, 16.256f)
        curveTo(207.685f, 16.085f, 207.474f, 16.0f, 207.222f, 16.0f)
        curveTo(206.971f, 16.0f, 206.759f, 16.085f, 206.589f, 16.256f)
        curveTo(206.419f, 16.426f, 206.334f, 16.637f, 206.334f, 16.889f)
        close()
        moveTo(201.889f, 19.556f)
        verticalLineTo(21.333f)
        horizontalLineTo(208.111f)
        verticalLineTo(19.556f)
        horizontalLineTo(201.889f)
        close()
        moveTo(208.111f, 31.111f)
        verticalLineTo(23.111f)
        horizontalLineTo(203.667f)
        verticalLineTo(31.111f)
        horizontalLineTo(208.111f)
        close()
        moveTo(209.889f, 31.111f)
        horizontalLineTo(214.334f)
        verticalLineTo(23.111f)
        horizontalLineTo(209.889f)
        verticalLineTo(31.111f)
        close()
        moveTo(216.111f, 21.333f)
        verticalLineTo(19.556f)
        horizontalLineTo(209.889f)
        verticalLineTo(21.333f)
        horizontalLineTo(216.111f)
        close()
      }
    }
  }.build()
