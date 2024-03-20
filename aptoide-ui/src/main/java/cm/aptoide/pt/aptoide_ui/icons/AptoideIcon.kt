package cm.aptoide.pt.aptoide_ui.icons

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
import cm.aptoide.pt.aptoide_ui.theme.aptoideIconOrange
import cm.aptoide.pt.aptoide_ui.theme.textWhite

@Preview
@Composable
fun TestAptoideIcon() {
  Image(
    imageVector = getAptoideIcon(aptoideIconOrange),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getAptoideIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "AptoideIcon",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {

  path(fill = SolidColor(textWhite)) {
    moveTo(4f, 4f)
    horizontalLineTo(20f)
    verticalLineTo(20f)
    horizontalLineTo(4f)
    verticalLineTo(4f)
    close()
  }

  path(fill = SolidColor(color)) {
    moveTo(23.179f, 4.89395f)
    curveTo(22.6427f, 2.68435f, 21.2657f, 1.30164f, 19.0244f, 0.784652f)
    curveTo(16.7202f, 0.269103f, 14.3966f, 0.000183105f, 12.0522f, 0.000183105f)
    curveTo(9.70786f, 0.000183105f, 7.40359f, 0.269103f, 5.16306f, 0.805504f)
    curveTo(2.96191f, 1.32177f, 1.46034f, 2.53982f, 0.904682f, 4.77099f)
    curveTo(0.309641f, 7.1244f, 0.000305176f, 9.56049f, 0.000305176f, 11.9966f)
    curveTo(0.000305176f, 14.4327f, 0.26811f, 16.7882f, 0.823052f, 19.1007f)
    curveTo(1.35723f, 21.311f, 2.73563f, 22.6944f, 4.97688f, 23.2107f)
    curveTo(9.54316f, 24.2626f, 14.2734f, 24.2626f, 18.8383f, 23.2107f)
    curveTo(21.0401f, 22.6944f, 22.541f, 21.4749f, 23.0952f, 19.2243f)
    curveTo(23.6917f, 16.8709f, 24.0003f, 14.4334f, 24.0003f, 11.9959f)
    curveTo(24.0003f, 9.55833f, 23.7339f, 7.20565f, 23.1783f, 4.89323f)
    lineTo(23.179f, 4.89395f)
    close()
    moveTo(6.06744f, 14.1875f)
    curveTo(6.00585f, 14.1249f, 5.94427f, 14.0631f, 5.88269f, 14.0013f)
    curveTo(5.6156f, 13.6906f, 5.4502f, 13.2779f, 5.40938f, 12.8451f)
    lineTo(5.30699f, 6.89718f)
    curveTo(5.28622f, 6.44347f, 5.59484f, 6.27665f, 5.96432f, 6.54557f)
    lineTo(11.6004f, 10.634f)
    lineTo(6.33524f, 14.4125f)
    lineTo(6.06815f, 14.1868f)
    lineTo(6.06744f, 14.1875f)
    close()
    moveTo(18.7151f, 12.886f)
    curveTo(18.675f, 13.4016f, 18.448f, 13.8776f, 18.0578f, 14.2278f)
    lineTo(12.7103f, 18.4823f)
    curveTo(12.3207f, 18.7721f, 11.8059f, 18.7721f, 11.4142f, 18.4823f)
    lineTo(7.52819f, 15.3437f)
    lineTo(6.78707f, 14.7656f)
    lineTo(12.0716f, 10.9662f)
    lineTo(14.6637f, 9.10822f)
    lineTo(18.3041f, 6.50459f)
    curveTo(18.6134f, 6.38163f, 18.8175f, 6.54701f, 18.8175f, 6.93888f)
    lineTo(18.7151f, 12.886f)
    close()
  }
}.build()
