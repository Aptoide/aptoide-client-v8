import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.theme.blueGradientEnd
import cm.aptoide.pt.aptoide_ui.theme.blueGradientStart
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestNoConnectionSimple() {
  Image(
    imageVector = getNoConnectionSimple(),
    contentDescription = null,
    modifier = Modifier.size(64.dp)
  )
}

fun getNoConnectionSimple(): ImageVector = ImageVector.Builder(
  name = "NoConnectionSimple",
  defaultWidth = 64.dp,
  defaultHeight = 64.dp,
  viewportWidth = 64f,
  viewportHeight = 64f,
).apply {
  path(
    fill = Brush.linearGradient(
      colors = listOf(blueGradientStart, blueGradientEnd),
      start = Offset(x = 5.444f, y = 6.326f),
      end = Offset(x = 69.915f, y = 38.658f)
    ),
  ) {
    moveTo(32f, 64f)
    curveTo(49.673f, 64f, 64f, 49.673f, 64f, 32f)
    curveTo(64f, 14.327f, 49.673f, 0f, 32f, 0f)
    curveTo(14.327f, 0f, 0f, 14.327f, 0f, 32f)
    curveTo(0f, 49.673f, 14.327f, 64f, 32f, 64f)
    close()
  }
  path(
    fill = SolidColor(Palette.White),
  ) {
    moveTo(46.769f, 30.371f)
    curveTo(44.691f, 28.388f, 42.418f, 26.806f, 39.951f, 25.625f)
    curveTo(37.484f, 24.445f, 34.833f, 23.854f, 32f, 23.854f)
    curveTo(31.126f, 23.854f, 30.288f, 23.907f, 29.485f, 24.014f)
    curveTo(28.683f, 24.12f, 28.01f, 24.267f, 27.467f, 24.456f)
    lineTo(24.881f, 21.871f)
    curveTo(25.92f, 21.493f, 27.048f, 21.198f, 28.264f, 20.986f)
    curveTo(29.479f, 20.773f, 30.725f, 20.667f, 32f, 20.667f)
    curveTo(35.306f, 20.667f, 38.416f, 21.351f, 41.332f, 22.721f)
    curveTo(44.248f, 24.09f, 46.804f, 25.897f, 49f, 28.14f)
    lineTo(46.769f, 30.371f)
    close()
    moveTo(40.783f, 36.356f)
    curveTo(40.004f, 35.601f, 39.296f, 34.993f, 38.658f, 34.532f)
    curveTo(38.021f, 34.072f, 37.206f, 33.618f, 36.215f, 33.169f)
    lineTo(32.213f, 29.167f)
    curveTo(34.456f, 29.214f, 36.433f, 29.674f, 38.145f, 30.548f)
    curveTo(39.857f, 31.422f, 41.48f, 32.614f, 43.015f, 34.125f)
    lineTo(40.783f, 36.356f)
    close()
    moveTo(43.51f, 46.556f)
    lineTo(29.556f, 32.602f)
    curveTo(28.281f, 32.909f, 27.107f, 33.405f, 26.032f, 34.09f)
    curveTo(24.958f, 34.774f, 24.019f, 35.53f, 23.217f, 36.356f)
    lineTo(20.985f, 34.125f)
    curveTo(21.859f, 33.251f, 22.762f, 32.484f, 23.695f, 31.823f)
    curveTo(24.627f, 31.162f, 25.743f, 30.572f, 27.042f, 30.052f)
    lineTo(23.11f, 26.121f)
    curveTo(22.001f, 26.664f, 20.95f, 27.307f, 19.958f, 28.051f)
    curveTo(18.967f, 28.795f, 18.058f, 29.568f, 17.231f, 30.371f)
    lineTo(15f, 28.14f)
    curveTo(15.85f, 27.266f, 16.759f, 26.451f, 17.727f, 25.696f)
    curveTo(18.695f, 24.94f, 19.687f, 24.291f, 20.702f, 23.748f)
    lineTo(17.444f, 20.49f)
    lineTo(18.967f, 18.967f)
    lineTo(45.033f, 45.034f)
    lineTo(43.51f, 46.556f)
    close()
    moveTo(32f, 45.14f)
    lineTo(26.758f, 39.863f)
    curveTo(27.443f, 39.178f, 28.228f, 38.641f, 29.114f, 38.251f)
    curveTo(29.999f, 37.862f, 30.961f, 37.667f, 32f, 37.667f)
    curveTo(33.039f, 37.667f, 34.001f, 37.862f, 34.887f, 38.251f)
    curveTo(35.772f, 38.641f, 36.557f, 39.178f, 37.242f, 39.863f)
    lineTo(32f, 45.14f)
    close()
  }
}.build()
