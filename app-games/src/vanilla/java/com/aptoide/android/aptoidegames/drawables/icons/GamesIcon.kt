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
fun TestHomeIcon() {
  Image(
    imageVector = getGamesIcon(Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

// Material Filled.Home path mapped to the same getGamesIcon signature used by
// the bottom bar, so vanilla's "Home" tab gets a house glyph in place of the AG
// controller without touching the call sites in main/.
fun getGamesIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "HomeIcon",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(fill = SolidColor(color)) {
    moveTo(10f, 20f)
    verticalLineToRelative(-6f)
    horizontalLineToRelative(4f)
    verticalLineToRelative(6f)
    horizontalLineToRelative(5f)
    verticalLineToRelative(-8f)
    horizontalLineToRelative(3f)
    lineTo(12f, 3f)
    lineTo(2f, 12f)
    horizontalLineToRelative(3f)
    verticalLineToRelative(8f)
    close()
  }
}.build()
