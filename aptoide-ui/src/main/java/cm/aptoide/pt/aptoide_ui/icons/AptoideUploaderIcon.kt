package cm.aptoide.pt.aptoide_ui.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.theme.blueGradient
import cm.aptoide.pt.theme.darkBlue
import cm.aptoide.pt.theme.iconsBlack
import cm.aptoide.pt.theme.textWhite

@Preview
@Composable
fun TestAptoideUploaderIcon() {
  Image(
    imageVector = getAptoideUploaderIcon(blueGradient, darkBlue, iconsBlack, textWhite),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getAptoideUploaderIcon(
  primaryGradient: Brush,
  primaryColor: Color,
  secondaryColor: Color,
  backgroundColor: Color
): ImageVector = ImageVector.Builder(
  name = "AptoideUploaderIcon",
  defaultWidth = 56.dp,
  defaultHeight = 58.dp,
  viewportWidth = 56f,
  viewportHeight = 58f,
).apply {
  path(fill = SolidColor(primaryColor)) {
    moveTo(56f, 29.7243f)
    curveTo(56.0194f, 24.138f, 55.3804f, 18.5582f, 54.0638f, 12.9849f)
    curveTo(52.8376f, 7.79483f, 49.5913f, 4.51451f, 44.3831f, 3.3258f)
    curveTo(33.6052f, 0.863931f, 22.8272f, 0.792478f, 12.0429f, 3.33879f)
    curveTo(6.92497f, 4.54699f, 3.42054f, 7.42458f, 2.11041f, 12.7121f)
    curveTo(0.697015f, 18.3893f, -0.0129079f, 24.06f, -2.15077e-07f, 29.7243f)
    curveTo(-0.0193617f, 35.3041f, 0.619569f, 40.8904f, 1.93615f, 46.4637f)
    curveTo(3.16238f, 51.6537f, 6.40867f, 54.934f, 11.6169f, 56.1227f)
    curveTo(22.3948f, 58.5846f, 33.1728f, 58.6561f, 43.9571f, 56.1098f)
    curveTo(49.075f, 54.9016f, 52.5795f, 52.024f, 53.8896f, 46.7365f)
    curveTo(55.2965f, 41.0657f, 56.0065f, 35.395f, 55.9935f, 29.7308f)
  }
  path(fill = primaryGradient) {
    moveTo(56f, 28.2693f)
    curveTo(56.0194f, 22.6895f, 55.3804f, 17.1032f, 54.0638f, 11.5299f)
    curveTo(52.8376f, 6.33981f, 49.5913f, 3.05949f, 44.3831f, 1.87078f)
    curveTo(33.6052f, -0.591086f, 22.8272f, -0.662539f, 12.0429f, 1.88377f)
    curveTo(6.92497f, 3.09197f, 3.42054f, 5.96956f, 2.11041f, 11.257f)
    curveTo(0.697015f, 16.9343f, -0.0129079f, 22.605f, -2.15077e-07f, 28.2693f)
    curveTo(-0.0193617f, 33.8555f, 0.619569f, 39.4353f, 1.93615f, 45.0086f)
    curveTo(3.16238f, 50.1987f, 6.40867f, 53.479f, 11.6169f, 54.6677f)
    curveTo(22.3948f, 57.1296f, 33.1728f, 57.201f, 43.9571f, 54.6482f)
    curveTo(49.075f, 53.44f, 52.5795f, 50.5625f, 53.8896f, 45.275f)
    curveTo(55.2965f, 39.6042f, 56.0065f, 33.9335f, 55.9935f, 28.2628f)
  }
  path(
    fillAlpha = 0.13f,
    fill = SolidColor(secondaryColor),
  ) {
    moveTo(12.4495f, 41.358f)
    lineTo(30.2234f, 56.4995f)
    curveTo(30.2234f, 56.4995f, 40.1946f, 55.8564f, 44.9317f, 54.3949f)
    curveTo(49.6688f, 52.9333f, 51.9341f, 50.8612f, 53.5605f, 46.4312f)
    curveTo(55.1997f, 41.9621f, 55.0255f, 40.4551f, 55.1997f, 38.8052f)
    curveTo(55.3675f, 37.1553f, 55.8709f, 34.0634f, 55.8709f, 34.0634f)
    lineTo(39.2007f, 21.111f)
    lineTo(15.6054f, 22.0074f)
    lineTo(14.0113f, 23.287f)
    lineTo(12.443f, 41.358f)
    horizontalLineTo(12.4495f)
    close()
  }
  path(fill = SolidColor(backgroundColor)) {
    moveTo(14.0178f, 23.287f)
    curveTo(13.1852f, 23.9561f, 12.4817f, 25.3852f, 12.4688f, 26.4569f)
    lineTo(12.2171f, 40.4811f)
    curveTo(12.1978f, 41.5594f, 12.8883f, 41.9167f, 13.7531f, 41.2866f)
    lineTo(26.1252f, 32.2576f)
  }
  path(fill = SolidColor(backgroundColor)) {
    moveTo(43.5312f, 26.5154f)
    curveTo(43.5183f, 25.632f, 43.0407f, 24.5147f, 42.4082f, 23.7677f)
    curveTo(42.2727f, 23.6053f, 42.1242f, 23.4624f, 41.9758f, 23.3455f)
    lineTo(40.5947f, 22.2347f)
    lineTo(40.5237f, 22.1828f)
    lineTo(38.9941f, 20.9486f)
    lineTo(38.7295f, 20.7407f)
    lineTo(29.5457f, 13.2707f)
    curveTo(28.7132f, 12.5951f, 27.3514f, 12.5887f, 26.5188f, 13.2577f)
    lineTo(15.6118f, 22.0204f)
    lineTo(19.9682f, 25.1708f)
    lineTo(27.9516f, 30.939f)
    horizontalLineTo(27.9581f)
    lineTo(42.2404f, 41.3516f)
    curveTo(43.1052f, 41.9816f, 43.7958f, 41.6179f, 43.7764f, 40.5461f)
    lineTo(43.5247f, 26.5219f)
    lineTo(43.5312f, 26.5154f)
    close()
  }
}.build()
