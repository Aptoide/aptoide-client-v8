package com.aptoide.android.aptoidegames.drawables.backgrounds

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
fun TestErrorIconBackground() {
  Image(
    imageVector = getErrorIconBackground(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getErrorIconBackground(): ImageVector = ImageVector.Builder(
  name = "ErrorIconBackground",
  defaultWidth = 280.0.dp,
  defaultHeight = 144.0.dp,
  viewportWidth = 280.0f,
  viewportHeight = 144.0f
).apply {
  path(
    fill = SolidColor(Color(0xFFC8ED4F))
  ) {
    moveTo(222.0f, 16.0f)
    horizontalLineToRelative(58.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-58.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFD2D2D2))
  ) {
    moveTo(241.0f, 64.0f)
    horizontalLineToRelative(23.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-23.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFffffff))
  ) {
    moveTo(17f, 0.0f)
    horizontalLineToRelative(23.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-23.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFC8ED4F))
  ) {
    moveTo(40.0f, 104.0f)
    horizontalLineToRelative(16.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-16.0f)
    close()
  }
}.build()
