package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.agWhite

@Preview
@Composable
fun TestMoreVert() {
    Image(
      imageVector = getMoreVert(),
      contentDescription = null,
      modifier = Modifier.size(240.dp)
    )
}
fun getMoreVert(): ImageVector = ImageVector.Builder(
  name = "MoreVert",
  defaultWidth = 31.0.dp,
  defaultHeight = 32.0.dp,
  viewportWidth = 31.0f,
  viewportHeight = 32.0f
).apply {
  group {
    path(
      fill = SolidColor(agWhite),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(15.3191f, 10.6666f)
      curveTo(16.7233f, 10.6666f, 17.8723f, 9.4666f, 17.8723f, 7.9999f)
      curveTo(17.8723f, 6.5332f, 16.7233f, 5.3333f, 15.3191f, 5.3333f)
      curveTo(13.9148f, 5.3333f, 12.7659f, 6.5332f, 12.7659f, 7.9999f)
      curveTo(12.7659f, 9.4666f, 13.9148f, 10.6666f, 15.3191f, 10.6666f)
      close()
      moveTo(15.3191f, 13.3333f)
      curveTo(13.9148f, 13.3333f, 12.7659f, 14.5333f, 12.7659f, 15.9999f)
      curveTo(12.7659f, 17.4666f, 13.9148f, 18.6666f, 15.3191f, 18.6666f)
      curveTo(16.7233f, 18.6666f, 17.8723f, 17.4666f, 17.8723f, 15.9999f)
      curveTo(17.8723f, 14.5333f, 16.7233f, 13.3333f, 15.3191f, 13.3333f)
      close()
      moveTo(15.3191f, 21.3333f)
      curveTo(13.9148f, 21.3333f, 12.7659f, 22.5333f, 12.7659f, 23.9999f)
      curveTo(12.7659f, 25.4666f, 13.9148f, 26.6666f, 15.3191f, 26.6666f)
      curveTo(16.7233f, 26.6666f, 17.8723f, 25.4666f, 17.8723f, 23.9999f)
      curveTo(17.8723f, 22.5333f, 16.7233f, 21.3333f, 15.3191f, 21.3333f)
      close()
    }
    path(
      fill = SolidColor(agWhite),
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
    }
    path(
      fill = SolidColor(agWhite),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(12.7659f, 13.3333f)
      horizontalLineToRelative(5.1064f)
      verticalLineToRelative(5.3333f)
      horizontalLineToRelative(-5.1064f)
      close()
    }
    path(
      fill = SolidColor(agWhite),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(12.7659f, 21.3333f)
      horizontalLineToRelative(5.1064f)
      verticalLineToRelative(5.3333f)
      horizontalLineToRelative(-5.1064f)
      close()
    }
  }
}.build()

