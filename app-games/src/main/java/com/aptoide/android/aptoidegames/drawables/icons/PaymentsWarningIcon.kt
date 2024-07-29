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
fun TestPaymentsWarningIcon() {
  Image(
    imageVector = getPaymentsWarningIcon(Color.Magenta, Color.Cyan),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPaymentsWarningIcon(
  color1: Color,
  color2: Color,
): ImageVector = ImageVector.Builder(
  name = "PaymentsWarningIcon",
  defaultWidth = 328.0.dp,
  defaultHeight = 96.0.dp,
  viewportWidth = 328.0f,
  viewportHeight = 96.0f
).apply {
  path(
    fill = SolidColor(color1),
  ) {
    moveTo(80.0f, 16.0f)
    horizontalLineToRelative(16.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-16.0f)
    close()
  }
  path(
    fill = SolidColor(color2),
  ) {
    moveTo(209.0f, 8.0f)
    horizontalLineToRelative(47.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-47.0f)
    close()
  }
  path(
    fill = SolidColor(color1),
  ) {
    moveTo(224.0f, 64.0f)
    horizontalLineToRelative(23.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-23.0f)
    close()
  }
  path(
    fill = SolidColor(color1),
  ) {
    moveTo(97.0f, 0.0f)
    horizontalLineToRelative(23.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-23.0f)
    close()
  }
  path(
    fill = SolidColor(color2),
  ) {
    moveTo(88.0f, 72.0f)
    horizontalLineToRelative(16.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-16.0f)
    close()
  }
  path(
    fill = SolidColor(color1),
  ) {
    moveTo(123.667f, 85f)
    lineTo(164f, 15.3334f)
    lineTo(204.334f, 85f)
    horizontalLineTo(123.667f)
    close()
    moveTo(164f, 74f)
    curveTo(165.039f, 74f, 165.91f, 73.6487f, 166.613f, 72.9459f)
    curveTo(167.316f, 72.2431f, 167.667f, 71.3723f, 167.667f, 70.3334f)
    curveTo(167.667f, 69.2945f, 167.316f, 68.4237f, 166.613f, 67.7209f)
    curveTo(165.91f, 67.0181f, 165.039f, 66.6667f, 164f, 66.6667f)
    curveTo(162.961f, 66.6667f, 162.091f, 67.0181f, 161.388f, 67.7209f)
    curveTo(160.685f, 68.4237f, 160.334f, 69.2945f, 160.334f, 70.3334f)
    curveTo(160.334f, 71.3723f, 160.685f, 72.2431f, 161.388f, 72.9459f)
    curveTo(162.091f, 73.6487f, 162.961f, 74f, 164f, 74f)
    close()
    moveTo(160.334f, 63f)
    horizontalLineTo(167.667f)
    verticalLineTo(44.6667f)
    horizontalLineTo(160.334f)
    verticalLineTo(63f)
    close()
  }
}.build()
