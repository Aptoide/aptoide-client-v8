package com.aptoide.android.aptoidegames.drawables.figures

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
fun TestCheckCircle() {
  Image(
    imageVector = getCheckCircle(Palette.SecondaryLight),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getCheckCircle(color: Color): ImageVector = ImageVector.Builder(
  name = "CheckCircle",
  defaultWidth = 16.0.dp,
  defaultHeight = 16.0.dp,
  viewportWidth = 16.0f,
  viewportHeight = 16.0f
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
      moveTo(7.065f, 11.066f)
      lineTo(11.765f, 6.366f)
      lineTo(10.832f, 5.433f)
      lineTo(7.065f, 9.2f)
      lineTo(5.165f, 7.3f)
      lineTo(4.232f, 8.233f)
      lineTo(7.065f, 11.066f)
      close()
      moveTo(7.999f, 14.666f)
      curveTo(7.076f, 14.666f, 6.21f, 14.491f, 5.399f, 14.141f)
      curveTo(4.588f, 13.791f, 3.882f, 13.316f, 3.282f, 12.716f)
      curveTo(2.682f, 12.116f, 2.207f, 11.411f, 1.857f, 10.6f)
      curveTo(1.507f, 9.789f, 1.332f, 8.922f, 1.332f, 8.0f)
      curveTo(1.332f, 7.077f, 1.507f, 6.211f, 1.857f, 5.4f)
      curveTo(2.207f, 4.589f, 2.682f, 3.883f, 3.282f, 3.283f)
      curveTo(3.882f, 2.683f, 4.588f, 2.208f, 5.399f, 1.858f)
      curveTo(6.21f, 1.508f, 7.076f, 1.333f, 7.999f, 1.333f)
      curveTo(8.921f, 1.333f, 9.788f, 1.508f, 10.599f, 1.858f)
      curveTo(11.41f, 2.208f, 12.115f, 2.683f, 12.715f, 3.283f)
      curveTo(13.315f, 3.883f, 13.79f, 4.589f, 14.14f, 5.4f)
      curveTo(14.49f, 6.211f, 14.665f, 7.077f, 14.665f, 8.0f)
      curveTo(14.665f, 8.922f, 14.49f, 9.789f, 14.14f, 10.6f)
      curveTo(13.79f, 11.411f, 13.315f, 12.116f, 12.715f, 12.716f)
      curveTo(12.115f, 13.316f, 11.41f, 13.791f, 10.599f, 14.141f)
      curveTo(9.788f, 14.491f, 8.921f, 14.666f, 7.999f, 14.666f)
      close()
    }
  }
}.build()
