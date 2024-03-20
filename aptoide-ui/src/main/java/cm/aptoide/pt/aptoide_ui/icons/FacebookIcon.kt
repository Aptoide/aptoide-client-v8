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
import cm.aptoide.pt.aptoide_ui.theme.greyMedium

@Preview
@Composable
fun TestFacebookIcon() {
  Image(
    imageVector = getFacebookIcon(greyMedium),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getFacebookIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "FacebookIcon",
  defaultWidth = 32.dp,
  defaultHeight = 32.dp,
  viewportWidth = 32f,
  viewportHeight = 32f,
).apply {
  path(fill = SolidColor(color)) {
    moveTo(17.112f, 23.144f)
    verticalLineTo(16.232f)
    horizontalLineTo(19.432f)
    lineTo(19.776f, 13.536f)
    horizontalLineTo(17.112f)
    verticalLineTo(11.816f)
    curveTo(17.112f, 11.016f, 17.328f, 10.504f, 18.448f, 10.504f)
    horizontalLineTo(19.872f)
    verticalLineTo(8.10404f)
    curveTo(19.1809f, 8.03328f, 18.4867f, 7.99857f, 17.792f, 8.00005f)
    curveTo(15.736f, 8.00005f, 14.328f, 9.25605f, 14.328f, 11.56f)
    verticalLineTo(13.552f)
    horizontalLineTo(12f)
    verticalLineTo(16.248f)
    horizontalLineTo(14.328f)
    verticalLineTo(23.16f)
    lineTo(17.112f, 23.144f)
    close()
  }
}.build()
