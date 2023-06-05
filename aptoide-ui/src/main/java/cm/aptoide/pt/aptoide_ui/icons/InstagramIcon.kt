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
import cm.aptoide.pt.theme.grey

@Preview
@Composable
fun TestInstagramIcon() {
  Image(
    imageVector = getInstagramIcon(grey),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getInstagramIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "InstagramIcon",
  defaultWidth = 32.dp,
  defaultHeight = 32.dp,
  viewportWidth = 32f,
  viewportHeight = 32f,
).apply {
  path(fill = SolidColor(color)) {
    moveTo(19.5903f, 8f)
    horizontalLineTo(12.4097f)
    curveTo(11.2402f, 8f, 10.1186f, 8.46481f, 9.29158f, 9.29218f)
    curveTo(8.46459f, 10.1195f, 8f, 11.2417f, 8f, 12.4118f)
    verticalLineTo(19.5882f)
    curveTo(8f, 20.7583f, 8.46459f, 21.8805f, 9.29158f, 22.7078f)
    curveTo(10.1186f, 23.5352f, 11.2402f, 24f, 12.4097f, 24f)
    horizontalLineTo(19.5903f)
    curveTo(20.7598f, 24f, 21.8814f, 23.5352f, 22.7084f, 22.7078f)
    curveTo(23.5354f, 21.8805f, 24f, 20.7583f, 24f, 19.5882f)
    verticalLineTo(12.4118f)
    curveTo(24f, 11.2417f, 23.5354f, 10.1195f, 22.7084f, 9.29218f)
    curveTo(21.8814f, 8.46481f, 20.7598f, 8f, 19.5903f, 8f)
    close()
    moveTo(16.0037f, 20.1397f)
    curveTo(15.1861f, 20.1412f, 14.3865f, 19.8999f, 13.7061f, 19.4463f)
    curveTo(13.0258f, 18.9928f, 12.4952f, 18.3475f, 12.1817f, 17.5921f)
    curveTo(11.8681f, 16.8367f, 11.7857f, 16.0053f, 11.9449f, 15.203f)
    curveTo(12.104f, 14.4007f, 12.4976f, 13.6637f, 13.0757f, 13.0853f)
    curveTo(13.6538f, 12.507f, 14.3904f, 12.1133f, 15.1923f, 11.9541f)
    curveTo(15.9942f, 11.7948f, 16.8253f, 11.8773f, 17.5804f, 12.191f)
    curveTo(18.3354f, 12.5046f, 18.9804f, 13.0354f, 19.4337f, 13.7161f)
    curveTo(19.887f, 14.3968f, 20.1282f, 15.1968f, 20.1268f, 16.0147f)
    curveTo(20.1151f, 17.1081f, 19.6708f, 18.1523f, 18.8911f, 18.9186f)
    curveTo(18.1114f, 19.6849f, 17.0599f, 20.1108f, 15.9669f, 20.1029f)
    lineTo(16.0037f, 20.1397f)
    close()
    moveTo(21.0014f, 12.4118f)
    curveTo(20.8315f, 12.5837f, 20.6073f, 12.6914f, 20.3669f, 12.7164f)
    curveTo(20.1265f, 12.7413f, 19.885f, 12.6821f, 19.6834f, 12.5487f)
    curveTo(19.4818f, 12.4154f, 19.3328f, 12.2162f, 19.2617f, 11.9851f)
    curveTo(19.1905f, 11.754f, 19.2018f, 11.5054f, 19.2934f, 11.2817f)
    curveTo(19.3851f, 11.058f, 19.5515f, 10.8731f, 19.7643f, 10.7585f)
    curveTo(19.9771f, 10.6439f, 20.223f, 10.6067f, 20.4602f, 10.6533f)
    curveTo(20.6973f, 10.6999f, 20.9109f, 10.8273f, 21.0646f, 11.0139f)
    curveTo(21.2182f, 11.2005f, 21.3024f, 11.4347f, 21.3027f, 11.6765f)
    curveTo(21.3031f, 11.9517f, 21.1948f, 12.216f, 21.0014f, 12.4118f)
    close()
  }
  path(fill = SolidColor(color)) {
    moveTo(15.9669f, 13.2721f)
    curveTo(15.432f, 13.2721f, 14.9091f, 13.4308f, 14.4643f, 13.7281f)
    curveTo(14.0195f, 14.0254f, 13.6729f, 14.448f, 13.4682f, 14.9425f)
    curveTo(13.2635f, 15.4369f, 13.2099f, 15.981f, 13.3143f, 16.5058f)
    curveTo(13.4186f, 17.0307f, 13.6762f, 17.5129f, 14.0544f, 17.8913f)
    curveTo(14.4327f, 18.2697f, 14.9146f, 18.5274f, 15.4393f, 18.6318f)
    curveTo(15.9639f, 18.7362f, 16.5077f, 18.6827f, 17.0019f, 18.4779f)
    curveTo(17.4962f, 18.2731f, 17.9186f, 17.9262f, 18.2158f, 17.4813f)
    curveTo(18.513f, 17.0363f, 18.6716f, 16.5131f, 18.6716f, 15.978f)
    curveTo(18.6696f, 15.2609f, 18.3841f, 14.5738f, 17.8773f, 14.0667f)
    curveTo(17.3705f, 13.5597f, 16.6836f, 13.274f, 15.9669f, 13.2721f)
    close()
  }
}.build()
