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
fun TestMoreVert() {
    Image(
      imageVector = getMoreVert(Color.Black),
      contentDescription = null,
      modifier = Modifier.size(240.dp)
    )
}
fun getMoreVert(color: Color): ImageVector = ImageVector.Builder(
  name = "MoreVert",
  defaultWidth = 31.0.dp,
  defaultHeight = 32.0.dp,
  viewportWidth = 31.0f,
  viewportHeight = 32.0f
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
      moveTo(12.7659f, 5.3333f)
      horizontalLineToRelative(5.1064f)
      verticalLineToRelative(5.3333f)
      horizontalLineToRelative(-5.1064f)
      close()
      moveTo(12.7659f, 13.3333f)
      horizontalLineToRelative(5.1064f)
      verticalLineToRelative(5.3333f)
      horizontalLineToRelative(-5.1064f)
      close()
      moveTo(12.7659f, 21.3333f)
      horizontalLineToRelative(5.1064f)
      verticalLineToRelative(5.3333f)
      horizontalLineToRelative(-5.1064f)
      close()
    }
  }
}.build()

