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
fun TestLeftArrow() {
  Image(
    imageVector = getLeftArrow(Color.Black, Color.Red),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getLeftArrow(background: Color, arrow: Color): ImageVector = ImageVector.Builder(
  name = "LeftArrow",
  defaultWidth = 32.dp,
  defaultHeight = 32.dp,
  viewportWidth = 32f,
  viewportHeight = 32f,
).apply {
  path(
    fill = SolidColor(background),
  ) {
    moveTo(16f, 0f)
    arcTo(16f, 16f, 0f, false, true, 32f, 16f)
    arcTo(16f, 16f, 0f, false, true, 16f, 32f)
    arcTo(16f, 16f, 0f, false, true, 0f, 16f)
    arcTo(16f, 16f, 0f, false, true, 16f, 0f)
    close()
  }
  path(
    fill = SolidColor(arrow),
  ) {
    moveTo(24f, 14.997f)
    horizontalLineTo(11.83f)
    lineTo(17.416f, 9.411f)
    lineTo(15.995f, 8f)
    lineTo(8f, 15.995f)
    lineTo(15.995f, 24f)
    lineTo(17.406f, 22.589f)
    lineTo(11.83f, 17.003f)
    horizontalLineTo(24f)
    verticalLineTo(14.997f)
    close()
  }
}.build()
