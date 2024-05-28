package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun VectorPreview() {
  Image(getAptoideLogo(Color.Black), null)
}

fun getAptoideLogo(color: Color): ImageVector = ImageVector.Builder(
  name = "Aptoidelogo",
  defaultWidth = 95.dp,
  defaultHeight = 24.dp,
  viewportWidth = 95f,
  viewportHeight = 24f
).apply {
  group {
    group {
      path(
        fill = SolidColor(color),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
      ) {
        moveTo(29.2558f, 16.2178f)
        lineTo(33.1826f, 5.5797f)
        curveTo(33.3514f, 5.1109f, 33.6262f, 4.7054f, 34.0064f, 4.407f)
        curveTo(34.3653f, 4.1086f, 34.8089f, 3.959f, 35.2731f, 3.959f)
        curveTo(35.7374f, 3.9381f, 36.2016f, 4.1086f, 36.5612f, 4.407f)
        curveTo(36.9414f, 4.7269f, 37.2155f, 5.1325f, 37.3636f, 5.6013f)
        lineTo(41.1844f, 16.2394f)
        curveTo(41.2691f, 16.4738f, 41.3325f, 16.7089f, 41.3325f, 16.9642f)
        curveTo(41.3325f, 17.1986f, 41.2691f, 17.4337f, 41.1431f, 17.6257f)
        curveTo(41.0163f, 17.817f, 40.8476f, 17.9881f, 40.6575f, 18.073f)
        curveTo(40.4467f, 18.1801f, 40.2139f, 18.2434f, 39.9818f, 18.2434f)
        curveTo(39.3274f, 18.2858f, 38.7364f, 17.881f, 38.5463f, 17.241f)
        lineTo(37.9546f, 15.4075f)
        horizontalLineTo(32.529f)
        lineTo(31.938f, 17.2202f)
        curveTo(31.7899f, 17.8594f, 31.1989f, 18.2858f, 30.5446f, 18.2434f)
        curveTo(30.1643f, 18.2434f, 29.8055f, 18.1154f, 29.5306f, 17.8386f)
        curveTo(29.2565f, 17.5826f, 29.1084f, 17.2418f, 29.1084f, 16.8794f)
        curveTo(29.1298f, 16.645f, 29.1718f, 16.4314f, 29.2558f, 16.2178f)
        close()
        moveTo(35.2304f, 7.00771f)
        lineTo(33.3941f, 12.6787f)
        horizontalLineTo(37.0888f)
        lineTo(35.2731f, 7.00771f)
        horizontalLineTo(35.2304f)
        close()
      }
      path(
        fill = SolidColor(color),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
      ) {
        moveTo(42.1556f, 19.5866f)
        verticalLineTo(9.16132f)
        curveTo(42.1342f, 8.7565f, 42.2823f, 8.351f, 42.5358f, 8.031f)
        curveTo(42.7893f, 7.7326f, 43.1695f, 7.583f, 43.549f, 7.6046f)
        curveTo(44.2881f, 7.6046f, 44.7737f, 7.9886f, 44.9638f, 8.7766f)
        curveTo(45.238f, 8.3502f, 45.6182f, 8.0094f, 46.0831f, 7.8174f)
        curveTo(46.5687f, 7.6046f, 47.0964f, 7.4759f, 47.624f, 7.4975f)
        curveTo(48.5111f, 7.4759f, 49.3763f, 7.7534f, 50.0947f, 8.2863f)
        curveTo(50.7911f, 8.8191f, 51.3187f, 9.5446f, 51.6355f, 10.3549f)
        curveTo(51.9951f, 11.2077f, 52.1631f, 12.1453f, 52.1631f, 13.0628f)
        curveTo(52.1631f, 13.9803f, 51.9737f, 14.8324f, 51.5935f, 15.6211f)
        curveTo(51.2346f, 16.4099f, 50.6643f, 17.0923f, 49.9466f, 17.6042f)
        curveTo(49.2495f, 18.0946f, 48.4058f, 18.3722f, 47.5392f, 18.3506f)
        curveTo(47.075f, 18.3506f, 46.6321f, 18.265f, 46.1885f, 18.0946f)
        curveTo(45.7449f, 17.9242f, 45.3227f, 17.6898f, 44.9638f, 17.3698f)
        verticalLineTo(19.5658f)
        curveTo(44.9638f, 19.8433f, 44.9005f, 20.1201f, 44.7737f, 20.3754f)
        curveTo(44.6676f, 20.6098f, 44.4782f, 20.8018f, 44.2668f, 20.9298f)
        curveTo(44.056f, 21.0578f, 43.8025f, 21.121f, 43.549f, 21.121f)
        curveTo(43.1688f, 21.1426f, 42.7886f, 20.9722f, 42.5358f, 20.6947f)
        curveTo(42.2823f, 20.3963f, 42.135f, 19.9914f, 42.1556f, 19.5866f)
        close()
        moveTo(44.9638f, 12.8493f)
        curveTo(44.9425f, 13.574f, 45.1532f, 14.278f, 45.5335f, 14.8963f)
        curveTo(46.1038f, 15.8131f, 47.3071f, 16.0691f, 48.1936f, 15.4932f)
        curveTo(48.257f, 15.4507f, 48.299f, 15.4083f, 48.3623f, 15.3867f)
        curveTo(48.7005f, 15.1099f, 48.9533f, 14.7259f, 49.1228f, 14.3204f)
        curveTo(49.2915f, 13.894f, 49.3763f, 13.4252f, 49.3763f, 12.9557f)
        curveTo(49.3763f, 12.4861f, 49.2915f, 12.0173f, 49.1228f, 11.5909f)
        curveTo(48.9747f, 11.1646f, 48.7212f, 10.8022f, 48.3837f, 10.5038f)
        curveTo(48.0462f, 10.2262f, 47.6233f, 10.0774f, 47.1804f, 10.0774f)
        curveTo(46.7368f, 10.0558f, 46.3153f, 10.2054f, 45.977f, 10.4822f)
        curveTo(45.6395f, 10.7597f, 45.3861f, 11.1006f, 45.238f, 11.5054f)
        curveTo(45.0479f, 11.9318f, 44.9638f, 12.3797f, 44.9638f, 12.8485f)
      }
      path(
        fill = SolidColor(color),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
      ) {
        moveTo(54.0422f, 16.6658f)
        verticalLineTo(10.1844f)
        horizontalLineTo(53.4092f)
        curveTo(53.0717f, 10.2052f, 52.7548f, 10.0773f, 52.5227f, 9.8429f)
        curveTo(52.2899f, 9.6084f, 52.1638f, 9.2669f, 52.1852f, 8.9476f)
        curveTo(52.1852f, 8.6277f, 52.3112f, 8.3084f, 52.5441f, 8.074f)
        curveTo(52.7762f, 7.8396f, 53.093f, 7.7116f, 53.4099f, 7.7325f)
        horizontalLineTo(54.0429f)
        verticalLineTo(6.04777f)
        curveTo(54.0215f, 5.643f, 54.1703f, 5.2381f, 54.4231f, 4.9174f)
        curveTo(54.6759f, 4.619f, 55.0568f, 4.4695f, 55.4363f, 4.4911f)
        curveTo(55.6898f, 4.4911f, 55.9433f, 4.555f, 56.154f, 4.6823f)
        curveTo(56.3648f, 4.8103f, 56.5343f, 5.0023f, 56.661f, 5.2367f)
        curveTo(56.7877f, 5.4927f, 56.8518f, 5.7695f, 56.8518f, 6.0463f)
        verticalLineTo(7.70947f)
        horizontalLineTo(57.6536f)
        curveTo(57.9918f, 7.6879f, 58.3293f, 7.7943f, 58.6248f, 8.0079f)
        curveTo(58.8989f, 8.2423f, 59.0256f, 8.5831f, 59.005f, 8.9462f)
        curveTo(59.0256f, 9.3086f, 58.8989f, 9.6494f, 58.6248f, 9.8846f)
        curveTo(58.3499f, 10.0974f, 58.0124f, 10.2038f, 57.6536f, 10.183f)
        horizontalLineTo(56.8518f)
        verticalLineTo(16.6643f)
        curveTo(56.8518f, 16.9419f, 56.7877f, 17.2187f, 56.661f, 17.4747f)
        curveTo(56.5556f, 17.7091f, 56.3655f, 17.9011f, 56.154f, 18.0291f)
        curveTo(55.9433f, 18.1571f, 55.6891f, 18.2211f, 55.4363f, 18.2211f)
        curveTo(55.0568f, 18.2419f, 54.6759f, 18.0715f, 54.4231f, 17.7947f)
        curveTo(54.1703f, 17.4963f, 54.0215f, 17.0915f, 54.0429f, 16.6651f)
      }
      path(
        fill = SolidColor(color),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
      ) {
        moveTo(63.7534f, 7.47656f)
        curveTo(64.4505f, 7.4766f, 65.1468f, 7.6261f, 65.7798f, 7.9245f)
        curveTo(66.3922f, 8.2021f, 66.9411f, 8.6285f, 67.384f, 9.1397f)
        curveTo(67.8276f, 9.6517f, 68.1865f, 10.2484f, 68.4186f, 10.8884f)
        curveTo(68.6721f, 11.5492f, 68.7988f, 12.2531f, 68.7775f, 12.9563f)
        curveTo(68.7775f, 13.6603f, 68.6507f, 14.3851f, 68.4186f, 15.0459f)
        curveTo(68.1865f, 15.6851f, 67.8483f, 16.261f, 67.4054f, 16.773f)
        curveTo(66.9618f, 17.2634f, 66.4135f, 17.6682f, 65.8005f, 17.9457f)
        curveTo(65.1461f, 18.2441f, 64.4498f, 18.3937f, 63.7527f, 18.3721f)
        curveTo(62.8235f, 18.3937f, 61.9164f, 18.1377f, 61.1346f, 17.6473f)
        curveTo(60.3748f, 17.1569f, 59.7625f, 16.4746f, 59.3403f, 15.6642f)
        curveTo(58.918f, 14.8323f, 58.7073f, 13.8947f, 58.7073f, 12.9563f)
        curveTo(58.7073f, 12.2531f, 58.8347f, 11.5708f, 59.0661f, 10.91f)
        curveTo(59.2983f, 10.27f, 59.6571f, 9.6732f, 60.1007f, 9.1404f)
        curveTo(60.5443f, 8.6285f, 61.0933f, 8.2236f, 61.7056f, 7.9252f)
        curveTo(62.36f, 7.6268f, 63.0563f, 7.4773f, 63.7534f, 7.4773f)
        moveTo(63.7534f, 10.0572f)
        curveTo(63.3098f, 10.0572f, 62.8883f, 10.1852f, 62.5501f, 10.4836f)
        curveTo(62.2126f, 10.7611f, 61.9377f, 11.1444f, 61.7903f, 11.5708f)
        curveTo(61.6216f, 12.0187f, 61.5369f, 12.4875f, 61.5369f, 12.9563f)
        curveTo(61.5369f, 13.4252f, 61.6216f, 13.8947f, 61.7903f, 14.3211f)
        curveTo(61.9385f, 14.7259f, 62.2126f, 15.1099f, 62.5501f, 15.3874f)
        curveTo(63.2678f, 15.9202f, 64.2604f, 15.9202f, 64.9781f, 15.3874f)
        curveTo(65.3156f, 15.1106f, 65.5691f, 14.7482f, 65.7378f, 14.3211f)
        curveTo(65.9066f, 13.8731f, 65.9913f, 13.4259f, 65.9913f, 12.9563f)
        curveTo(65.9913f, 12.4868f, 65.9066f, 12.018f, 65.7378f, 11.5916f)
        curveTo(65.5904f, 11.1652f, 65.337f, 10.8028f, 64.9781f, 10.5044f)
        curveTo(64.6192f, 10.206f, 64.197f, 10.0565f, 63.7534f, 10.0565f)
      }
      path(
        fill = SolidColor(color),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
      ) {
        moveTo(71.333f, 3.38306f)
        curveTo(71.6071f, 3.3831f, 71.8819f, 3.4686f, 72.114f, 3.5966f)
        curveTo(72.3462f, 3.7462f, 72.5576f, 3.9374f, 72.6844f, 4.1718f)
        curveTo(72.8325f, 4.3847f, 72.8951f, 4.6622f, 72.8951f, 4.9182f)
        curveTo(72.8951f, 5.3446f, 72.7264f, 5.7494f, 72.4316f, 6.0701f)
        curveTo(71.8613f, 6.6885f, 70.9114f, 6.7093f, 70.2984f, 6.1341f)
        curveTo(70.2777f, 6.1125f, 70.235f, 6.0701f, 70.2136f, 6.0492f)
        curveTo(69.9189f, 5.7501f, 69.7288f, 5.3453f, 69.7288f, 4.9189f)
        curveTo(69.7288f, 4.4925f, 69.8982f, 4.1086f, 70.2136f, 3.8526f)
        curveTo(70.5099f, 3.5549f, 70.9107f, 3.3838f, 71.333f, 3.3838f)
      }
      path(
        fill = SolidColor(color),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
      ) {
        moveTo(69.9403f, 16.6659f)
        verticalLineTo(9.16057f)
        curveTo(69.9189f, 8.7558f, 70.0663f, 8.3509f, 70.3198f, 8.0302f)
        curveTo(70.5733f, 7.7318f, 70.9535f, 7.5823f, 71.333f, 7.6039f)
        curveTo(71.5865f, 7.6039f, 71.84f, 7.6678f, 72.0507f, 7.7951f)
        curveTo(72.2622f, 7.9231f, 72.431f, 8.1151f, 72.557f, 8.3495f)
        curveTo(72.6837f, 8.6055f, 72.7471f, 8.8823f, 72.7471f, 9.1599f)
        verticalLineTo(16.6644f)
        curveTo(72.7471f, 16.942f, 72.6837f, 17.2188f, 72.557f, 17.4748f)
        curveTo(72.4516f, 17.7092f, 72.2615f, 17.9012f, 72.0507f, 18.0292f)
        curveTo(71.84f, 18.1572f, 71.5865f, 18.2211f, 71.333f, 18.2211f)
        curveTo(70.9528f, 18.242f, 70.5733f, 18.0716f, 70.3198f, 17.7948f)
        curveTo(70.0457f, 17.4964f, 69.9189f, 17.0915f, 69.9403f, 16.6651f)
      }
      path(
        fill = SolidColor(color),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
      ) {
        moveTo(83.8953f, 4.61973f)
        verticalLineTo(16.6658f)
        curveTo(83.8953f, 16.9426f, 83.8312f, 17.2201f, 83.7051f, 17.4761f)
        curveTo(83.599f, 17.7105f, 83.4096f, 17.9025f, 83.1989f, 18.0305f)
        curveTo(82.9874f, 18.1585f, 82.7346f, 18.2225f, 82.4812f, 18.2225f)
        curveTo(81.7207f, 18.2225f, 81.2565f, 17.8385f, 81.087f, 17.0497f)
        curveTo(80.8129f, 17.4761f, 80.4327f, 17.7961f, 79.9891f, 18.0089f)
        curveTo(79.5248f, 18.2225f, 79.0186f, 18.3505f, 78.5116f, 18.3505f)
        curveTo(77.6458f, 18.372f, 76.8013f, 18.0945f, 76.105f, 17.6041f)
        curveTo(75.3865f, 17.0929f, 74.8376f, 16.4105f, 74.458f, 15.6217f)
        curveTo(74.0785f, 14.8322f, 73.8884f, 13.9377f, 73.8884f, 13.0634f)
        curveTo(73.8884f, 12.125f, 74.0572f, 11.209f, 74.3947f, 10.3555f)
        curveTo(74.7115f, 9.5458f, 75.2391f, 8.8203f, 75.9355f, 8.2875f)
        curveTo(76.6532f, 7.7547f, 77.5191f, 7.4772f, 78.4055f, 7.4988f)
        curveTo(78.7224f, 7.4988f, 79.0392f, 7.5412f, 79.3554f, 7.6052f)
        curveTo(79.6722f, 7.6692f, 79.9677f, 7.7756f, 80.2632f, 7.9252f)
        curveTo(80.5587f, 8.0747f, 80.8335f, 8.2451f, 81.087f, 8.4795f)
        verticalLineTo(4.64202f)
        curveTo(81.0664f, 4.2372f, 81.2138f, 3.8324f, 81.4672f, 3.5124f)
        curveTo(81.7207f, 3.214f, 82.1002f, 3.0645f, 82.4805f, 3.086f)
        curveTo(82.7339f, 3.086f, 82.9874f, 3.15f, 83.2195f, 3.278f)
        curveTo(83.431f, 3.406f, 83.5998f, 3.598f, 83.7265f, 3.8324f)
        curveTo(83.8312f, 4.0668f, 83.8953f, 4.3436f, 83.8953f, 4.6212f)
        moveTo(81.087f, 12.8505f)
        curveTo(81.1084f, 12.1473f, 80.8969f, 11.465f, 80.5167f, 10.889f)
        curveTo(80.1578f, 10.3562f, 79.5248f, 10.0362f, 78.8911f, 10.0571f)
        curveTo(78.4482f, 10.0571f, 78.0474f, 10.2066f, 77.7085f, 10.4835f)
        curveTo(77.371f, 10.7819f, 77.1175f, 11.1443f, 76.9701f, 11.5707f)
        curveTo(76.8013f, 12.0186f, 76.7166f, 12.4659f, 76.7166f, 12.9347f)
        curveTo(76.7166f, 13.2762f, 76.7593f, 13.617f, 76.8647f, 13.937f)
        curveTo(76.9487f, 14.2786f, 77.0968f, 14.5762f, 77.2862f, 14.8754f)
        curveTo(77.4557f, 15.1522f, 77.7084f, 15.3657f, 77.9826f, 15.5361f)
        curveTo(78.2574f, 15.7066f, 78.5956f, 15.7921f, 78.9118f, 15.7706f)
        curveTo(79.3554f, 15.7921f, 79.7982f, 15.6217f, 80.1357f, 15.3449f)
        curveTo(80.474f, 15.0465f, 80.7267f, 14.6625f, 80.8748f, 14.2361f)
        curveTo(81.0016f, 13.7882f, 81.0856f, 13.3194f, 81.0856f, 12.8505f)
      }
      path(
        fill = SolidColor(color),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
      ) {
        moveTo(93.7332f, 13.8724f)
        horizontalLineTo(87.7579f)
        curveTo(87.8212f, 14.4908f, 88.1381f, 15.0452f, 88.6237f, 15.386f)
        curveTo(89.1513f, 15.7484f, 89.7636f, 15.9188f, 90.3966f, 15.8979f)
        curveTo(90.7768f, 15.8979f, 91.1357f, 15.8339f, 91.4732f, 15.7059f)
        curveTo(91.7687f, 15.578f, 92.1489f, 15.386f, 92.5918f, 15.1307f)
        curveTo(92.8873f, 14.9387f, 93.2255f, 14.8107f, 93.563f, 14.7683f)
        curveTo(93.7524f, 14.7683f, 93.9646f, 14.8323f, 94.1113f, 14.9395f)
        curveTo(94.28f, 15.0459f, 94.4281f, 15.2163f, 94.5549f, 15.3867f)
        curveTo(94.661f, 15.5571f, 94.7236f, 15.7707f, 94.7236f, 15.9626f)
        curveTo(94.703f, 16.3042f, 94.5549f, 16.6026f, 94.3227f, 16.837f)
        curveTo(94.0059f, 17.157f, 93.6477f, 17.4122f, 93.2462f, 17.6042f)
        curveTo(92.7606f, 17.8386f, 92.2536f, 18.0306f, 91.7267f, 18.1586f)
        curveTo(91.1991f, 18.2866f, 90.6708f, 18.3506f, 90.1431f, 18.3714f)
        curveTo(89.1933f, 18.3923f, 88.2435f, 18.137f, 87.3983f, 17.6675f)
        curveTo(86.5965f, 17.2202f, 85.9628f, 16.5372f, 85.5406f, 15.7275f)
        curveTo(85.097f, 14.8747f, 84.8649f, 13.9155f, 84.8855f, 12.9348f)
        curveTo(84.8855f, 12.2308f, 85.013f, 11.5492f, 85.2658f, 10.8877f)
        curveTo(85.4979f, 10.2485f, 85.8567f, 9.6517f, 86.3217f, 9.1181f)
        curveTo(87.7365f, 7.4974f, 90.0378f, 7.0286f, 91.9795f, 7.9245f)
        curveTo(92.5918f, 8.2014f, 93.1408f, 8.6285f, 93.5837f, 9.1181f)
        curveTo(94.0272f, 9.6092f, 94.3655f, 10.1629f, 94.6182f, 10.7813f)
        curveTo(94.8504f, 11.3565f, 94.9771f, 11.9749f, 94.9985f, 12.5932f)
        curveTo(94.9771f, 13.446f, 94.5549f, 13.8724f, 93.7318f, 13.8724f)
        moveTo(87.6938f, 11.9749f)
        horizontalLineTo(92.1482f)
        curveTo(92.1062f, 11.3996f, 91.8527f, 10.8445f, 91.4519f, 10.4182f)
        curveTo(91.0503f, 10.0133f, 90.4806f, 9.7998f, 89.9103f, 9.8214f)
        curveTo(89.34f, 9.7998f, 88.8124f, 10.0133f, 88.4115f, 10.4182f)
        curveTo(88.0106f, 10.8453f, 87.7571f, 11.3989f, 87.6938f, 11.9749f)
        close()
      }
      path(
        fill = SolidColor(color),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero
      ) {
        moveTo(23.1787f, 4.89376f)
        curveTo(22.6424f, 2.6842f, 21.2654f, 1.3015f, 19.0241f, 0.7845f)
        curveTo(16.7199f, 0.2689f, 14.3963f, 0f, 12.0519f, 0f)
        curveTo(9.7075f, 0f, 7.4033f, 0.2689f, 5.1627f, 0.8053f)
        curveTo(2.9616f, 1.3216f, 1.46f, 2.5396f, 0.9044f, 4.7708f)
        curveTo(0.3093f, 7.1242f, 0f, 9.5603f, 0f, 11.9964f)
        curveTo(0f, 14.4325f, 0.2678f, 16.7881f, 0.8227f, 19.1005f)
        curveTo(1.3569f, 21.3108f, 2.7353f, 22.6942f, 4.9766f, 23.2105f)
        curveTo(9.5429f, 24.2624f, 14.2731f, 24.2624f, 18.838f, 23.2105f)
        curveTo(21.0398f, 22.6942f, 22.5407f, 21.4747f, 23.0949f, 19.2242f)
        curveTo(23.6914f, 16.8708f, 24f, 14.4332f, 24f, 11.9957f)
        curveTo(24f, 9.5581f, 23.7336f, 7.2055f, 23.178f, 4.893f)
        lineTo(23.1787f, 4.89376f)
        close()
        moveTo(6.06713f, 14.1873f)
        curveTo(6.0056f, 14.1248f, 5.944f, 14.0629f, 5.8824f, 14.0011f)
        curveTo(5.6153f, 13.6905f, 5.4499f, 13.2777f, 5.4091f, 12.8449f)
        lineTo(5.30668f, 6.897f)
        curveTo(5.2859f, 6.4433f, 5.5945f, 6.2765f, 5.964f, 6.5454f)
        lineTo(11.6001f, 10.6338f)
        lineTo(6.33493f, 14.4124f)
        lineTo(6.06785f, 14.1866f)
        lineTo(6.06713f, 14.1873f)
        close()
        moveTo(18.7148f, 12.8859f)
        curveTo(18.6747f, 13.4014f, 18.4477f, 13.8774f, 18.0575f, 14.2276f)
        lineTo(12.71f, 18.4821f)
        curveTo(12.3204f, 18.7719f, 11.8056f, 18.7719f, 11.4139f, 18.4821f)
        lineTo(7.52788f, 15.3435f)
        lineTo(6.78676f, 14.7654f)
        lineTo(12.0712f, 10.966f)
        lineTo(14.6634f, 9.10804f)
        lineTo(18.3038f, 6.5044f)
        curveTo(18.6131f, 6.3815f, 18.8172f, 6.5468f, 18.8172f, 6.9387f)
        lineTo(18.7148f, 12.8859f)
        close()
      }
    }
  }
}.build()

