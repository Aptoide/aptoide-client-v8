package cm.aptoide.pt.aptoide_ui.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestBonusBackground() {
  Image(
    imageVector = getBonusBackground(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getBonusBackground(): ImageVector = ImageVector.Builder(
  name = "Rectangle_2180",
  defaultWidth = 64.dp,
  defaultHeight = 64.dp,
  viewportWidth = 64f,
  viewportHeight = 64f,
).apply {
  path(
    fill = SolidColor(Color.White),
  ) {
    moveTo(64f, 48f)
    curveTo(64f, 56.8366f, 56.8366f, 64f, 48f, 64f)
    lineTo(0f, 64f)
    verticalLineTo(16f)
    curveTo(0f, 7.16344f, 7.16344f, 0f, 16f, 0f)
    lineTo(48f, 0f)
    curveTo(56.8366f, 0f, 64f, 7.16344f, 64f, 16f)
    lineTo(64f, 48f)
    close()
  }
  path(
    pathFillType = PathFillType.EvenOdd,
    fill = SolidColor(Color(0xFFFE7073)),
  ) {
    moveTo(52f, 18f)
    horizontalLineTo(54f)
    verticalLineTo(21f)
    horizontalLineTo(52f)
    verticalLineTo(18f)
    close()
    moveTo(54f, 18f)
    horizontalLineTo(58f)
    verticalLineTo(20f)
    horizontalLineTo(54f)
    verticalLineTo(18f)
    close()
    moveTo(7f, 3f)
    horizontalLineTo(9f)
    verticalLineTo(4f)
    horizontalLineTo(7f)
    verticalLineTo(3f)
    close()
  }
  path(
    pathFillType = PathFillType.EvenOdd,
    fill = SolidColor(Color(0xFF007AFF)),
  ) {
    moveTo(6f, 42f)
    horizontalLineTo(9f)
    verticalLineTo(44f)
    horizontalLineTo(6f)
    verticalLineTo(42f)
    close()
    moveTo(54f, 57f)
    horizontalLineTo(57f)
    verticalLineTo(59f)
    horizontalLineTo(54f)
    verticalLineTo(57f)
    close()
  }
}.build()
