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
fun TestTrustedIcon() {
  Image(
    imageVector = getTrustedIcon(Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getTrustedIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "TrustedIcon",
  defaultWidth = 16.dp,
  defaultHeight = 16.dp,
  viewportWidth = 16f,
  viewportHeight = 16f,
).apply {
  path(
    fill = SolidColor(color),
  ) {
    moveTo(8.00033f, 1.33325f)
    lineTo(2.66699f, 3.33325f)
    verticalLineTo(7.39325f)
    curveTo(2.66699f, 10.7599f, 4.94033f, 13.8999f, 8.00033f, 14.6666f)
    curveTo(11.0603f, 13.8999f, 13.3337f, 10.7599f, 13.3337f, 7.39325f)
    verticalLineTo(3.33325f)
    lineTo(8.00033f, 1.33325f)
    close()
    moveTo(7.29366f, 10.3599f)
    lineTo(4.93366f, 7.99992f)
    lineTo(5.87366f, 7.05992f)
    lineTo(7.28699f, 8.47325f)
    lineTo(10.1137f, 5.64659f)
    lineTo(11.0537f, 6.58659f)
    lineTo(7.29366f, 10.3599f)
    close()
  }
}.build()
