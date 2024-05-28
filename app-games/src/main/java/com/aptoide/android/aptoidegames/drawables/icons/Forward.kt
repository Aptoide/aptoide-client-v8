package com.aptoide.android.aptoidegames.drawables.icons

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
fun TestForward() {
  Image(
    imageVector = getForward(Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getForward(iconColor: Color): ImageVector = ImageVector.Builder(
  name = "Forward",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    pathFillType = PathFillType.EvenOdd,
    fill = SolidColor(iconColor),
  ) {
    moveTo(21f, 3f)
    lineTo(3f, 3f)
    lineTo(3f, 21f)
    lineTo(21f, 21f)
    lineTo(21f, 3f)
    close()
    moveTo(14.6474f, 12.3526f)
    lineTo(11f, 8.70526f)
    lineTo(11f, 16f)
    lineTo(14.6474f, 12.3526f)
    close()
  }
}.build()
