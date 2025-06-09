package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
private fun TestLocalPolice() {
  Image(
    imageVector = getLocalPoliceIcon(Palette.Primary),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getLocalPoliceIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "LocalPolice",
  defaultWidth = 24.0.dp,
  defaultHeight =
    24.0.dp,
  viewportWidth = 24.0f,
  viewportHeight = 24.0f
).apply {
  group {
    path(
      fill = SolidColor(color),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(9.2f, 15.6f)
      lineTo(12.0f, 13.5f)
      lineTo(14.75f, 15.6f)
      lineTo(13.7f, 12.2f)
      lineTo(16.5f, 10.0f)
      horizontalLineTo(13.1f)
      lineTo(12.0f, 6.6f)
      lineTo(10.9f, 10.0f)
      horizontalLineTo(7.5f)
      lineTo(10.25f, 12.2f)
      lineTo(9.2f, 15.6f)
      close()
      moveTo(12.0f, 22.0f)
      curveTo(9.683f, 21.417f, 7.771f, 20.087f, 6.262f, 18.013f)
      curveTo(4.754f, 15.938f, 4.0f, 13.633f, 4.0f, 11.1f)
      verticalLineTo(5.0f)
      lineTo(12.0f, 2.0f)
      lineTo(20.0f, 5.0f)
      verticalLineTo(11.1f)
      curveTo(20.0f, 13.633f, 19.246f, 15.938f, 17.737f, 18.013f)
      curveTo(16.229f, 20.087f, 14.317f, 21.417f, 12.0f, 22.0f)
      close()
      moveTo(12.0f, 19.9f)
      curveTo(13.733f, 19.35f, 15.167f, 18.25f, 16.3f, 16.6f)
      curveTo(17.433f, 14.95f, 18.0f, 13.117f, 18.0f, 11.1f)
      verticalLineTo(6.375f)
      lineTo(12.0f, 4.125f)
      lineTo(6.0f, 6.375f)
      verticalLineTo(11.1f)
      curveTo(6.0f, 13.117f, 6.567f, 14.95f, 7.7f, 16.6f)
      curveTo(8.833f, 18.25f, 10.267f, 19.35f, 12.0f, 19.9f)
      close()
    }
  }
}.build()
