package com.aptoide.android.aptoidegames.drawables.icons.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun TestCorrectHexagon() {
  Image(
    imageVector = getCorrectHexagon(),
    contentDescription = null,
  )
}

fun getCorrectHexagon(): ImageVector = ImageVector.Builder(
  name = "CorrectHexagon",
  defaultWidth = 53.dp,
  defaultHeight = 58.dp,
  viewportWidth = 53f,
  viewportHeight = 58f
).apply {
  path(fill = SolidColor(Color(0xFFCA8BFF))) {
    moveTo(0f, 16.111f)
    lineTo(26.5f, 0f)
    lineTo(53f, 16.111f)
    verticalLineTo(41.889f)
    lineTo(26.5f, 58f)
    lineTo(0f, 41.889f)
    verticalLineTo(16.111f)
    close()
  }
  path(fill = SolidColor(Color(0xFF913DD8))) {
    moveTo(5f, 18.333f)
    lineTo(26.391f, 5f)
    lineTo(47.783f, 18.333f)
    verticalLineTo(39.667f)
    lineTo(26.391f, 53f)
    lineTo(5f, 39.667f)
    verticalLineTo(18.333f)
    close()
  }
  path(fill = SolidColor(Color.White)) {
    moveTo(37.347f, 23.35f)
    lineTo(23.651f, 37.348f)
    lineTo(15.434f, 28.949f)
    lineTo(18.073f, 26.251f)
    lineTo(23.651f, 31.952f)
    lineTo(34.707f, 20.652f)
    lineTo(37.347f, 23.35f)
    close()
  }
}.build()
