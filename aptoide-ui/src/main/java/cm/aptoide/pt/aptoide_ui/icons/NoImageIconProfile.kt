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
import cm.aptoide.pt.aptoide_ui.theme.greyLight
import cm.aptoide.pt.aptoide_ui.theme.greyMedium

@Preview
@Composable
fun TestNoImageIconProfile() {
  Image(
    imageVector = getNoImageIconProfile(greyMedium, greyLight),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getNoImageIconProfile(primaryColor: Color, secondaryColor: Color): ImageVector =
  ImageVector.Builder(
    name = "NoImageIconProfile",
    defaultWidth = 80.dp,
    defaultHeight = 80.dp,
    viewportWidth = 80f,
    viewportHeight = 80f,
  ).apply {
    path(fill = SolidColor(secondaryColor)) {
      horizontalLineTo(80f)
      verticalLineTo(80f)
      horizontalLineTo(0f)
      verticalLineTo(0f)
      close()
    }
    path(fill = SolidColor(primaryColor)) {
      moveTo(40f, 40.579f)
      curveTo(47.1797f, 40.579f, 53f, 34.7146f, 53f, 27.4804f)
      curveTo(53f, 20.2462f, 47.1797f, 14.3818f, 40f, 14.3818f)
      curveTo(32.8203f, 14.3818f, 27f, 20.2462f, 27f, 27.4804f)
      curveTo(27f, 34.7146f, 32.8203f, 40.579f, 40f, 40.579f)
      close()
    }
    path(fill = SolidColor(primaryColor)) {
      moveTo(40f, 73f)
      curveTo(34.8514f, 73f, 29.7828f, 71.7162f, 25.2462f, 69.2631f)
      curveTo(20.7096f, 66.81f, 16.846f, 63.2639f, 14f, 58.9408f)
      curveTo(14f, 50.2084f, 31.3333f, 45.493f, 40f, 45.493f)
      curveTo(48.6667f, 45.493f, 66f, 50.2521f, 66f, 58.9408f)
      curveTo(63.154f, 63.2639f, 59.2904f, 66.81f, 54.7538f, 69.2631f)
      curveTo(50.2172f, 71.7162f, 45.1486f, 73f, 40f, 73f)
      close()
    }
  }.build()
