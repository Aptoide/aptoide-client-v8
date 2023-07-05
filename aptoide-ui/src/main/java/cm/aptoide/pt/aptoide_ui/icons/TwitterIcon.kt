package cm.aptoide.pt.aptoide_ui.icons

import androidx.compose.ui.graphics.*
import cm.aptoide.pt.theme.greyMedium
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.theme.shapes

@Preview
@Composable
fun TestTwitterIcon() {
  Image(
    imageVector = getTwitterIcon(greyMedium),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getTwitterIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "TwitterIcon",
  defaultWidth = 32.dp,
  defaultHeight = 32.dp,
  viewportWidth = 32f,
  viewportHeight = 32f,
).apply {
  path(fill = SolidColor(color)) {
    moveTo(24f, 11.4401f)
    curveTo(23.4395f, 11.6933f, 22.8431f, 11.858f, 22.232f, 11.9281f)
    curveTo(22.8749f, 11.5434f, 23.3556f, 10.9376f, 23.584f, 10.2241f)
    curveTo(22.9827f, 10.586f, 22.3217f, 10.8379f, 21.632f, 10.9681f)
    curveTo(21.1657f, 10.4778f, 20.5514f, 10.1542f, 19.8833f, 10.0471f)
    curveTo(19.2153f, 9.94001f, 18.5305f, 10.0553f, 17.9344f, 10.3752f)
    curveTo(17.3382f, 10.6951f, 16.8636f, 11.2019f, 16.5835f, 11.8178f)
    curveTo(16.3034f, 12.4337f, 16.2333f, 13.1245f, 16.384f, 13.7841f)
    curveTo(15.1609f, 13.722f, 13.9641f, 13.4054f, 12.8703f, 12.8544f)
    curveTo(11.7765f, 12.3034f, 10.8098f, 11.5301f, 10.032f, 10.5841f)
    curveTo(9.63807f, 11.2628f, 9.51806f, 12.0662f, 9.69647f, 12.8305f)
    curveTo(9.87489f, 13.5947f, 10.3383f, 14.2619f, 10.992f, 14.6961f)
    curveTo(10.5013f, 14.6824f, 10.0211f, 14.5506f, 9.592f, 14.3121f)
    verticalLineTo(14.3521f)
    curveTo(9.59127f, 15.0632f, 9.8373f, 15.7526f, 10.2881f, 16.3026f)
    curveTo(10.7389f, 16.8526f, 11.3666f, 17.2292f, 12.064f, 17.3681f)
    curveTo(11.6107f, 17.4945f, 11.1341f, 17.5137f, 10.672f, 17.4241f)
    curveTo(10.869f, 18.0362f, 11.2527f, 18.5713f, 11.7692f, 18.9544f)
    curveTo(12.2856f, 19.3374f, 12.9091f, 19.5492f, 13.552f, 19.5601f)
    curveTo(12.4611f, 20.4158f, 11.1145f, 20.8806f, 9.72802f, 20.8801f)
    curveTo(9.48002f, 20.8801f, 9.24f, 20.8801f, 8.992f, 20.8801f)
    curveTo(10.3163f, 21.7291f, 11.8447f, 22.2058f, 13.4169f, 22.2603f)
    curveTo(14.989f, 22.3147f, 16.5467f, 21.9448f, 17.9266f, 21.1894f)
    curveTo(19.3064f, 20.4341f, 20.4574f, 19.3211f, 21.2586f, 17.9673f)
    curveTo(22.0598f, 16.6136f, 22.4817f, 15.0691f, 22.48f, 13.4961f)
    curveTo(22.48f, 13.3681f, 22.48f, 13.2321f, 22.48f, 13.0961f)
    curveTo(23.0885f, 12.662f, 23.6119f, 12.1196f, 24.024f, 11.4961f)
    lineTo(24f, 11.4401f)
    close()
  }
}.build()
