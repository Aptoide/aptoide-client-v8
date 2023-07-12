package cm.aptoide.pt.aptoide_ui.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.theme.green

@Preview
@Composable
fun TestTrustedIcon() {
  Image(
    imageVector = getTrustedIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getTrustedIcon(): ImageVector = ImageVector.Builder(
  name = "TrustedIcon",
  defaultWidth = 16.dp,
  defaultHeight = 20.dp,
  viewportWidth = 16f,
  viewportHeight = 20f,
).apply {
  path(
    fill = SolidColor(green),
  ) {
    moveTo(8f, 0f)
    lineTo(0f, 3f)
    verticalLineTo(9.09f)
    curveTo(0f, 14.14f, 3.41f, 18.85f, 8f, 20f)
    curveTo(12.59f, 18.85f, 16f, 14.14f, 16f, 9.09f)
    verticalLineTo(3f)
    lineTo(8f, 0f)
    close()
    moveTo(6.94f, 13.54f)
    lineTo(3.4f, 10f)
    lineTo(4.81f, 8.59f)
    lineTo(6.93f, 10.71f)
    lineTo(11.17f, 6.47f)
    lineTo(12.58f, 7.88f)
    lineTo(6.94f, 13.54f)
    close()
  }
}.build()
