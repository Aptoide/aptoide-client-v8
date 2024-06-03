package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
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

@Preview
@Composable
fun TestWalletInstalled() {
  Image(
    getWalletInstalled(Color.Magenta, Color.White, Color.Green, Color.LightGray),
    null,
    modifier = Modifier.size(184.dp)
  )
}

fun getWalletInstalled(
  color1: Color,
  color2: Color,
  color3: Color,
  color4: Color,
): ImageVector = ImageVector.Builder(
  name = "WalletInstalled",
  defaultWidth = 328.0.dp,
  defaultHeight = 120.0.dp,
  viewportWidth = 328.0f,
  viewportHeight = 120.0f
).apply {
  path(
    fill = SolidColor(color3),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(117.0f, 0.0f)
    horizontalLineToRelative(104.0f)
    verticalLineToRelative(24.0f)
    horizontalLineToRelative(-104.0f)
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
    moveTo(117.0f, 48.0f)
    horizontalLineToRelative(104.0f)
    verticalLineToRelative(24.0f)
    horizontalLineToRelative(-104.0f)
    close()
  }
  path(
    fill = SolidColor(color4),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(101.0f, 96.0f)
    horizontalLineToRelative(32.0f)
    verticalLineToRelative(24.0f)
    horizontalLineToRelative(-32.0f)
    close()
  }
  path(
    fill = SolidColor(color4),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(22.0f, 24.0f)
    horizontalLineToRelative(16.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-16.0f)
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
    moveTo(269.0f, 80.0f)
    horizontalLineToRelative(40.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-40.0f)
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
    moveTo(237.0f, 40.0f)
    horizontalLineToRelative(24.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-24.0f)
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
    moveTo(77.0f, 64.0f)
    horizontalLineToRelative(16.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-16.0f)
    close()
  }
  path(
    fill = SolidColor(color4),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(54.0f, 8.0f)
    horizontalLineToRelative(23.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-23.0f)
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
    moveTo(112.0f, 8.0f)
    horizontalLineToRelative(104.0f)
    verticalLineToRelative(104.0f)
    horizontalLineToRelative(-104.0f)
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
    moveTo(158.891f, 86.574f)
    curveTo(159.293f, 86.574f, 159.688f, 86.614f, 160.077f, 86.714f)
    curveTo(160.405f, 86.794f, 160.706f, 86.94f, 160.967f, 87.154f)
    curveTo(161.215f, 87.36f, 161.409f, 87.62f, 161.53f, 87.92f)
    curveTo(161.671f, 88.279f, 161.744f, 88.665f, 161.731f, 89.052f)
    verticalLineTo(93.381f)
    curveTo(161.731f, 93.448f, 161.704f, 93.507f, 161.657f, 93.547f)
    curveTo(161.61f, 93.594f, 161.55f, 93.621f, 161.49f, 93.621f)
    horizontalLineTo(160.13f)
    curveTo(160.063f, 93.621f, 160.003f, 93.594f, 159.963f, 93.547f)
    curveTo(159.916f, 93.501f, 159.889f, 93.441f, 159.889f, 93.381f)
    verticalLineTo(93.188f)
    curveTo(159.662f, 93.354f, 159.407f, 93.488f, 159.146f, 93.587f)
    curveTo(158.845f, 93.694f, 158.53f, 93.741f, 158.208f, 93.734f)
    curveTo(157.947f, 93.734f, 157.686f, 93.701f, 157.438f, 93.634f)
    curveTo(157.197f, 93.567f, 156.969f, 93.448f, 156.775f, 93.288f)
    curveTo(156.568f, 93.115f, 156.407f, 92.895f, 156.306f, 92.642f)
    curveTo(156.179f, 92.329f, 156.119f, 91.989f, 156.132f, 91.649f)
    verticalLineTo(91.536f)
    curveTo(156.085f, 90.93f, 156.353f, 90.344f, 156.842f, 89.978f)
    curveTo(157.539f, 89.598f, 158.329f, 89.425f, 159.119f, 89.485f)
    horizontalLineTo(159.775f)
    verticalLineTo(89.045f)
    curveTo(159.775f, 88.699f, 159.695f, 88.472f, 159.534f, 88.366f)
    curveTo(159.333f, 88.253f, 159.099f, 88.193f, 158.865f, 88.206f)
    curveTo(158.476f, 88.206f, 158.094f, 88.213f, 157.706f, 88.232f)
    curveTo(157.525f, 88.239f, 157.358f, 88.253f, 157.217f, 88.259f)
    lineTo(156.856f, 88.292f)
    horizontalLineTo(156.795f)
    curveTo(156.742f, 88.292f, 156.681f, 88.279f, 156.635f, 88.246f)
    curveTo(156.588f, 88.199f, 156.568f, 88.139f, 156.574f, 88.079f)
    verticalLineTo(87.1f)
    curveTo(156.574f, 86.967f, 156.675f, 86.854f, 156.802f, 86.834f)
    curveTo(157.03f, 86.774f, 157.331f, 86.721f, 157.719f, 86.661f)
    curveTo(158.115f, 86.607f, 158.51f, 86.574f, 158.905f, 86.581f)
    lineTo(158.891f, 86.574f)
    close()
    moveTo(179.726f, 85.182f)
    curveTo(179.786f, 85.182f, 179.84f, 85.195f, 179.886f, 85.235f)
    curveTo(179.927f, 85.275f, 179.953f, 85.329f, 179.947f, 85.389f)
    verticalLineTo(86.674f)
    horizontalLineTo(180.945f)
    curveTo(181.011f, 86.674f, 181.072f, 86.701f, 181.112f, 86.747f)
    curveTo(181.159f, 86.794f, 181.186f, 86.854f, 181.186f, 86.92f)
    verticalLineTo(87.899f)
    curveTo(181.186f, 87.966f, 181.159f, 88.026f, 181.112f, 88.066f)
    curveTo(181.065f, 88.113f, 181.005f, 88.139f, 180.945f, 88.139f)
    horizontalLineTo(179.947f)
    verticalLineTo(91.669f)
    curveTo(179.933f, 91.829f, 179.953f, 91.989f, 180.007f, 92.135f)
    curveTo(180.047f, 92.202f, 180.161f, 92.242f, 180.348f, 92.242f)
    horizontalLineTo(180.945f)
    curveTo(181.112f, 92.242f, 181.199f, 92.309f, 181.199f, 92.449f)
    verticalLineTo(93.394f)
    curveTo(181.199f, 93.534f, 181.119f, 93.614f, 180.971f, 93.634f)
    curveTo(180.824f, 93.654f, 180.65f, 93.687f, 180.482f, 93.707f)
    curveTo(180.328f, 93.727f, 180.168f, 93.734f, 180.007f, 93.734f)
    curveTo(179.712f, 93.734f, 179.418f, 93.714f, 179.13f, 93.654f)
    curveTo(178.895f, 93.607f, 178.674f, 93.501f, 178.493f, 93.348f)
    curveTo(178.313f, 93.174f, 178.179f, 92.955f, 178.105f, 92.708f)
    curveTo(178.005f, 92.369f, 177.964f, 92.016f, 177.971f, 91.656f)
    verticalLineTo(88.139f)
    lineTo(177.0f, 87.979f)
    curveTo(176.933f, 87.966f, 176.873f, 87.933f, 176.826f, 87.886f)
    curveTo(176.779f, 87.846f, 176.746f, 87.786f, 176.746f, 87.726f)
    verticalLineTo(86.927f)
    curveTo(176.746f, 86.86f, 176.772f, 86.794f, 176.826f, 86.747f)
    curveTo(176.873f, 86.701f, 176.933f, 86.667f, 177.0f, 86.667f)
    horizontalLineTo(177.971f)
    verticalLineTo(85.675f)
    curveTo(177.971f, 85.615f, 177.998f, 85.555f, 178.045f, 85.515f)
    curveTo(178.092f, 85.475f, 178.152f, 85.449f, 178.212f, 85.428f)
    lineTo(179.692f, 85.175f)
    horizontalLineTo(179.732f)
    lineTo(179.726f, 85.182f)
    close()
    moveTo(172.915f, 86.574f)
    curveTo(173.364f, 86.567f, 173.806f, 86.647f, 174.221f, 86.814f)
    curveTo(174.582f, 86.967f, 174.897f, 87.193f, 175.158f, 87.48f)
    curveTo(175.413f, 87.773f, 175.607f, 88.119f, 175.721f, 88.486f)
    curveTo(175.848f, 88.898f, 175.915f, 89.325f, 175.908f, 89.758f)
    verticalLineTo(90.464f)
    curveTo(175.908f, 90.657f, 175.821f, 90.757f, 175.654f, 90.757f)
    horizontalLineTo(171.783f)
    verticalLineTo(90.783f)
    curveTo(171.783f, 90.95f, 171.803f, 91.116f, 171.843f, 91.283f)
    curveTo(171.877f, 91.436f, 171.95f, 91.583f, 172.051f, 91.709f)
    curveTo(172.151f, 91.836f, 172.285f, 91.936f, 172.433f, 92.002f)
    curveTo(172.62f, 92.082f, 172.828f, 92.116f, 173.035f, 92.109f)
    curveTo(173.23f, 92.109f, 173.437f, 92.109f, 173.652f, 92.095f)
    lineTo(174.301f, 92.069f)
    curveTo(174.515f, 92.062f, 174.716f, 92.049f, 174.904f, 92.042f)
    curveTo(175.091f, 92.035f, 175.245f, 92.016f, 175.366f, 92.002f)
    horizontalLineTo(175.399f)
    curveTo(175.553f, 92.002f, 175.634f, 92.069f, 175.634f, 92.195f)
    verticalLineTo(93.115f)
    curveTo(175.634f, 93.188f, 175.62f, 93.268f, 175.587f, 93.334f)
    curveTo(175.533f, 93.394f, 175.46f, 93.434f, 175.379f, 93.441f)
    curveTo(175.004f, 93.534f, 174.623f, 93.607f, 174.241f, 93.661f)
    curveTo(173.792f, 93.714f, 173.343f, 93.741f, 172.895f, 93.734f)
    curveTo(172.526f, 93.734f, 172.158f, 93.681f, 171.803f, 93.574f)
    curveTo(171.442f, 93.467f, 171.107f, 93.294f, 170.819f, 93.055f)
    curveTo(170.511f, 92.788f, 170.27f, 92.462f, 170.116f, 92.089f)
    curveTo(169.921f, 91.623f, 169.834f, 91.116f, 169.848f, 90.617f)
    verticalLineTo(89.771f)
    curveTo(169.848f, 89.325f, 169.915f, 88.879f, 170.055f, 88.452f)
    curveTo(170.182f, 88.079f, 170.383f, 87.733f, 170.658f, 87.447f)
    curveTo(170.926f, 87.167f, 171.261f, 86.947f, 171.622f, 86.807f)
    curveTo(172.038f, 86.654f, 172.473f, 86.574f, 172.915f, 86.581f)
    verticalLineTo(86.574f)
    close()
    moveTo(147.044f, 84.609f)
    curveTo(147.158f, 84.609f, 147.225f, 84.683f, 147.239f, 84.836f)
    lineTo(147.928f, 91.336f)
    curveTo(147.935f, 91.443f, 147.962f, 91.496f, 148.002f, 91.496f)
    curveTo(148.042f, 91.496f, 148.062f, 91.443f, 148.089f, 91.336f)
    lineTo(148.96f, 87.999f)
    curveTo(148.993f, 87.813f, 149.1f, 87.64f, 149.261f, 87.533f)
    curveTo(149.395f, 87.46f, 149.556f, 87.42f, 149.71f, 87.42f)
    horizontalLineTo(150.909f)
    curveTo(151.063f, 87.42f, 151.223f, 87.46f, 151.357f, 87.533f)
    curveTo(151.518f, 87.64f, 151.625f, 87.813f, 151.659f, 87.999f)
    lineTo(152.529f, 91.336f)
    curveTo(152.556f, 91.443f, 152.583f, 91.496f, 152.616f, 91.496f)
    curveTo(152.65f, 91.496f, 152.683f, 91.443f, 152.69f, 91.336f)
    lineTo(153.38f, 84.836f)
    curveTo(153.393f, 84.683f, 153.46f, 84.609f, 153.574f, 84.609f)
    horizontalLineTo(155.221f)
    curveTo(155.268f, 84.609f, 155.315f, 84.616f, 155.355f, 84.636f)
    curveTo(155.396f, 84.663f, 155.422f, 84.716f, 155.416f, 84.762f)
    verticalLineTo(84.789f)
    lineTo(154.291f, 92.735f)
    curveTo(154.277f, 93.015f, 154.15f, 93.281f, 153.942f, 93.461f)
    curveTo(153.715f, 93.601f, 153.447f, 93.667f, 153.179f, 93.654f)
    horizontalLineTo(152.489f)
    curveTo(152.241f, 93.654f, 152.0f, 93.594f, 151.793f, 93.474f)
    curveTo(151.572f, 93.321f, 151.424f, 93.094f, 151.377f, 92.835f)
    lineTo(150.42f, 89.205f)
    curveTo(150.42f, 89.205f, 150.4f, 89.145f, 150.38f, 89.118f)
    curveTo(150.359f, 89.105f, 150.333f, 89.105f, 150.313f, 89.105f)
    curveTo(150.293f, 89.105f, 150.272f, 89.105f, 150.252f, 89.118f)
    curveTo(150.239f, 89.145f, 150.226f, 89.172f, 150.219f, 89.205f)
    lineTo(149.261f, 92.835f)
    curveTo(149.214f, 93.094f, 149.06f, 93.328f, 148.846f, 93.474f)
    curveTo(148.638f, 93.601f, 148.391f, 93.661f, 148.149f, 93.654f)
    horizontalLineTo(147.46f)
    curveTo(147.192f, 93.667f, 146.924f, 93.601f, 146.696f, 93.461f)
    curveTo(146.482f, 93.274f, 146.355f, 93.015f, 146.341f, 92.735f)
    lineTo(145.216f, 84.789f)
    verticalLineTo(84.762f)
    curveTo(145.21f, 84.709f, 145.23f, 84.663f, 145.276f, 84.636f)
    curveTo(145.317f, 84.616f, 145.364f, 84.603f, 145.41f, 84.603f)
    horizontalLineTo(147.058f)
    lineTo(147.044f, 84.609f)
    close()
    moveTo(164.919f, 84.316f)
    curveTo(164.979f, 84.316f, 165.039f, 84.343f, 165.086f, 84.39f)
    curveTo(165.133f, 84.436f, 165.153f, 84.496f, 165.153f, 84.563f)
    verticalLineTo(93.368f)
    curveTo(165.153f, 93.434f, 165.126f, 93.494f, 165.086f, 93.547f)
    curveTo(165.046f, 93.594f, 164.986f, 93.627f, 164.925f, 93.627f)
    horizontalLineTo(163.445f)
    curveTo(163.378f, 93.627f, 163.311f, 93.601f, 163.271f, 93.547f)
    curveTo(163.224f, 93.501f, 163.191f, 93.441f, 163.191f, 93.368f)
    verticalLineTo(84.563f)
    curveTo(163.191f, 84.496f, 163.218f, 84.436f, 163.271f, 84.39f)
    curveTo(163.318f, 84.343f, 163.378f, 84.316f, 163.445f, 84.316f)
    horizontalLineTo(164.925f)
    horizontalLineTo(164.919f)
    close()
    moveTo(168.341f, 84.316f)
    curveTo(168.401f, 84.316f, 168.461f, 84.343f, 168.508f, 84.39f)
    curveTo(168.548f, 84.436f, 168.575f, 84.496f, 168.575f, 84.563f)
    verticalLineTo(93.368f)
    curveTo(168.575f, 93.434f, 168.548f, 93.494f, 168.508f, 93.547f)
    curveTo(168.468f, 93.594f, 168.408f, 93.627f, 168.341f, 93.627f)
    horizontalLineTo(166.861f)
    curveTo(166.794f, 93.627f, 166.727f, 93.601f, 166.687f, 93.547f)
    curveTo(166.64f, 93.501f, 166.606f, 93.441f, 166.606f, 93.368f)
    verticalLineTo(84.563f)
    curveTo(166.606f, 84.496f, 166.633f, 84.436f, 166.687f, 84.39f)
    curveTo(166.734f, 84.343f, 166.794f, 84.316f, 166.861f, 84.316f)
    horizontalLineTo(168.341f)
    close()
    moveTo(159.802f, 90.644f)
    horizontalLineTo(159.112f)
    curveTo(158.938f, 90.644f, 158.764f, 90.657f, 158.59f, 90.697f)
    curveTo(158.47f, 90.724f, 158.356f, 90.783f, 158.262f, 90.857f)
    curveTo(158.182f, 90.93f, 158.121f, 91.03f, 158.088f, 91.136f)
    curveTo(158.054f, 91.263f, 158.034f, 91.396f, 158.041f, 91.523f)
    verticalLineTo(91.636f)
    curveTo(158.041f, 91.896f, 158.108f, 92.062f, 158.228f, 92.135f)
    curveTo(158.409f, 92.222f, 158.603f, 92.255f, 158.804f, 92.242f)
    curveTo(158.985f, 92.242f, 159.159f, 92.215f, 159.333f, 92.162f)
    curveTo(159.494f, 92.109f, 159.648f, 92.049f, 159.802f, 91.976f)
    verticalLineTo(90.644f)
    close()
    moveTo(172.908f, 88.146f)
    curveTo(172.6f, 88.133f, 172.299f, 88.259f, 172.098f, 88.486f)
    curveTo(171.877f, 88.772f, 171.77f, 89.132f, 171.796f, 89.491f)
    verticalLineTo(89.538f)
    horizontalLineTo(174.006f)
    verticalLineTo(89.491f)
    curveTo(174.033f, 89.132f, 173.933f, 88.772f, 173.712f, 88.479f)
    curveTo(173.511f, 88.253f, 173.216f, 88.133f, 172.915f, 88.146f)
    horizontalLineTo(172.908f)
    close()
    moveTo(187.749f, 37.161f)
    curveTo(201.417f, 50.848f, 201.417f, 72.94f, 187.749f, 86.634f)
    curveTo(187.4f, 87.007f, 186.878f, 87.154f, 186.382f, 87.027f)
    curveTo(185.887f, 86.9f, 185.505f, 86.514f, 185.378f, 86.021f)
    curveTo(185.251f, 85.528f, 185.405f, 85.002f, 185.773f, 84.656f)
    curveTo(198.35f, 72.061f, 198.35f, 51.734f, 185.773f, 39.146f)
    curveTo(173.283f, 26.638f, 152.965f, 26.572f, 140.388f, 38.993f)
    curveTo(140.334f, 39.046f, 140.287f, 39.093f, 140.234f, 39.146f)
    curveTo(127.657f, 51.741f, 127.657f, 72.068f, 140.234f, 84.656f)
    curveTo(140.602f, 85.009f, 140.749f, 85.528f, 140.622f, 86.021f)
    curveTo(140.495f, 86.507f, 140.106f, 86.894f, 139.618f, 87.02f)
    curveTo(139.129f, 87.147f, 138.606f, 87.0f, 138.251f, 86.634f)
    curveTo(124.583f, 72.947f, 124.583f, 50.855f, 138.251f, 37.161f)
    curveTo(151.826f, 23.568f, 173.906f, 23.494f, 187.575f, 36.988f)
    curveTo(187.635f, 37.048f, 187.695f, 37.101f, 187.749f, 37.161f)
    close()
    moveTo(163.285f, 40.445f)
    curveTo(164.564f, 40.392f, 165.816f, 40.805f, 166.807f, 41.604f)
    curveTo(167.812f, 42.43f, 168.582f, 43.495f, 169.051f, 44.701f)
    lineTo(172.205f, 53.126f)
    horizontalLineTo(178.005f)
    curveTo(178.942f, 53.153f, 179.692f, 53.919f, 179.692f, 54.851f)
    curveTo(179.692f, 55.784f, 178.949f, 56.556f, 178.005f, 56.576f)
    horizontalLineTo(173.497f)
    lineTo(174.187f, 58.328f)
    horizontalLineTo(178.058f)
    curveTo(179.022f, 58.328f, 179.799f, 59.1f, 179.819f, 60.053f)
    curveTo(179.826f, 60.506f, 179.652f, 60.945f, 179.331f, 61.272f)
    curveTo(179.009f, 61.598f, 178.574f, 61.778f, 178.118f, 61.778f)
    horizontalLineTo(175.48f)
    lineTo(179.665f, 73.047f)
    curveTo(179.88f, 73.686f, 180.04f, 74.339f, 180.148f, 75.005f)
    curveTo(180.168f, 75.645f, 180.0f, 76.284f, 179.665f, 76.837f)
    curveTo(179.297f, 77.316f, 178.855f, 77.743f, 178.359f, 78.102f)
    curveTo(177.79f, 78.422f, 177.141f, 78.582f, 176.484f, 78.568f)
    curveTo(174.736f, 78.682f, 173.136f, 77.596f, 172.593f, 75.938f)
    lineTo(170.946f, 71.016f)
    horizontalLineTo(155.757f)
    lineTo(154.11f, 75.831f)
    curveTo(153.628f, 77.543f, 152.007f, 78.695f, 150.219f, 78.595f)
    curveTo(149.181f, 78.608f, 148.17f, 78.242f, 147.386f, 77.569f)
    curveTo(146.669f, 76.917f, 146.248f, 76.011f, 146.201f, 75.052f)
    curveTo(146.234f, 74.359f, 146.388f, 73.68f, 146.656f, 73.047f)
    lineTo(150.942f, 61.785f)
    lineTo(148.297f, 61.731f)
    curveTo(147.359f, 61.711f, 146.609f, 60.945f, 146.609f, 60.013f)
    curveTo(146.609f, 59.087f, 147.359f, 58.328f, 148.297f, 58.328f)
    horizontalLineTo(152.268f)
    lineTo(152.938f, 56.623f)
    lineTo(148.498f, 56.589f)
    curveTo(147.547f, 56.583f, 146.783f, 55.817f, 146.777f, 54.871f)
    curveTo(146.777f, 54.418f, 146.951f, 53.985f, 147.265f, 53.666f)
    curveTo(147.58f, 53.346f, 148.016f, 53.166f, 148.464f, 53.166f)
    horizontalLineTo(154.257f)
    lineTo(157.498f, 44.701f)
    curveTo(157.974f, 43.495f, 158.744f, 42.43f, 159.742f, 41.604f)
    curveTo(160.753f, 40.811f, 162.006f, 40.405f, 163.291f, 40.445f)
    horizontalLineTo(163.285f)
    close()
    moveTo(163.285f, 48.424f)
    lineTo(158.228f, 63.509f)
    horizontalLineTo(168.428f)
    lineTo(163.285f, 48.424f)
    close()
  }
}.build()
