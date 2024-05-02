package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.richOrange

@Preview
@Composable
fun TestCaretRight() {
  Image(
    imageVector = getCaretRight(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getCaretRight(): ImageVector = ImageVector.Builder(
  name = "CaretRight",
  defaultWidth = 7.dp,
  defaultHeight = 12.dp,
  viewportWidth = 7f,
  viewportHeight = 12f,
).apply {
  path(
    fill = SolidColor(richOrange),
  ) {
    moveTo(1.06876e-06f, 1.41f)
    lineTo(4.32659f, 6f)
    lineTo(2.66219e-07f, 10.59f)
    lineTo(1.33198f, 12f)
    lineTo(7f, 6f)
    lineTo(1.33198f, -4.95514e-07f)
    lineTo(1.06876e-06f, 1.41f)
    close()
  }
}.build()
