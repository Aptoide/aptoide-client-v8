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
fun TestAsterisk() {
  Image(
    imageVector = getAsterisk(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getAsterisk(): ImageVector = ImageVector.Builder(
  name = "Asterisk",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(primary),
  ) {
    moveTo(11f, 21f)
    verticalLineTo(14.4f)
    lineTo(6.35f, 19.075f)
    lineTo(4.925f, 17.65f)
    lineTo(9.6f, 13f)
    horizontalLineTo(3f)
    verticalLineTo(11f)
    horizontalLineTo(9.6f)
    lineTo(4.925f, 6.35f)
    lineTo(6.35f, 4.925f)
    lineTo(11f, 9.6f)
    verticalLineTo(3f)
    horizontalLineTo(13f)
    verticalLineTo(9.6f)
    lineTo(17.65f, 4.925f)
    lineTo(19.075f, 6.35f)
    lineTo(14.4f, 11f)
    horizontalLineTo(21f)
    verticalLineTo(13f)
    horizontalLineTo(14.4f)
    lineTo(19.075f, 17.65f)
    lineTo(17.65f, 19.075f)
    lineTo(13f, 14.4f)
    verticalLineTo(21f)
    horizontalLineTo(11f)
    close()
  }
}.build()
