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
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestAppcoinsIconWithBackground() {
  Image(
    imageVector = getAppcoinsIconWithBackground(Color.Magenta, Color.White),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getAppcoinsIconWithBackground(iconColor: Color, backgroundColor: Color): ImageVector =
  Builder(
    name = "Appcoinsiconwithbackground",
    defaultWidth =
    24.0.dp,
    defaultHeight = 24.0.dp,
    viewportWidth = 24.0f,
    viewportHeight =
    24.0f
  ).apply {
    path(
      fill = SolidColor(backgroundColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(11.9939f, 0.0f)
      curveTo(7.2729f, -0.0031f, 2.9931f, 2.7661f, 1.06f, 7.0729f)
      curveTo(0.3584f, 8.6229f, 0.0f, 10.3045f, 0.0f, 12.0046f)
      curveTo(0.0f, 18.6302f, 5.3735f, 24.0f, 12.0f, 24.0f)
      curveTo(18.6265f, 24.0f, 24.0f, 18.6302f, 24.0f, 12.0046f)
      curveTo(24.0f, 10.3045f, 23.6416f, 8.6229f, 22.94f, 7.0729f)
      curveTo(20.9977f, 2.7691f, 16.7118f, 0.0031f, 11.9877f, 0.0f)
      horizontalLineTo(11.9939f)
      close()
    }
    path(
      fill = SolidColor(iconColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(10.4621f, 12.8775f)
      lineTo(12.0214f, 8.1326f)
      lineTo(13.6084f, 12.8775f)
      horizontalLineTo(10.4621f)
      close()
      moveTo(15.7804f, 12.3353f)
      horizontalLineTo(16.5831f)
      curveTo(16.8772f, 12.3353f, 17.1161f, 12.0964f, 17.1161f, 11.8023f)
      verticalLineTo(11.7931f)
      curveTo(17.11f, 11.4929f, 16.8649f, 11.2509f, 16.5616f, 11.2509f)
      horizontalLineTo(15.3791f)
      lineTo(15.1677f, 10.6996f)
      horizontalLineTo(16.5463f)
      curveTo(16.8465f, 10.6996f, 17.0886f, 10.4576f, 17.0886f, 10.1574f)
      curveTo(17.0886f, 9.8572f, 16.8465f, 9.6152f, 16.5463f, 9.6152f)
      horizontalLineTo(14.7694f)
      lineTo(13.7983f, 6.9717f)
      curveTo(13.6543f, 6.5918f, 13.4184f, 6.2579f, 13.1059f, 5.9976f)
      curveTo(12.7996f, 5.7464f, 12.4136f, 5.6178f, 12.0184f, 5.6331f)
      curveTo(11.6232f, 5.6208f, 11.2341f, 5.7495f, 10.9247f, 5.9976f)
      curveTo(10.6152f, 6.2579f, 10.3794f, 6.5949f, 10.2354f, 6.9717f)
      lineTo(9.2366f, 9.6336f)
      horizontalLineTo(7.4598f)
      curveTo(7.1657f, 9.6336f, 6.9298f, 9.8725f, 6.9298f, 10.1635f)
      verticalLineTo(10.1696f)
      curveTo(6.9328f, 10.4668f, 7.1749f, 10.7057f, 7.472f, 10.7087f)
      lineTo(8.8292f, 10.7179f)
      lineTo(8.6239f, 11.254f)
      horizontalLineTo(7.4077f)
      curveTo(7.1136f, 11.254f, 6.8777f, 11.4929f, 6.8777f, 11.7839f)
      curveTo(6.8777f, 12.078f, 7.1136f, 12.32f, 7.4077f, 12.3261f)
      lineTo(8.2134f, 12.3414f)
      lineTo(6.8899f, 15.8825f)
      curveTo(6.8072f, 16.0816f, 6.7582f, 16.296f, 6.749f, 16.5135f)
      curveTo(6.7643f, 16.8167f, 6.8961f, 17.0985f, 7.1167f, 17.3068f)
      curveTo(7.3587f, 17.5182f, 7.6712f, 17.6315f, 7.9898f, 17.6285f)
      curveTo(8.5443f, 17.656f, 9.0436f, 17.2946f, 9.1907f, 16.7585f)
      lineTo(9.6992f, 15.2453f)
      horizontalLineTo(14.3804f)
      lineTo(14.8889f, 16.7953f)
      curveTo(15.0513f, 17.313f, 15.5476f, 17.656f, 16.0898f, 17.6223f)
      curveTo(16.292f, 17.6285f, 16.4912f, 17.5764f, 16.6689f, 17.4753f)
      curveTo(16.822f, 17.362f, 16.9568f, 17.2272f, 17.0702f, 17.0771f)
      curveTo(17.1713f, 16.9025f, 17.2234f, 16.7034f, 17.2172f, 16.5012f)
      curveTo(17.1866f, 16.2929f, 17.1345f, 16.0846f, 17.0702f, 15.8855f)
      lineTo(15.7804f, 12.3414f)
      verticalLineTo(12.3353f)
      close()
    }
    path(
      fill = SolidColor(iconColor),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(12.0001f, 2.5729f)
      curveTo(6.7951f, 2.576f, 2.5766f, 6.794f, 2.5735f, 11.9983f)
      curveTo(2.5796f, 17.2027f, 6.7982f, 21.4176f, 12.0001f, 21.4237f)
      curveTo(17.2051f, 21.4206f, 21.4236f, 17.2027f, 21.4267f, 11.9983f)
      curveTo(21.4206f, 6.794f, 17.202f, 2.579f, 12.0001f, 2.5729f)
      close()
      moveTo(12.0001f, 22.1252f)
      curveTo(6.4121f, 22.1221f, 1.8811f, 17.5917f, 1.875f, 12.0044f)
      curveTo(1.8811f, 6.4141f, 6.4091f, 1.8837f, 12.0001f, 1.8745f)
      curveTo(17.588f, 1.8776f, 22.1191f, 6.408f, 22.1252f, 11.9953f)
      curveTo(22.116f, 17.5825f, 17.588f, 22.1129f, 12.0001f, 22.1252f)
      close()
    }
  }.build()
