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
import com.aptoide.android.aptoidegames.theme.primary

@Preview
@Composable
fun TestCheck() {
  Image(
    imageVector = getCheck(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getCheck(): ImageVector = ImageVector.Builder(
  name = "Check",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(primary),
  ) {
    moveTo(9.5501f, 18f)
    lineTo(3.8501f, 12.3f)
    lineTo(5.2751f, 10.875f)
    lineTo(9.5501f, 15.15f)
    lineTo(18.7251f, 5.97501f)
    lineTo(20.1501f, 7.40001f)
    lineTo(9.5501f, 18f)
    close()
  }
}.build()
