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
private fun TestCrown() {
  Image(
    imageVector = getCrownIcon(Palette.Primary),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getCrownIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "Crown",
  defaultWidth = 24.0.dp,
  defaultHeight = 24.0.dp,
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
      moveTo(5.0f, 20.0f)
      verticalLineTo(18.0f)
      horizontalLineTo(19.0f)
      verticalLineTo(20.0f)
      horizontalLineTo(5.0f)
      close()
      moveTo(5.0f, 16.5f)
      lineTo(3.725f, 8.475f)
      curveTo(3.692f, 8.475f, 3.654f, 8.479f, 3.612f, 8.488f)
      curveTo(3.571f, 8.496f, 3.533f, 8.5f, 3.5f, 8.5f)
      curveTo(3.083f, 8.5f, 2.729f, 8.354f, 2.438f, 8.063f)
      curveTo(2.146f, 7.771f, 2.0f, 7.417f, 2.0f, 7.0f)
      curveTo(2.0f, 6.583f, 2.146f, 6.229f, 2.438f, 5.938f)
      curveTo(2.729f, 5.646f, 3.083f, 5.5f, 3.5f, 5.5f)
      curveTo(3.917f, 5.5f, 4.271f, 5.646f, 4.563f, 5.938f)
      curveTo(4.854f, 6.229f, 5.0f, 6.583f, 5.0f, 7.0f)
      curveTo(5.0f, 7.117f, 4.988f, 7.225f, 4.963f, 7.325f)
      curveTo(4.938f, 7.425f, 4.908f, 7.517f, 4.875f, 7.6f)
      lineTo(8.0f, 9.0f)
      lineTo(11.125f, 4.725f)
      curveTo(10.942f, 4.592f, 10.792f, 4.417f, 10.675f, 4.2f)
      curveTo(10.558f, 3.983f, 10.5f, 3.75f, 10.5f, 3.5f)
      curveTo(10.5f, 3.083f, 10.646f, 2.729f, 10.938f, 2.438f)
      curveTo(11.229f, 2.146f, 11.583f, 2.0f, 12.0f, 2.0f)
      curveTo(12.417f, 2.0f, 12.771f, 2.146f, 13.063f, 2.438f)
      curveTo(13.354f, 2.729f, 13.5f, 3.083f, 13.5f, 3.5f)
      curveTo(13.5f, 3.75f, 13.442f, 3.983f, 13.325f, 4.2f)
      curveTo(13.208f, 4.417f, 13.058f, 4.592f, 12.875f, 4.725f)
      lineTo(16.0f, 9.0f)
      lineTo(19.125f, 7.6f)
      curveTo(19.092f, 7.517f, 19.063f, 7.425f, 19.038f, 7.325f)
      curveTo(19.013f, 7.225f, 19.0f, 7.117f, 19.0f, 7.0f)
      curveTo(19.0f, 6.583f, 19.146f, 6.229f, 19.438f, 5.938f)
      curveTo(19.729f, 5.646f, 20.083f, 5.5f, 20.5f, 5.5f)
      curveTo(20.917f, 5.5f, 21.271f, 5.646f, 21.563f, 5.938f)
      curveTo(21.854f, 6.229f, 22.0f, 6.583f, 22.0f, 7.0f)
      curveTo(22.0f, 7.417f, 21.854f, 7.771f, 21.563f, 8.063f)
      curveTo(21.271f, 8.354f, 20.917f, 8.5f, 20.5f, 8.5f)
      curveTo(20.467f, 8.5f, 20.429f, 8.496f, 20.388f, 8.488f)
      curveTo(20.346f, 8.479f, 20.308f, 8.475f, 20.275f, 8.475f)
      lineTo(19.0f, 16.5f)
      horizontalLineTo(5.0f)
      close()
      moveTo(6.7f, 14.5f)
      horizontalLineTo(17.3f)
      lineTo(17.95f, 10.325f)
      lineTo(15.325f, 11.475f)
      lineTo(12.0f, 6.9f)
      lineTo(8.675f, 11.475f)
      lineTo(6.05f, 10.325f)
      lineTo(6.7f, 14.5f)
      close()
    }
  }
}.build()
