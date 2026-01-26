package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

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
fun SendIconDisabledPreview() {
  Image(
    imageVector = getSendIconDisabled(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getSendIconDisabled(): ImageVector = ImageVector.Builder(
  name = "send_icon_disabled",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
  tintColor = Color.Unspecified,
).apply {
  path(
    fill = SolidColor(Color(0xFFC8ED4F)),
  ) {
    moveTo(3f, 20f)
    verticalLineTo(4f)
    lineTo(22f, 12f)
    lineTo(3f, 20f)
    close()
    moveTo(5f, 17f)
    lineTo(16.85f, 12f)
    lineTo(5f, 7f)
    verticalLineTo(10.5f)
    lineTo(11f, 12f)
    lineTo(5f, 13.5f)
    verticalLineTo(17f)
    close()
  }
}.build()
