package com.aptoide.android.aptoidegames.drawables.icons

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

@Preview
@Composable
fun TestCheckBox() {
  Image(
    imageVector = getCheckBox(Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getCheckBox(color: Color): ImageVector = ImageVector.Builder(
  name = "CheckBox",
  defaultWidth = 88.dp,
  defaultHeight = 88.dp,
  viewportWidth = 88f,
  viewportHeight = 88f,
).apply {
  path(
    fill = SolidColor(color),
  ) {
    moveTo(38.8667f, 59.4f)
    lineTo(64.7167f, 33.55f)
    lineTo(59.5833f, 28.4167f)
    lineTo(38.8667f, 49.1333f)
    lineTo(28.4167f, 38.6833f)
    lineTo(23.2833f, 43.8167f)
    lineTo(38.8667f, 59.4f)
    close()
    moveTo(11f, 77f)
    verticalLineTo(11f)
    horizontalLineTo(77f)
    verticalLineTo(77f)
    horizontalLineTo(11f)
    close()
  }
}.build()
