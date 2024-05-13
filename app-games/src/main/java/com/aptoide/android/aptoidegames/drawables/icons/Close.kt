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
import com.aptoide.android.aptoidegames.theme.agWhite

@Preview
@Composable
fun TestClose() {
  Image(
    imageVector = getClose(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getClose(): ImageVector = ImageVector.Builder(
  name = "Close",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(agWhite),
  ) {
    moveTo(6.4f, 19f)
    lineTo(5f, 17.6f)
    lineTo(10.6f, 12f)
    lineTo(5f, 6.4f)
    lineTo(6.4f, 5f)
    lineTo(12f, 10.6f)
    lineTo(17.6f, 5f)
    lineTo(19f, 6.4f)
    lineTo(13.4f, 12f)
    lineTo(19f, 17.6f)
    lineTo(17.6f, 19f)
    lineTo(12f, 13.4f)
    lineTo(6.4f, 19f)
    close()
  }
}.build()
