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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestPermissionAllowFigure() {
  Image(
    imageVector = getPermissionAllowFigure(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getPermissionAllowFigure(): ImageVector = ImageVector.Builder(
  name = "PermissionAllowFigure",
  defaultWidth = 296.0.dp,
  defaultHeight = 63.0.dp,
  viewportWidth = 296.0f,
  viewportHeight = 63.0f
).apply {
  path(
    fill = SolidColor(Color(0xFF1E1E26)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 0.0f)
    horizontalLineToRelative(296.0f)
    verticalLineToRelative(62.0f)
    horizontalLineToRelative(-296.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFF000000)),
    stroke = null,
    fillAlpha = 0.2f,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(0.0f, 0.0f)
    horizontalLineToRelative(296.0f)
    verticalLineToRelative(62.0f)
    horizontalLineToRelative(-296.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFF595959)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(16.0f, 16.0f)
    horizontalLineToRelative(187.0f)
    verticalLineToRelative(12.0f)
    horizontalLineToRelative(-187.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFF595959)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(16.0f, 38.0f)
    horizontalLineToRelative(96.0f)
    verticalLineToRelative(8.0f)
    horizontalLineToRelative(-96.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFFFC93E)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(248.0f, 20.0f)
    lineTo(266.0f, 20.0f)
    arcTo(11.0f, 11.0f, 0.0f, false, true, 277.0f, 31.0f)
    lineTo(277.0f, 31.0f)
    arcTo(11.0f, 11.0f, 0.0f, false, true, 266.0f, 42.0f)
    lineTo(248.0f, 42.0f)
    arcTo(11.0f, 11.0f, 0.0f, false, true, 237.0f, 31.0f)
    lineTo(237.0f, 31.0f)
    arcTo(11.0f, 11.0f, 0.0f, false, true, 248.0f, 20.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFF312D35)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(266.0f, 22.0f)
    lineTo(266.0f, 22.0f)
    arcTo(9.0f, 9.0f, 0.0f, false, true, 275.0f, 31.0f)
    lineTo(275.0f, 31.0f)
    arcTo(9.0f, 9.0f, 0.0f, false, true, 266.0f, 40.0f)
    lineTo(266.0f, 40.0f)
    arcTo(9.0f, 9.0f, 0.0f, false, true, 257.0f, 31.0f)
    lineTo(257.0f, 31.0f)
    arcTo(9.0f, 9.0f, 0.0f, false, true, 266.0f, 22.0f)
    close()
  }
  path(
    fill = SolidColor(Color(0xFFffffff)),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(269.27f, 62.63f)
    curveTo(268.52f, 62.63f, 267.8f, 62.46f, 267.14f, 62.14f)
    curveTo(266.48f, 61.81f, 265.92f, 61.35f, 265.45f, 60.76f)
    lineTo(257.29f, 50.4f)
    curveTo(257.07f, 50.15f, 256.98f, 49.86f, 257.01f, 49.52f)
    curveTo(257.03f, 49.18f, 257.15f, 48.91f, 257.37f, 48.69f)
    curveTo(257.91f, 48.12f, 258.56f, 47.78f, 259.32f, 47.67f)
    curveTo(260.08f, 47.57f, 260.78f, 47.72f, 261.43f, 48.12f)
    lineTo(264.44f, 49.95f)
    verticalLineTo(36.63f)
    curveTo(264.44f, 36.16f, 264.6f, 35.78f, 264.91f, 35.47f)
    curveTo(265.22f, 35.16f, 265.6f, 35.0f, 266.06f, 35.0f)
    curveTo(266.52f, 35.0f, 266.92f, 35.16f, 267.24f, 35.47f)
    curveTo(267.57f, 35.78f, 267.73f, 36.16f, 267.73f, 36.63f)
    verticalLineTo(44.75f)
    horizontalLineTo(279.88f)
    curveTo(281.23f, 44.75f, 282.38f, 45.22f, 283.33f, 46.17f)
    curveTo(284.28f, 47.12f, 284.75f, 48.27f, 284.75f, 49.63f)
    verticalLineTo(56.13f)
    curveTo(284.75f, 57.91f, 284.11f, 59.44f, 282.84f, 60.72f)
    curveTo(281.57f, 61.99f, 280.04f, 62.63f, 278.25f, 62.63f)
    horizontalLineTo(269.27f)
    close()
  }
}.build()
