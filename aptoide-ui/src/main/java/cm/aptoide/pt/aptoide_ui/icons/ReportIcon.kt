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
import cm.aptoide.pt.theme.error

@Preview
@Composable
fun TestReportIcon() {
  Image(
    imageVector = getReportIcon(error),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getReportIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "ReportIcon",
  defaultWidth = 16.dp,
  defaultHeight = 16.dp,
  viewportHeight = 16f,
  viewportWidth = 16f,
).apply {
  path(
    fill = SolidColor(color),
  ) {
    moveTo(8f, 3.993f)
    lineTo(13.02f, 12.667f)
    horizontalLineTo(2.98f)
    lineTo(8f, 3.993f)
    close()
    moveTo(8f, 1.333f)
    lineTo(0.667f, 14f)
    horizontalLineTo(15.334f)
    lineTo(8f, 1.333f)
    close()
    moveTo(8.667f, 10.667f)
    horizontalLineTo(7.334f)
    verticalLineTo(12f)
    horizontalLineTo(8.667f)
    verticalLineTo(10.667f)
    close()
    moveTo(8.667f, 6.667f)
    horizontalLineTo(7.334f)
    verticalLineTo(9.333f)
    horizontalLineTo(8.667f)
    verticalLineTo(6.667f)
    close()
  }
}.build()
