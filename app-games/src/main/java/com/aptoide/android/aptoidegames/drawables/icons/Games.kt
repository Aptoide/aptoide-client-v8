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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.agBlack
import com.aptoide.android.aptoidegames.theme.primary

@Preview
@Composable
fun TestGames() {
  Image(
    imageVector = getGames(primary, agBlack),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getGames(iconColor: Color, bgColor: Color): ImageVector = ImageVector.Builder(
  name = "Games",
  defaultWidth = 24.dp,
  defaultHeight = 24.dp,
  viewportWidth = 24f,
  viewportHeight = 24f,
).apply {
  path(
    fill = SolidColor(iconColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(19.4942f, 23.9932f)
    lineTo(0.0068f, 19.4942f)
    lineTo(4.5059f, 0.0068f)
    lineTo(23.9933f, 4.5058f)
    lineTo(19.4942f, 23.9932f)
    close()
  }
  path(
    fill = SolidColor(bgColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(11.0f, 11.0f)
    lineToRelative(-6.0f, -0.0f)
    lineToRelative(-0.0f, -6.0f)
    lineToRelative(6.0f, -0.0f)
    close()
  }
  path(
    fill = SolidColor(bgColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(16.0f, 11.0f)
    lineTo(16.0f, 11.0f)
    arcTo(3.0f, 3.0f, 0.0f, false, true, 13.0f, 8.0f)
    lineTo(13.0f, 8.0f)
    arcTo(3.0f, 3.0f, 0.0f, false, true, 16.0f, 5.0f)
    lineTo(16.0f, 5.0f)
    arcTo(3.0f, 3.0f, 0.0f, false, true, 19.0f, 8.0f)
    lineTo(19.0f, 8.0f)
    arcTo(3.0f, 3.0f, 0.0f, false, true, 16.0f, 11.0f)
    close()
  }
  path(
    fill = SolidColor(bgColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(11.0f, 19.0f)
    lineTo(5.0f, 19.0f)
    lineTo(8.0f, 13.0f)
    lineTo(11.0f, 19.0f)
    close()
  }
  path(
    fill = SolidColor(bgColor),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(19.0f, 19.0f)
    lineToRelative(-6.0f, -0.0f)
    lineToRelative(-0.0f, -6.0f)
    lineToRelative(6.0f, -0.0f)
    close()
  }
}.build()
