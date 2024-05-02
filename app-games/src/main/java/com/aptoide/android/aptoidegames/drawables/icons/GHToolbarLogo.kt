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
import com.aptoide.android.aptoidegames.theme.ghLogoGradient

@Preview
@Composable
fun TestGHToolbarLogo() {
  Image(
    imageVector = getGHToolbarLogo(Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getGHToolbarLogo(textColor: Color): ImageVector = ImageVector.Builder(
  name = "Filled.GHToolbarLogo",
  defaultWidth = 163.dp,
  defaultHeight = 23.dp,
  viewportWidth = 163f,
  viewportHeight = 23f
)
  .apply {
    val color = SolidColor(textColor)
    path(fill = color) {
      moveTo(x = 10.1262f, y = 22.9073f)
      curveTo(
        x1 = 8.10917f,
        y1 = 22.9073f,
        x2 = 6.33915f,
        y2 = 22.4443f,
        x3 = 4.81611f,
        y3 = 21.5181f
      )
      curveTo(
        x1 = 3.29307f,
        y1 = 20.5713f,
        x2 = 2.10962f,
        y2 = 19.2644f,
        x3 = 1.26577f,
        y3 = 17.5973f
      )
      curveTo(
        x1 = 0.421924f,
        y1 = 15.9096f,
        x2 = 0f,
        y2 = 13.9646f,
        x3 = 0f,
        y3 = 11.7624f
      )
      curveTo(
        x1 = 0f,
        y1 = 9.56014f,
        x2 = 0.421924f,
        y2 = 7.61517f,
        x3 = 1.26577f,
        y3 = 5.92747f
      )
      curveTo(
        x1 = 2.1302f,
        y1 = 4.23978f,
        x2 = 3.34452f,
        y2 = 2.92255f,
        x3 = 4.90873f,
        y3 = 1.9758f
      )
      curveTo(
        x1 = 6.49351f,
        y1 = 1.02904f,
        x2 = 8.35615f,
        y2 = 0.555664f,
        x3 = 10.4966f,
        y3 = 0.555664f
      )
      curveTo(
        x1 = 12.9253f,
        y1 = 0.555664f,
        x2 = 14.9423f,
        y2 = 1.14224f,
        x3 = 16.5476f,
        y3 = 2.3154f
      )
      curveTo(
        x1 = 18.1736f,
        y1 = 3.48855f,
        x2 = 19.2233f,
        y2 = 5.10421f,
        x3 = 19.6966f,
        y3 = 7.16237f
      )
      horizontalLineTo(x = 16.702f)
      curveTo(
        x1 = 16.4139f,
        y1 = 5.86573f,
        x2 = 15.7244f,
        y2 = 4.83665f,
        x3 = 14.6336f,
        y3 = 4.07513f
      )
      curveTo(
        x1 = 13.5633f,
        y1 = 3.3136f,
        x2 = 12.1843f,
        y2 = 2.93284f,
        x3 = 10.4966f,
        y3 = 2.93284f
      )
      curveTo(
        x1 = 8.91186f,
        y1 = 2.93284f,
        x2 = 7.53289f,
        y2 = 3.29302f,
        x3 = 6.35973f,
        y3 = 4.01338f
      )
      curveTo(
        x1 = 5.18658f,
        y1 = 4.71316f,
        x2 = 4.27069f,
        y2 = 5.72166f,
        x3 = 3.61208f,
        y3 = 7.03888f
      )
      curveTo(
        x1 = 2.97405f,
        y1 = 8.35611f,
        x2 = 2.65503f,
        y2 = 9.93061f,
        x3 = 2.65503f,
        y3 = 11.7624f
      )
      curveTo(
        x1 = 2.65503f,
        y1 = 13.5941f,
        x2 = 2.97405f,
        y2 = 15.1686f,
        x3 = 3.61208f,
        y3 = 16.4859f
      )
      curveTo(
        x1 = 4.25011f,
        y1 = 17.8031f,
        x2 = 5.13512f,
        y2 = 18.8219f,
        x3 = 6.26711f,
        y3 = 19.5422f
      )
      curveTo(
        x1 = 7.39911f,
        y1 = 20.242f,
        x2 = 8.71633f,
        y2 = 20.5919f,
        x3 = 10.2188f,
        y3 = 20.5919f
      )
      curveTo(
        x1 = 12.5239f,
        y1 = 20.5919f,
        x2 = 14.2631f,
        y2 = 19.923f,
        x3 = 15.4362f,
        y3 = 18.5852f
      )
      curveTo(
        x1 = 16.6094f,
        y1 = 17.2268f,
        x2 = 17.2783f,
        y2 = 15.3745f,
        x3 = 17.443f,
        y3 = 13.0281f
      )
      horizontalLineTo(x = 11.2067f)
      verticalLineTo(y = 10.9906f)
      horizontalLineTo(x = 20.098f)
      verticalLineTo(y = 22.5369f)
      horizontalLineTo(x = 17.7208f)
      lineTo(x = 17.5047f, y = 18.9865f)
      curveTo(
        x1 = 16.7226f,
        y1 = 20.242f,
        x2 = 15.7655f,
        y2 = 21.2094f,
        x3 = 14.6336f,
        y3 = 21.8885f
      )
      curveTo(
        x1 = 13.5016f,
        y1 = 22.5677f,
        x2 = 11.9991f,
        y2 = 22.9073f,
        x3 = 10.1262f,
        y3 = 22.9073f
      )
      close()
    }
    path(fill = color) {
      moveTo(x = 29.1386f, y = 22.9073f)
      curveTo(
        x1 = 27.8625f,
        y1 = 22.9073f,
        x2 = 26.8025f,
        y2 = 22.6912f,
        x3 = 25.9587f,
        y3 = 22.259f
      )
      curveTo(
        x1 = 25.1148f,
        y1 = 21.8268f,
        x2 = 24.4871f,
        y2 = 21.2505f,
        x3 = 24.0755f,
        y3 = 20.5302f
      )
      curveTo(
        x1 = 23.6638f,
        y1 = 19.8098f,
        x2 = 23.458f,
        y2 = 19.0277f,
        x3 = 23.458f,
        y3 = 18.1839f
      )
      curveTo(
        x1 = 23.458f,
        y1 = 16.6196f,
        x2 = 24.0549f,
        y2 = 15.4156f,
        x3 = 25.2486f,
        y3 = 14.5718f
      )
      curveTo(
        x1 = 26.4424f,
        y1 = 13.7279f,
        x2 = 28.0683f,
        y2 = 13.306f,
        x3 = 30.1265f,
        y3 = 13.306f
      )
      horizontalLineTo(x = 34.2634f)
      verticalLineTo(y = 13.1208f)
      curveTo(
        x1 = 34.2634f,
        y1 = 11.783f,
        x2 = 33.9135f,
        y2 = 10.7745f,
        x3 = 33.2137f,
        y3 = 10.0953f
      )
      curveTo(
        x1 = 32.5139f,
        y1 = 9.39549f,
        x2 = 31.5775f,
        y2 = 9.0456f,
        x3 = 30.4043f,
        y3 = 9.0456f
      )
      curveTo(
        x1 = 29.3958f,
        y1 = 9.0456f,
        x2 = 28.5211f,
        y2 = 9.30287f,
        x3 = 27.7802f,
        y3 = 9.81741f
      )
      curveTo(
        x1 = 27.0598f,
        y1 = 10.3114f,
        x2 = 26.607f,
        y2 = 11.042f,
        x3 = 26.4218f,
        y3 = 12.0094f
      )
      horizontalLineTo(x = 23.7667f)
      curveTo(
        x1 = 23.8697f,
        y1 = 10.8979f,
        x2 = 24.2401f,
        y2 = 9.96148f,
        x3 = 24.8782f,
        y3 = 9.19996f
      )
      curveTo(
        x1 = 25.5368f,
        y1 = 8.43844f,
        x2 = 26.3497f,
        y2 = 7.86215f,
        x3 = 27.3171f,
        y3 = 7.4711f
      )
      curveTo(
        x1 = 28.2844f,
        y1 = 7.05947f,
        x2 = 29.3135f,
        y2 = 6.85365f,
        x3 = 30.4043f,
        y3 = 6.85365f
      )
      curveTo(
        x1 = 32.5448f,
        y1 = 6.85365f,
        x2 = 34.1502f,
        y2 = 7.42994f,
        x3 = 35.2204f,
        y3 = 8.58251f
      )
      curveTo(
        x1 = 36.3113f,
        y1 = 9.7145f,
        x2 = 36.8567f,
        y2 = 11.2273f,
        x3 = 36.8567f,
        y3 = 13.1208f
      )
      verticalLineTo(y = 22.5369f)
      horizontalLineTo(x = 34.5412f)
      lineTo(x = 34.3869f, y = 19.7892f)
      curveTo(
        x1 = 33.9547f,
        y1 = 20.6536f,
        x2 = 33.3166f,
        y2 = 21.3946f,
        x3 = 32.4728f,
        y3 = 22.012f
      )
      curveTo(
        x1 = 31.6495f,
        y1 = 22.6089f,
        x2 = 30.5381f,
        y2 = 22.9073f,
        x3 = 29.1386f,
        y3 = 22.9073f
      )
      close()
      moveTo(x = 29.5399f, y = 20.7154f)
      curveTo(
        x1 = 30.5278f,
        y1 = 20.7154f,
        x2 = 31.3717f,
        y2 = 20.4581f,
        x3 = 32.0714f,
        y3 = 19.9436f
      )
      curveTo(
        x1 = 32.7918f,
        y1 = 19.429f,
        x2 = 33.3372f,
        y2 = 18.7601f,
        x3 = 33.7077f,
        y3 = 17.9369f
      )
      curveTo(
        x1 = 34.0782f,
        y1 = 17.1136f,
        x2 = 34.2634f,
        y2 = 16.2492f,
        x3 = 34.2634f,
        y3 = 15.3436f
      )
      verticalLineTo(y = 15.3127f)
      horizontalLineTo(x = 30.3426f)
      curveTo(
        x1 = 28.8195f,
        y1 = 15.3127f,
        x2 = 27.739f,
        y2 = 15.5803f,
        x3 = 27.101f,
        y3 = 16.1154f
      )
      curveTo(
        x1 = 26.4835f,
        y1 = 16.6299f,
        x2 = 26.1748f,
        y2 = 17.2783f,
        x3 = 26.1748f,
        y3 = 18.0604f
      )
      curveTo(
        x1 = 26.1748f,
        y1 = 18.863f,
        x2 = 26.4629f,
        y2 = 19.5114f,
        x3 = 27.0392f,
        y3 = 20.0053f
      )
      curveTo(
        x1 = 27.6361f,
        y1 = 20.4787f,
        x2 = 28.4697f,
        y2 = 20.7154f,
        x3 = 29.5399f,
        y3 = 20.7154f
      )
      close()
    }
    path(fill = color) {
      moveTo(x = 40.7471f, y = 22.5369f)
      verticalLineTo(y = 7.22412f)
      horizontalLineTo(x = 43.0934f)
      lineTo(x = 43.2786f, y = 9.44694f)
      curveTo(
        x1 = 43.7726f,
        y1 = 8.62367f,
        x2 = 44.4312f,
        y2 = 7.98564f,
        x3 = 45.2545f,
        y3 = 7.53285f
      )
      curveTo(
        x1 = 46.0777f,
        y1 = 7.08005f,
        x2 = 47.0039f,
        y2 = 6.85365f,
        x3 = 48.033f,
        y3 = 6.85365f
      )
      curveTo(
        x1 = 49.2473f,
        y1 = 6.85365f,
        x2 = 50.2867f,
        y2 = 7.10063f,
        x3 = 51.1511f,
        y3 = 7.59459f
      )
      curveTo(
        x1 = 52.0361f,
        y1 = 8.08855f,
        x2 = 52.7153f,
        y2 = 8.83978f,
        x3 = 53.1887f,
        y3 = 9.84828f
      )
      curveTo(
        x1 = 53.7238f,
        y1 = 8.92211f,
        x2 = 54.4545f,
        y2 = 8.19146f,
        x3 = 55.3807f,
        y3 = 7.65634f
      )
      curveTo(
        x1 = 56.3274f,
        y1 = 7.12121f,
        x2 = 57.3462f,
        y2 = 6.85365f,
        x3 = 58.437f,
        y3 = 6.85365f
      )
      curveTo(
        x1 = 60.2688f,
        y1 = 6.85365f,
        x2 = 61.7301f,
        y2 = 7.40935f,
        x3 = 62.8209f,
        y3 = 8.52076f
      )
      curveTo(
        x1 = 63.9118f,
        y1 = 9.61159f,
        x2 = 64.4572f,
        y2 = 11.2993f,
        x3 = 64.4572f,
        y3 = 13.5839f
      )
      verticalLineTo(y = 22.5369f)
      horizontalLineTo(x = 61.8948f)
      verticalLineTo(y = 13.8617f)
      curveTo(
        x1 = 61.8948f,
        y1 = 12.2769f,
        x2 = 61.5757f,
        y2 = 11.0832f,
        x3 = 60.9377f,
        y3 = 10.2805f
      )
      curveTo(
        x1 = 60.2997f,
        y1 = 9.47781f,
        x2 = 59.3838f,
        y2 = 9.07647f,
        x3 = 58.1901f,
        y3 = 9.07647f
      )
      curveTo(
        x1 = 56.9552f,
        y1 = 9.07647f,
        x2 = 55.9261f,
        y2 = 9.56014f,
        x3 = 55.1028f,
        y3 = 10.5275f
      )
      curveTo(
        x1 = 54.3001f,
        y1 = 11.4742f,
        x2 = 53.8988f,
        y2 = 12.8326f,
        x3 = 53.8988f,
        y3 = 14.6026f
      )
      verticalLineTo(y = 22.5369f)
      horizontalLineTo(x = 51.3055f)
      verticalLineTo(y = 13.8617f)
      curveTo(
        x1 = 51.3055f,
        y1 = 12.2769f,
        x2 = 50.9865f,
        y2 = 11.0832f,
        x3 = 50.3484f,
        y3 = 10.2805f
      )
      curveTo(
        x1 = 49.7104f,
        y1 = 9.47781f,
        x2 = 48.7945f,
        y2 = 9.07647f,
        x3 = 47.6008f,
        y3 = 9.07647f
      )
      curveTo(
        x1 = 46.3865f,
        y1 = 9.07647f,
        x2 = 45.3677f,
        y2 = 9.56014f,
        x3 = 44.5444f,
        y3 = 10.5275f
      )
      curveTo(
        x1 = 43.7417f,
        y1 = 11.4742f,
        x2 = 43.3404f,
        y2 = 12.8326f,
        x3 = 43.3404f,
        y3 = 14.6026f
      )
      verticalLineTo(y = 22.5369f)
      horizontalLineTo(x = 40.7471f)
      close()
    }
    path(fill = color) {
      moveTo(x = 75.2481f, y = 22.9073f)
      curveTo(
        x1 = 73.7868f,
        y1 = 22.9073f,
        x2 = 72.4901f,
        y2 = 22.578f,
        x3 = 71.3581f,
        y3 = 21.9194f
      )
      curveTo(
        x1 = 70.2261f,
        y1 = 21.2402f,
        x2 = 69.3308f,
        y2 = 20.3038f,
        x3 = 68.6722f,
        y3 = 19.11f
      )
      curveTo(
        x1 = 68.0342f,
        y1 = 17.9163f,
        x2 = 67.7152f,
        y2 = 16.5064f,
        x3 = 67.7152f,
        y3 = 14.8805f
      )
      curveTo(
        x1 = 67.7152f,
        y1 = 13.2751f,
        x2 = 68.0342f,
        y2 = 11.8756f,
        x3 = 68.6722f,
        y3 = 10.6818f
      )
      curveTo(
        x1 = 69.3103f,
        y1 = 9.46752f,
        x2 = 70.1953f,
        y2 = 8.53106f,
        x3 = 71.3273f,
        y3 = 7.87244f
      )
      curveTo(
        x1 = 72.4798f,
        y1 = 7.19325f,
        x2 = 73.8073f,
        y2 = 6.85365f,
        x3 = 75.3098f,
        y3 = 6.85365f
      )
      curveTo(
        x1 = 76.7917f,
        y1 = 6.85365f,
        x2 = 78.0678f,
        y2 = 7.19325f,
        x3 = 79.138f,
        y3 = 7.87244f
      )
      curveTo(
        x1 = 80.2288f,
        y1 = 8.53106f,
        x2 = 81.0624f,
        y2 = 9.40578f,
        x3 = 81.6387f,
        y3 = 10.4966f
      )
      curveTo(
        x1 = 82.215f,
        y1 = 11.5874f,
        x2 = 82.5031f,
        y2 = 12.7606f,
        x3 = 82.5031f,
        y3 = 14.0161f
      )
      curveTo(
        x1 = 82.5031f,
        y1 = 14.2425f,
        x2 = 82.4928f,
        y2 = 14.4689f,
        x3 = 82.4722f,
        y3 = 14.6953f
      )
      curveTo(
        x1 = 82.4722f,
        y1 = 14.9217f,
        x2 = 82.4722f,
        y2 = 15.1789f,
        x3 = 82.4722f,
        y3 = 15.4671f
      )
      horizontalLineTo(x = 70.2776f)
      curveTo(
        x1 = 70.3393f,
        y1 = 16.6402f,
        x2 = 70.6069f,
        y2 = 17.6179f,
        x3 = 71.0803f,
        y3 = 18.4f
      )
      curveTo(
        x1 = 71.5742f,
        y1 = 19.1615f,
        x2 = 72.1814f,
        y2 = 19.7378f,
        x3 = 72.9018f,
        y3 = 20.1288f
      )
      curveTo(
        x1 = 73.6427f,
        y1 = 20.5199f,
        x2 = 74.4248f,
        y2 = 20.7154f,
        x3 = 75.2481f,
        y3 = 20.7154f
      )
      curveTo(
        x1 = 76.3183f,
        y1 = 20.7154f,
        x2 = 77.2136f,
        y2 = 20.4684f,
        x3 = 77.934f,
        y3 = 19.9745f
      )
      curveTo(
        x1 = 78.6543f,
        y1 = 19.4805f,
        x2 = 79.1792f,
        y2 = 18.8116f,
        x3 = 79.5085f,
        y3 = 17.9677f
      )
      horizontalLineTo(x = 82.0709f)
      curveTo(
        x1 = 81.6593f,
        y1 = 19.3879f,
        x2 = 80.8669f,
        y2 = 20.5713f,
        x3 = 79.6937f,
        y3 = 21.5181f
      )
      curveTo(
        x1 = 78.5411f,
        y1 = 22.4443f,
        x2 = 77.0592f,
        y2 = 22.9073f,
        x3 = 75.2481f,
        y3 = 22.9073f
      )
      close()
      moveTo(x = 75.2481f, y = 9.0456f)
      curveTo(
        x1 = 74.0132f,
        y1 = 9.0456f,
        x2 = 72.912f,
        y2 = 9.42636f,
        x3 = 71.9447f,
        y3 = 10.1879f
      )
      curveTo(
        x1 = 70.998f,
        y1 = 10.9288f,
        x2 = 70.4525f,
        y2 = 12.0196f,
        x3 = 70.3085f,
        y3 = 13.4604f
      )
      horizontalLineTo(x = 79.9407f)
      curveTo(
        x1 = 79.8789f,
        y1 = 12.0814f,
        x2 = 79.4056f,
        y2 = 11.0009f,
        x3 = 78.5205f,
        y3 = 10.2188f
      )
      curveTo(
        x1 = 77.6355f,
        y1 = 9.43665f,
        x2 = 76.5447f,
        y2 = 9.0456f,
        x3 = 75.2481f,
        y3 = 9.0456f
      )
      close()
    }
    path(fill = color) {
      moveTo(x = 91.8061f, y = 22.9073f)
      curveTo(
        x1 = 89.9743f,
        y1 = 22.9073f,
        x2 = 88.4513f,
        y2 = 22.4443f,
        x3 = 87.237f,
        y3 = 21.5181f
      )
      curveTo(
        x1 = 86.0226f,
        y1 = 20.5919f,
        x2 = 85.3126f,
        y2 = 19.3364f,
        x3 = 85.1068f,
        y3 = 17.7516f
      )
      horizontalLineTo(x = 87.7618f)
      curveTo(
        x1 = 87.9264f,
        y1 = 18.5543f,
        x2 = 88.3484f,
        y2 = 19.2541f,
        x3 = 89.0276f,
        y3 = 19.851f
      )
      curveTo(
        x1 = 89.7273f,
        y1 = 20.4273f,
        x2 = 90.6638f,
        y2 = 20.7154f,
        x3 = 91.837f,
        y3 = 20.7154f
      )
      curveTo(
        x1 = 92.9278f,
        y1 = 20.7154f,
        x2 = 93.7305f,
        y2 = 20.489f,
        x3 = 94.245f,
        y3 = 20.0362f
      )
      curveTo(
        x1 = 94.7596f,
        y1 = 19.5628f,
        x2 = 95.0168f,
        y2 = 19.0071f,
        x3 = 95.0168f,
        y3 = 18.3691f
      )
      curveTo(
        x1 = 95.0168f,
        y1 = 17.4429f,
        x2 = 94.6772f,
        y2 = 16.8255f,
        x3 = 93.998f,
        y3 = 16.5167f
      )
      curveTo(
        x1 = 93.3394f,
        y1 = 16.208f,
        x2 = 92.403f,
        y2 = 15.9302f,
        x3 = 91.1886f,
        y3 = 15.6832f
      )
      curveTo(
        x1 = 90.3654f,
        y1 = 15.5185f,
        x2 = 89.5421f,
        y2 = 15.2818f,
        x3 = 88.7188f,
        y3 = 14.9731f
      )
      curveTo(
        x1 = 87.8956f,
        y1 = 14.6644f,
        x2 = 87.2061f,
        y2 = 14.2322f,
        x3 = 86.6504f,
        y3 = 13.6765f
      )
      curveTo(
        x1 = 86.0947f,
        y1 = 13.1002f,
        x2 = 85.8168f,
        y2 = 12.349f,
        x3 = 85.8168f,
        y3 = 11.4228f
      )
      curveTo(
        x1 = 85.8168f,
        y1 = 10.085f,
        x2 = 86.3108f,
        y2 = 8.99414f,
        x3 = 87.2987f,
        y3 = 8.15029f
      )
      curveTo(
        x1 = 88.3072f,
        y1 = 7.28586f,
        x2 = 89.6656f,
        y2 = 6.85365f,
        x3 = 91.3739f,
        y3 = 6.85365f
      )
      curveTo(
        x1 = 92.9998f,
        y1 = 6.85365f,
        x2 = 94.3273f,
        y2 = 7.26528f,
        x3 = 95.3564f,
        y3 = 8.08855f
      )
      curveTo(
        x1 = 96.4061f,
        y1 = 8.89123f,
        x2 = 97.0132f,
        y2 = 10.0438f,
        x3 = 97.1779f,
        y3 = 11.5463f
      )
      horizontalLineTo(x = 94.6155f)
      curveTo(
        x1 = 94.5126f,
        y1 = 10.7642f,
        x2 = 94.173f,
        y2 = 10.157f,
        x3 = 93.5967f,
        y3 = 9.72479f
      )
      curveTo(
        x1 = 93.041f,
        y1 = 9.272f,
        x2 = 92.2898f,
        y2 = 9.0456f,
        x3 = 91.343f,
        y3 = 9.0456f
      )
      curveTo(
        x1 = 90.4168f,
        y1 = 9.0456f,
        x2 = 89.6965f,
        y2 = 9.24112f,
        x3 = 89.1819f,
        y3 = 9.63217f
      )
      curveTo(
        x1 = 88.688f,
        y1 = 10.0232f,
        x2 = 88.441f,
        y2 = 10.5378f,
        x3 = 88.441f,
        y3 = 11.1758f
      )
      curveTo(
        x1 = 88.441f,
        y1 = 11.7932f,
        x2 = 88.76f,
        y2 = 12.2769f,
        x3 = 89.398f,
        y3 = 12.6268f
      )
      curveTo(
        x1 = 90.0566f,
        y1 = 12.9767f,
        x2 = 90.9417f,
        y2 = 13.2751f,
        x3 = 92.0531f,
        y3 = 13.5221f
      )
      curveTo(
        x1 = 92.9998f,
        y1 = 13.7279f,
        x2 = 93.8951f,
        y2 = 13.9852f,
        x3 = 94.739f,
        y3 = 14.2939f
      )
      curveTo(
        x1 = 95.6034f,
        y1 = 14.5821f,
        x2 = 96.3032f,
        y2 = 15.0246f,
        x3 = 96.8383f,
        y3 = 15.6214f
      )
      curveTo(
        x1 = 97.394f,
        y1 = 16.1977f,
        x2 = 97.6719f,
        y2 = 17.0416f,
        x3 = 97.6719f,
        y3 = 18.153f
      )
      curveTo(
        x1 = 97.6924f,
        y1 = 19.5319f,
        x2 = 97.1676f,
        y2 = 20.6742f,
        x3 = 96.0974f,
        y3 = 21.5798f
      )
      curveTo(
        x1 = 95.0477f,
        y1 = 22.4648f,
        x2 = 93.6173f,
        y2 = 22.9073f,
        x3 = 91.8061f,
        y3 = 22.9073f
      )
      close()
    }
    path(fill = ghLogoGradient) {
      moveTo(x = 108.517f, y = 22.5369f)
      verticalLineTo(y = 14.7879f)
      horizontalLineTo(x = 116.698f)
      verticalLineTo(y = 22.5369f)
      horizontalLineTo(x = 122.41f)
      verticalLineTo(y = 1.69799f)
      horizontalLineTo(x = 116.698f)
      verticalLineTo(y = 9.5396f)
      horizontalLineTo(x = 108.517f)
      verticalLineTo(y = 1.69799f)
      horizontalLineTo(x = 102.806f)
      verticalLineTo(y = 22.5369f)
      horizontalLineTo(x = 108.517f)
      close()
    }
    path(fill = ghLogoGradient) {
      moveTo(x = 134.231f, y = 17.5973f)
      curveTo(
        x1 = 132.441f,
        y1 = 17.5973f,
        x2 = 131.669f,
        y2 = 15.9611f,
        x3 = 131.669f,
        y3 = 14.0161f
      )
      verticalLineTo(y = 7.10067f)
      horizontalLineTo(x = 126.112f)
      verticalLineTo(y = 14.6953f)
      curveTo(
        x1 = 126.112f,
        y1 = 19.2336f,
        x2 = 128.767f,
        y2 = 23f,
        x3 = 134.231f,
        y3 = 23f
      )
      curveTo(
        x1 = 139.696f,
        y1 = 23f,
        x2 = 142.351f,
        y2 = 19.2336f,
        x3 = 142.351f,
        y3 = 14.6953f
      )
      verticalLineTo(y = 7.10067f)
      horizontalLineTo(x = 136.794f)
      verticalLineTo(y = 14.0161f)
      curveTo(
        x1 = 136.794f,
        y1 = 15.9611f,
        x2 = 136.022f,
        y2 = 17.5973f,
        x3 = 134.231f,
        y3 = 17.5973f
      )
      close()
    }
    path(fill = ghLogoGradient) {
      moveTo(x = 153.633f, y = 23f)
      curveTo(
        x1 = 158.449f,
        y1 = 23f,
        x2 = 162.123f,
        y2 = 19.3262f,
        x3 = 162.123f,
        y3 = 14.8497f
      )
      curveTo(
        x1 = 162.123f,
        y1 = 9.91007f,
        x2 = 158.851f,
        y2 = 6.63758f,
        x3 = 153.942f,
        y3 = 6.63758f
      )
      curveTo(
        x1 = 152.8f,
        y1 = 6.63758f,
        x2 = 151.657f,
        y2 = 6.97718f,
        x3 = 150.7f,
        y3 = 7.50201f
      )
      verticalLineTo(y = 0f)
      horizontalLineTo(x = 145.143f)
      verticalLineTo(y = 14.8497f)
      curveTo(
        x1 = 145.143f,
        y1 = 19.5732f,
        x2 = 148.694f,
        y2 = 23f,
        x3 = 153.633f,
        y3 = 23f
      )
      close()
      moveTo(x = 153.633f, y = 17.7826f)
      curveTo(
        x1 = 151.873f,
        y1 = 17.7826f,
        x2 = 150.7f,
        y2 = 16.455f,
        x3 = 150.7f,
        y3 = 14.8188f
      )
      curveTo(
        x1 = 150.7f,
        y1 = 13.2134f,
        x2 = 151.873f,
        y2 = 11.855f,
        x3 = 153.633f,
        y3 = 11.855f
      )
      curveTo(
        x1 = 155.393f,
        y1 = 11.855f,
        x2 = 156.566f,
        y2 = 13.2134f,
        x3 = 156.566f,
        y3 = 14.8188f
      )
      curveTo(
        x1 = 156.566f,
        y1 = 16.455f,
        x2 = 155.393f,
        y2 = 17.7826f,
        x3 = 153.633f,
        y3 = 17.7826f
      )
      close()
    }
  }
  .build()
