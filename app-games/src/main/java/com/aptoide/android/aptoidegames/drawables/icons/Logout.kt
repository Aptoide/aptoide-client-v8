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
fun TestLogout() {
  Image(
    imageVector = getLogout(Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getLogout(color: Color): ImageVector = ImageVector.Builder(
  name = "Logout",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(color),
    fillAlpha = 0.933333f,
  ) {
    moveTo(3f, 21f)
    verticalLineTo(3f)
    horizontalLineTo(12f)
    verticalLineTo(5f)
    horizontalLineTo(5f)
    verticalLineTo(19f)
    horizontalLineTo(12f)
    verticalLineTo(21f)
    horizontalLineTo(3f)
    close()
    moveTo(16f, 17f)
    lineTo(14.625f, 15.55f)
    lineTo(17.175f, 13f)
    horizontalLineTo(9f)
    verticalLineTo(11f)
    horizontalLineTo(17.175f)
    lineTo(14.625f, 8.45f)
    lineTo(16f, 7f)
    lineTo(21f, 12f)
    lineTo(16f, 17f)
    close()
  }
}.build()
