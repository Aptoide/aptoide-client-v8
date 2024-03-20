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
import cm.aptoide.pt.aptoide_ui.theme.aptoideTVIconOrange
import cm.aptoide.pt.aptoide_ui.theme.greyLight
import cm.aptoide.pt.aptoide_ui.theme.iconsBlack

@Preview
@Composable
fun TestAptoideTVIcon() {
  Image(
    imageVector = getAptoideTVIcon(aptoideTVIconOrange, iconsBlack, greyLight),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getAptoideTVIcon(
  primaryColor: Color,
  secondaryColor: Color,
  backgroundColor: Color
): ImageVector = ImageVector.Builder(
  name = "AptoideTVIcon",
  defaultWidth = 55.dp,
  defaultHeight = 55.dp,
  viewportWidth = 55f,
  viewportHeight = 55f,
).apply {
  path(
    fill = SolidColor(primaryColor),
  ) {
    moveTo(53.2202f, 27.5f)
    curveTo(53.2339f, 22.0943f, 52.62f, 16.7061f, 51.3916f, 11.4494f)
    curveTo(50.2353f, 6.47585f, 47.1692f, 3.32828f, 42.2546f, 2.18821f)
    curveTo(32.0853f, -0.174433f, 21.916f, -0.242005f, 11.7421f, 2.20314f)
    curveTo(6.91174f, 3.36285f, 3.60366f, 6.12228f, 2.36772f, 11.1917f)
    curveTo(1.04143f, 16.632f, 0.369094f, 22.0699f, 0.382877f, 27.5f)
    curveTo(0.368462f, 32.9052f, 0.982341f, 38.2929f, 2.21151f, 43.549f)
    curveTo(3.36704f, 48.5249f, 6.4339f, 51.6709f, 11.347f, 52.8126f)
    curveTo(21.5171f, 55.1736f, 31.6871f, 55.242f, 41.861f, 52.7968f)
    curveTo(46.6906f, 51.6356f, 50.0002f, 48.8761f, 51.2354f, 43.8083f)
    curveTo(52.5624f, 38.3664f, 53.234f, 32.9316f, 53.2202f, 27.5f)
    close()
  }
  path(
    fill = SolidColor(secondaryColor),
    fillAlpha = 0.32f,
  ) {
    moveTo(53.2202f, 27.5f)
    curveTo(53.2339f, 22.0943f, 52.62f, 16.7061f, 51.3916f, 11.4494f)
    curveTo(50.2353f, 6.47585f, 47.1692f, 3.32828f, 42.2546f, 2.18821f)
    curveTo(32.0853f, -0.174433f, 21.916f, -0.242005f, 11.7421f, 2.20314f)
    curveTo(6.91174f, 3.36285f, 3.60366f, 6.12228f, 2.36772f, 11.1917f)
    curveTo(1.04143f, 16.632f, 0.369094f, 22.0699f, 0.382877f, 27.5f)
    curveTo(0.368462f, 32.9052f, 0.982341f, 38.2929f, 2.21151f, 43.549f)
    curveTo(3.36704f, 48.5249f, 6.4339f, 51.6709f, 11.347f, 52.8126f)
    curveTo(21.5171f, 55.1736f, 31.6871f, 55.242f, 41.861f, 52.7968f)
    curveTo(46.6906f, 51.6356f, 50.0002f, 48.8761f, 51.2354f, 43.8083f)
    curveTo(52.5624f, 38.3664f, 53.234f, 32.9316f, 53.2202f, 27.5f)
    close()
  }
  path(
    fill = SolidColor(primaryColor),
  ) {
    moveTo(51.3916f, 11.0888f)
    curveTo(50.2353f, 6.27549f, 47.1692f, 3.23321f, 42.2546f, 2.12928f)
    curveTo(32.0853f, -0.155578f, 21.916f, -0.221577f, 11.7421f, 2.14421f)
    curveTo(6.91174f, 3.26621f, 3.60366f, 5.93606f, 2.36773f, 10.8405f)
    curveTo(1.04143f, 16.1016f, 0.369097f, 21.3612f, 0.382881f, 26.6153f)
    curveTo(0.366034f, 31.7947f, 0.969452f, 36.9718f, 2.21151f, 42.1426f)
    curveTo(3.36704f, 46.9566f, 6.43391f, 49.9997f, 11.347f, 51.1028f)
    curveTo(21.5171f, 53.3877f, 31.6871f, 53.4529f, 41.861f, 51.0879f)
    curveTo(46.6906f, 49.9644f, 50.0002f, 47.2953f, 51.2354f, 42.3924f)
    curveTo(52.5625f, 37.1297f, 53.234f, 31.8701f, 53.2202f, 26.6153f)
    curveTo(53.2334f, 21.3824f, 52.6193f, 16.1678f, 51.3916f, 11.0888f)
    close()
  }
  path(
    fill = SolidColor(secondaryColor),
    fillAlpha = 0.097f,
  ) {
    moveTo(51.4353f, 41.58f)
    curveTo(51.3702f, 41.8511f, 51.3036f, 42.1221f, 51.2362f, 42.3924f)
    curveTo(50.0002f, 47.2953f, 46.6906f, 49.9644f, 41.861f, 51.0879f)
    curveTo(36.4766f, 52.3456f, 30.9636f, 52.9297f, 25.4424f, 52.8275f)
    lineTo(14.7409f, 39.875f)
    lineTo(31.109f, 17.1875f)
    lineTo(51.4353f, 41.5808f)
    verticalLineTo(41.58f)
    close()
  }
  path(
    fill = SolidColor(backgroundColor),
  ) {
    moveTo(15.6751f, 40.4643f)
    curveTo(14.7761f, 40.48f, 14.4729f, 39.8994f, 15.0012f, 39.1757f)
    lineTo(30.9137f, 16.8929f)
    lineTo(38.4358f, 25.938f)
    curveTo(39.0009f, 26.6349f, 39.0047f, 27.7766f, 38.4449f, 28.4759f)
    lineTo(30.0522f, 38.9534f)
    curveTo(29.4925f, 39.6534f, 28.2986f, 40.2388f, 27.4004f, 40.2537f)
    lineTo(15.6743f, 40.4643f)
    horizontalLineTo(15.6751f)
    close()
    moveTo(15.6383f, 12.7679f)
    lineTo(26.5037f, 12.9761f)
    curveTo(27.5191f, 12.9761f, 28.2466f, 13.5198f, 28.8515f, 14.0447f)
    curveTo(28.9388f, 14.1217f, 29.213f, 14.443f, 29.6732f, 15.0095f)
    lineTo(30.2475f, 15.7143f)
    lineTo(23.0685f, 25.7322f)
    lineTo(22.6366f, 25.1429f)
    lineTo(14.9966f, 14.3967f)
    curveTo(14.4836f, 13.6785f, 14.7646f, 12.7529f, 15.6383f, 12.7679f)
    close()
  }
}.build()
