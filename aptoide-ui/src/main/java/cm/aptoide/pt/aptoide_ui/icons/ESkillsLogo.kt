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
import cm.aptoide.pt.theme.yellow

@Preview
@Composable
fun TestESkillsLogo() {
  Image(
    imageVector = getESkillsLogo(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getESkillsLogo(): ImageVector = ImageVector.Builder(
  name = "ESkillsLogo",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(fill = SolidColor(yellow)) {
    moveTo(12f, 12f)
    moveToRelative(-12f, 0f)
    arcToRelative(
      a = 12f,
      b = 12f,
      theta = 0f,
      isMoreThanHalf = true,
      isPositiveArc = true,
      dx1 = 24f,
      dy1 = 0f
    )
    arcToRelative(
      a = 12f,
      b = 12f,
      theta = 0f,
      isMoreThanHalf = true,
      isPositiveArc = true,
      dx1 = -24f,
      dy1 = 0f
    )
  }
  path(fill = SolidColor(Color.White)) {
    moveTo(10.588f, 13.05f)
    lineTo(12f, 8.447f)
    lineTo(13.525f, 13.05f)
    horizontalLineTo(10.588f)
    close()
    moveTo(15.614f, 12.513f)
    horizontalLineTo(16.362f)
    curveTo(16.492f, 12.506f, 16.614f, 12.449f, 16.704f, 12.355f)
    curveTo(16.793f, 12.26f, 16.843f, 12.135f, 16.842f, 12.005f)
    curveTo(16.842f, 11.935f, 16.828f, 11.866f, 16.801f, 11.802f)
    curveTo(16.774f, 11.738f, 16.734f, 11.679f, 16.684f, 11.631f)
    curveTo(16.634f, 11.582f, 16.575f, 11.544f, 16.51f, 11.518f)
    curveTo(16.445f, 11.493f, 16.376f, 11.481f, 16.306f, 11.483f)
    horizontalLineTo(15.205f)
    lineTo(15.007f, 10.946f)
    horizontalLineTo(16.235f)
    curveTo(16.361f, 10.926f, 16.475f, 10.861f, 16.557f, 10.764f)
    curveTo(16.64f, 10.667f, 16.685f, 10.544f, 16.685f, 10.417f)
    curveTo(16.685f, 10.29f, 16.64f, 10.167f, 16.557f, 10.069f)
    curveTo(16.475f, 9.973f, 16.361f, 9.908f, 16.235f, 9.887f)
    horizontalLineTo(14.626f)
    lineTo(13.708f, 7.304f)
    curveTo(13.58f, 6.936f, 13.356f, 6.61f, 13.059f, 6.358f)
    curveTo(12.916f, 6.23f, 12.748f, 6.133f, 12.566f, 6.072f)
    curveTo(12.384f, 6.012f, 12.191f, 5.989f, 12f, 6.005f)
    curveTo(11.635f, 6.004f, 11.281f, 6.128f, 10.998f, 6.358f)
    curveTo(10.708f, 6.617f, 10.486f, 6.941f, 10.348f, 7.304f)
    lineTo(9.402f, 9.887f)
    horizontalLineTo(7.765f)
    curveTo(7.697f, 9.887f, 7.63f, 9.901f, 7.567f, 9.927f)
    curveTo(7.504f, 9.954f, 7.448f, 9.993f, 7.4f, 10.041f)
    curveTo(7.353f, 10.09f, 7.316f, 10.148f, 7.291f, 10.211f)
    curveTo(7.266f, 10.274f, 7.255f, 10.342f, 7.256f, 10.41f)
    curveTo(7.26f, 10.545f, 7.314f, 10.674f, 7.408f, 10.771f)
    curveTo(7.503f, 10.867f, 7.63f, 10.925f, 7.765f, 10.932f)
    horizontalLineTo(9.035f)
    lineTo(8.838f, 11.455f)
    horizontalLineTo(7.765f)
    curveTo(7.689f, 11.443f, 7.611f, 11.45f, 7.538f, 11.473f)
    curveTo(7.464f, 11.497f, 7.397f, 11.536f, 7.342f, 11.589f)
    curveTo(7.286f, 11.642f, 7.243f, 11.707f, 7.216f, 11.779f)
    curveTo(7.189f, 11.851f, 7.179f, 11.929f, 7.186f, 12.005f)
    curveTo(7.189f, 12.076f, 7.207f, 12.146f, 7.239f, 12.21f)
    curveTo(7.27f, 12.274f, 7.314f, 12.331f, 7.369f, 12.377f)
    curveTo(7.423f, 12.424f, 7.486f, 12.458f, 7.554f, 12.479f)
    curveTo(7.622f, 12.5f, 7.694f, 12.507f, 7.765f, 12.499f)
    horizontalLineTo(8.513f)
    lineTo(7.186f, 15.958f)
    curveTo(7.112f, 16.152f, 7.069f, 16.357f, 7.059f, 16.565f)
    curveTo(7.07f, 16.86f, 7.197f, 17.139f, 7.412f, 17.341f)
    curveTo(7.635f, 17.546f, 7.928f, 17.657f, 8.231f, 17.652f)
    curveTo(8.487f, 17.66f, 8.739f, 17.581f, 8.944f, 17.427f)
    curveTo(9.149f, 17.273f, 9.296f, 17.053f, 9.36f, 16.805f)
    lineTo(9.84f, 15.393f)
    horizontalLineTo(14.259f)
    lineTo(14.739f, 16.89f)
    curveTo(14.809f, 17.134f, 14.961f, 17.347f, 15.169f, 17.494f)
    curveTo(15.377f, 17.64f, 15.629f, 17.711f, 15.882f, 17.694f)
    curveTo(16.071f, 17.698f, 16.257f, 17.649f, 16.419f, 17.553f)
    curveTo(16.563f, 17.444f, 16.691f, 17.316f, 16.8f, 17.172f)
    curveTo(16.901f, 17.002f, 16.95f, 16.805f, 16.941f, 16.607f)
    curveTo(16.912f, 16.406f, 16.865f, 16.207f, 16.8f, 16.014f)
    lineTo(15.586f, 12.57f)
    lineTo(15.614f, 12.513f)
    close()
  }
  path(fill = SolidColor(Color.White)) {
    moveTo(12f, 2.118f)
    curveTo(10.045f, 2.118f, 8.135f, 2.697f, 6.51f, 3.783f)
    curveTo(4.885f, 4.869f, 3.618f, 6.413f, 2.87f, 8.218f)
    curveTo(2.122f, 10.024f, 1.926f, 12.011f, 2.308f, 13.928f)
    curveTo(2.689f, 15.845f, 3.63f, 17.606f, 5.012f, 18.988f)
    curveTo(6.394f, 20.37f, 8.155f, 21.311f, 10.072f, 21.693f)
    curveTo(11.989f, 22.074f, 13.976f, 21.878f, 15.782f, 21.13f)
    curveTo(17.588f, 20.382f, 19.131f, 19.116f, 20.217f, 17.49f)
    curveTo(21.303f, 15.865f, 21.882f, 13.955f, 21.882f, 12f)
    curveTo(21.882f, 9.379f, 20.841f, 6.866f, 18.988f, 5.012f)
    curveTo(17.135f, 3.159f, 14.621f, 2.118f, 12f, 2.118f)
    close()
    moveTo(12f, 22.588f)
    curveTo(9.906f, 22.588f, 7.859f, 21.967f, 6.117f, 20.804f)
    curveTo(4.376f, 19.64f, 3.019f, 17.987f, 2.218f, 16.052f)
    curveTo(1.416f, 14.117f, 1.207f, 11.988f, 1.615f, 9.934f)
    curveTo(2.024f, 7.881f, 3.032f, 5.994f, 4.513f, 4.513f)
    curveTo(5.994f, 3.032f, 7.88f, 2.024f, 9.934f, 1.615f)
    curveTo(11.988f, 1.207f, 14.117f, 1.416f, 16.052f, 2.218f)
    curveTo(17.987f, 3.019f, 19.64f, 4.376f, 20.804f, 6.118f)
    curveTo(21.967f, 7.859f, 22.588f, 9.906f, 22.588f, 12f)
    curveTo(22.588f, 14.808f, 21.473f, 17.501f, 19.487f, 19.487f)
    curveTo(17.501f, 21.473f, 14.808f, 22.588f, 12f, 22.588f)
    close()
  }
}.build()
