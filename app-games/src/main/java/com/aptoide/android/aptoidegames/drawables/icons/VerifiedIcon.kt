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
fun TestVerifiedIcon() {
  Image(
    imageVector = getVerifiedIcon(Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getVerifiedIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "VerifiedIcon",
  defaultWidth = 16.dp,
  defaultHeight = 16.dp,
  viewportWidth = 16f,
  viewportHeight = 16f,
).apply {
  path(
    fill = SolidColor(color),
  ) {
    moveTo(5.73366f, 15f)
    lineTo(4.46699f, 12.8667f)
    lineTo(2.06699f, 12.3333f)
    lineTo(2.30033f, 9.86667f)
    lineTo(0.666992f, 8f)
    lineTo(2.30033f, 6.13333f)
    lineTo(2.06699f, 3.66667f)
    lineTo(4.46699f, 3.13333f)
    lineTo(5.73366f, 1f)
    lineTo(8.00033f, 1.96667f)
    lineTo(10.267f, 1f)
    lineTo(11.5337f, 3.13333f)
    lineTo(13.9337f, 3.66667f)
    lineTo(13.7003f, 6.13333f)
    lineTo(15.3337f, 8f)
    lineTo(13.7003f, 9.86667f)
    lineTo(13.9337f, 12.3333f)
    lineTo(11.5337f, 12.8667f)
    lineTo(10.267f, 15f)
    lineTo(8.00033f, 14.0333f)
    lineTo(5.73366f, 15f)
    close()
    moveTo(7.30032f, 10.3667f)
    lineTo(11.067f, 6.6f)
    lineTo(10.1337f, 5.63333f)
    lineTo(7.30032f, 8.46667f)
    lineTo(5.86699f, 7.06667f)
    lineTo(4.93366f, 8f)
    lineTo(7.30032f, 10.3667f)
    close()
  }
}.build()
