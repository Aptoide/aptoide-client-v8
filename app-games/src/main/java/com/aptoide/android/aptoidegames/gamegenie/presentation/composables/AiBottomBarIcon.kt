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
fun AiBottomBarIconPreview() {
  Image(
    imageVector = getAiBottomBarIcon(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getAiBottomBarIcon(): ImageVector = ImageVector.Builder(
  name = "ai_bottom_bar_icon",
  defaultWidth = 14.dp,
  defaultHeight = 14.dp,
  viewportWidth = 14f,
  viewportHeight = 14f,
  tintColor = Color.Unspecified,
).apply {
  path(
    fill = SolidColor(Color.White),
  ) {
    moveTo(0f, 0f)
    lineTo(14f, 0f)
    lineTo(14f, 14f)
    lineTo(0f, 14f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFF1E1E26)),
  ) {
    moveTo(10.1147f, 3f)
    horizontalLineTo(11.4747f)
    verticalLineTo(10f)
    horizontalLineTo(10.1147f)
    verticalLineTo(3f)
    close()
  }
  path(
    fill = SolidColor(Color.Black),
    fillAlpha = 0.2f,
  ) {
    moveTo(10.1147f, 3f)
    horizontalLineTo(11.4747f)
    verticalLineTo(10f)
    horizontalLineTo(10.1147f)
    verticalLineTo(3f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFF1E1E26)),
  ) {
    moveTo(5.56f, 3f)
    horizontalLineTo(6.79999f)
    lineTo(9.35999f, 10f)
    horizontalLineTo(7.97999f)
    lineTo(7.40999f, 8.45001f)
    horizontalLineTo(4.95f)
    lineTo(4.38f, 10f)
    horizontalLineTo(3f)
    lineTo(5.56f, 3f)
    close()
    moveTo(7.10999f, 7.33001f)
    lineTo(6.17999f, 4.67f)
    horizontalLineTo(6.15999f)
    lineTo(5.26f, 7.33001f)
    horizontalLineTo(7.10999f)
    close()
  }
  path(
    fill = SolidColor(Color.Black),
    fillAlpha = 0.2f,
  ) {
    moveTo(5.56f, 3f)
    horizontalLineTo(6.79999f)
    lineTo(9.35999f, 10f)
    horizontalLineTo(7.97999f)
    lineTo(7.40999f, 8.45001f)
    horizontalLineTo(4.95f)
    lineTo(4.38f, 10f)
    horizontalLineTo(3f)
    lineTo(5.56f, 3f)
    close()
    moveTo(7.10999f, 7.33001f)
    lineTo(6.17999f, 4.67f)
    horizontalLineTo(6.15999f)
    lineTo(5.26f, 7.33001f)
    horizontalLineTo(7.10999f)
    close()
  }
}.build()
