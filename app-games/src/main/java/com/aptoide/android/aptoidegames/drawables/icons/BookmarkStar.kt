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

@Preview
@Composable
fun TestBookMarkStarIcon() {
  Image(
    imageVector = getBookmarkStar(Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getBookmarkStar(color: Color): ImageVector = ImageVector.Builder(
  name = "BookmarkStar",
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
      moveTo(6.483f, 9.3333f)
      lineTo(7.9997f, 8.4167f)
      lineTo(9.5163f, 9.3333f)
      lineTo(9.1163f, 7.6f)
      lineTo(10.4497f, 6.45f)
      lineTo(8.6997f, 6.3f)
      lineTo(7.9997f, 4.6667f)
      lineTo(7.2997f, 6.3f)
      lineTo(5.5497f, 6.45f)
      lineTo(6.883f, 7.6f)
      lineTo(6.483f, 9.3333f)
      close()
      moveTo(3.333f, 14.0f)
      verticalLineTo(2.0f)
      horizontalLineTo(12.6663f)
      verticalLineTo(14.0f)
      lineTo(7.9997f, 12.0f)
      lineTo(3.333f, 14.0f)
      close()
      moveTo(4.6663f, 11.9667f)
      lineTo(7.9997f, 10.5333f)
      lineTo(11.333f, 11.9667f)
      verticalLineTo(3.3333f)
      horizontalLineTo(4.6663f)
      verticalLineTo(11.9667f)
      close()
    }
  }
}
  .build()
