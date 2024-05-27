package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestAppcoinsClearLogo() {
  Image(
    imageVector = getAppcoinsClearLogo(Palette.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp).background(Palette.White)
  )
}

fun getAppcoinsClearLogo(color: Color): ImageVector =
  ImageVector.Builder(
    name = "AppcoinsClearLogo",
    defaultWidth = 22.dp,
    defaultHeight = 22.dp,
    viewportWidth = 22f,
    viewportHeight = 22f,
  ).apply {
    path(fill = SolidColor(color)) {
      moveTo(9.399f, 12.352f)
      lineTo(11.037f, 7.277f)
      lineTo(12.705f, 12.352f)
      horizontalLineTo(9.399f)
      close()
      moveTo(14.989f, 11.769f)
      horizontalLineTo(15.833f)
      curveTo(15.907f, 11.769f, 15.979f, 11.754f, 16.048f, 11.726f)
      curveTo(16.116f, 11.697f, 16.177f, 11.655f, 16.229f, 11.602f)
      curveTo(16.282f, 11.549f, 16.323f, 11.486f, 16.351f, 11.417f)
      curveTo(16.379f, 11.347f, 16.394f, 11.273f, 16.394f, 11.198f)
      verticalLineTo(11.188f)
      curveTo(16.392f, 11.032f, 16.33f, 10.884f, 16.221f, 10.775f)
      curveTo(16.112f, 10.665f, 15.965f, 10.604f, 15.813f, 10.604f)
      horizontalLineTo(14.569f)
      lineTo(14.346f, 10.015f)
      horizontalLineTo(15.796f)
      curveTo(15.948f, 10.015f, 16.093f, 9.954f, 16.201f, 9.844f)
      curveTo(16.308f, 9.735f, 16.369f, 9.587f, 16.369f, 9.432f)
      curveTo(16.369f, 9.277f, 16.308f, 9.129f, 16.201f, 9.019f)
      curveTo(16.093f, 8.91f, 15.948f, 8.849f, 15.796f, 8.849f)
      horizontalLineTo(13.927f)
      lineTo(12.905f, 6.016f)
      curveTo(12.755f, 5.611f, 12.505f, 5.252f, 12.179f, 4.974f)
      curveTo(11.859f, 4.706f, 11.452f, 4.567f, 11.038f, 4.584f)
      curveTo(10.622f, 4.571f, 10.215f, 4.709f, 9.891f, 4.974f)
      curveTo(9.567f, 5.254f, 9.317f, 5.612f, 9.165f, 6.016f)
      lineTo(8.115f, 8.864f)
      horizontalLineTo(6.246f)
      curveTo(6.098f, 8.864f, 5.956f, 8.924f, 5.851f, 9.031f)
      curveTo(5.746f, 9.137f, 5.687f, 9.282f, 5.687f, 9.432f)
      verticalLineTo(9.438f)
      curveTo(5.689f, 9.59f, 5.749f, 9.737f, 5.856f, 9.845f)
      curveTo(5.962f, 9.952f, 6.106f, 10.014f, 6.256f, 10.015f)
      lineTo(7.684f, 10.026f)
      lineTo(7.467f, 10.599f)
      horizontalLineTo(6.19f)
      curveTo(6.042f, 10.6f, 5.901f, 10.659f, 5.796f, 10.766f)
      curveTo(5.692f, 10.872f, 5.633f, 11.016f, 5.633f, 11.167f)
      curveTo(5.633f, 11.318f, 5.691f, 11.463f, 5.796f, 11.571f)
      curveTo(5.9f, 11.679f, 6.041f, 11.742f, 6.19f, 11.745f)
      lineTo(7.038f, 11.762f)
      lineTo(5.648f, 15.55f)
      curveTo(5.56f, 15.764f, 5.51f, 15.992f, 5.5f, 16.224f)
      curveTo(5.515f, 16.547f, 5.654f, 16.851f, 5.886f, 17.071f)
      curveTo(6.14f, 17.295f, 6.466f, 17.418f, 6.803f, 17.415f)
      curveTo(7.086f, 17.43f, 7.365f, 17.346f, 7.595f, 17.177f)
      curveTo(7.824f, 17.007f, 7.99f, 16.764f, 8.064f, 16.485f)
      lineTo(8.597f, 14.867f)
      horizontalLineTo(13.519f)
      lineTo(14.053f, 16.523f)
      curveTo(14.136f, 16.792f, 14.306f, 17.025f, 14.534f, 17.185f)
      curveTo(14.762f, 17.346f, 15.037f, 17.423f, 15.314f, 17.406f)
      curveTo(15.527f, 17.411f, 15.737f, 17.357f, 15.922f, 17.249f)
      curveTo(16.082f, 17.128f, 16.224f, 16.984f, 16.344f, 16.822f)
      curveTo(16.451f, 16.636f, 16.505f, 16.424f, 16.5f, 16.208f)
      curveTo(16.466f, 15.984f, 16.414f, 15.764f, 16.344f, 15.549f)
      lineTo(14.989f, 11.769f)
      close()
    }
    path(fill = SolidColor(color)) {
      moveTo(11.001f, 0f)
      curveTo(8.825f, -0f, 6.698f, 0.645f, 4.889f, 1.853f)
      curveTo(3.08f, 3.062f, 1.67f, 4.78f, 0.838f, 6.79f)
      curveTo(0.005f, 8.8f, -0.213f, 11.012f, 0.211f, 13.146f)
      curveTo(0.636f, 15.279f, 1.683f, 17.24f, 3.222f, 18.778f)
      curveTo(4.76f, 20.316f, 6.72f, 21.364f, 8.854f, 21.789f)
      curveTo(10.988f, 22.213f, 13.199f, 21.995f, 15.209f, 21.163f)
      curveTo(17.219f, 20.33f, 18.937f, 18.92f, 20.146f, 17.111f)
      curveTo(21.355f, 15.302f, 22f, 13.176f, 22f, 11f)
      curveTo(22f, 8.083f, 20.841f, 5.285f, 18.778f, 3.222f)
      curveTo(16.716f, 1.159f, 13.918f, 0f, 11.001f, 0f)
      close()
      moveTo(11.001f, 21.224f)
      curveTo(8.979f, 21.224f, 7.002f, 20.624f, 5.321f, 19.501f)
      curveTo(3.639f, 18.377f, 2.329f, 16.781f, 1.555f, 14.912f)
      curveTo(0.781f, 13.044f, 0.579f, 10.989f, 0.973f, 9.005f)
      curveTo(1.368f, 7.022f, 2.342f, 5.201f, 3.772f, 3.771f)
      curveTo(5.201f, 2.341f, 7.023f, 1.367f, 9.006f, 0.973f)
      curveTo(10.989f, 0.578f, 13.045f, 0.781f, 14.913f, 1.555f)
      curveTo(16.781f, 2.328f, 18.378f, 3.639f, 19.501f, 5.32f)
      curveTo(20.625f, 7.001f, 21.225f, 8.978f, 21.225f, 11f)
      curveTo(21.224f, 13.712f, 20.147f, 16.312f, 18.23f, 18.229f)
      curveTo(16.313f, 20.146f, 13.712f, 21.224f, 11.001f, 21.224f)
      close()
    }
  }.build()
