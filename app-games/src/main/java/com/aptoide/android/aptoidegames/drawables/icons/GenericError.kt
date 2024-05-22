package com.aptoide.android.aptoidegames.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
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
import com.aptoide.android.aptoidegames.theme.greyLight
import com.aptoide.android.aptoidegames.theme.primary

@Preview
@Composable
fun TestGenericError() {
  Image(
    imageVector = getGenericError(),
    contentDescription = null,
  )
}

fun getGenericError(): ImageVector = ImageVector.Builder(
  name = "GenericError",
  defaultWidth = 328.0.dp,
  defaultHeight = 144.0.dp,
  viewportWidth = 328.0f,
  viewportHeight = 144.0f
).apply {
  path(
    fill = SolidColor(primary),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(246.0f, 16.0f)
    horizontalLineToRelative(58.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-58.0f)
    close()
  }
  path(
    fill = SolidColor(greyLight),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(265.0f, 64.0f)
    horizontalLineToRelative(23.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-23.0f)
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
    moveTo(41.0f, 0.0f)
    horizontalLineToRelative(23.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-23.0f)
    close()
  }
  path(
    fill = SolidColor(primary),
    stroke = null,
    strokeLineWidth = 0.0f,
    strokeLineCap = Butt,
    strokeLineJoin = Miter,
    strokeLineMiter = 4.0f,
    pathFillType = NonZero
  ) {
    moveTo(64.0f, 104.0f)
    horizontalLineToRelative(16.0f)
    verticalLineToRelative(16.0f)
    horizontalLineToRelative(-16.0f)
    close()
  }
  group {
    path(
      fill = SolidColor(primary),
      stroke = null,
      strokeLineWidth = 0.0f,
      strokeLineCap = Butt,
      strokeLineJoin = Miter,
      strokeLineMiter = 4.0f,
      pathFillType = NonZero
    ) {
      moveTo(164.0f, 126.0f)
      curveTo(157.5f, 126.0f, 151.475f, 124.4f, 145.925f, 121.2f)
      curveTo(140.375f, 118.0f, 136.0f, 113.6f, 132.8f, 108.0f)
      horizontalLineTo(116.0f)
      verticalLineTo(96.0f)
      horizontalLineTo(128.6f)
      curveTo(128.3f, 94.0f, 128.125f, 92.0f, 128.075f, 90.0f)
      curveTo(128.025f, 88.0f, 128.0f, 86.0f, 128.0f, 84.0f)
      horizontalLineTo(116.0f)
      verticalLineTo(72.0f)
      horizontalLineTo(128.0f)
      curveTo(128.0f, 70.0f, 128.025f, 68.0f, 128.075f, 66.0f)
      curveTo(128.125f, 64.0f, 128.3f, 62.0f, 128.6f, 60.0f)
      horizontalLineTo(116.0f)
      verticalLineTo(48.0f)
      horizontalLineTo(132.8f)
      curveTo(134.2f, 45.7f, 135.775f, 43.55f, 137.525f, 41.55f)
      curveTo(139.275f, 39.55f, 141.3f, 37.8f, 143.6f, 36.3f)
      lineTo(134.0f, 26.4f)
      lineTo(142.4f, 18.0f)
      lineTo(155.3f, 30.9f)
      curveTo(158.1f, 30.0f, 160.95f, 29.55f, 163.85f, 29.55f)
      curveTo(166.75f, 29.55f, 169.6f, 30.0f, 172.4f, 30.9f)
      lineTo(185.6f, 18.0f)
      lineTo(194.0f, 26.4f)
      lineTo(184.1f, 36.3f)
      curveTo(186.4f, 37.8f, 188.475f, 39.525f, 190.325f, 41.475f)
      curveTo(192.175f, 43.425f, 193.8f, 45.6f, 195.2f, 48.0f)
      horizontalLineTo(212.0f)
      verticalLineTo(60.0f)
      horizontalLineTo(199.4f)
      curveTo(199.7f, 62.0f, 199.875f, 64.0f, 199.925f, 66.0f)
      curveTo(199.975f, 68.0f, 200.0f, 70.0f, 200.0f, 72.0f)
      horizontalLineTo(212.0f)
      verticalLineTo(84.0f)
      horizontalLineTo(200.0f)
      curveTo(200.0f, 86.0f, 199.975f, 88.0f, 199.925f, 90.0f)
      curveTo(199.875f, 92.0f, 199.7f, 94.0f, 199.4f, 96.0f)
      horizontalLineTo(212.0f)
      verticalLineTo(108.0f)
      horizontalLineTo(195.2f)
      curveTo(192.0f, 113.6f, 187.625f, 118.0f, 182.075f, 121.2f)
      curveTo(176.525f, 124.4f, 170.5f, 126.0f, 164.0f, 126.0f)
      close()
      moveTo(152.0f, 96.0f)
      horizontalLineTo(176.0f)
      verticalLineTo(84.0f)
      horizontalLineTo(152.0f)
      verticalLineTo(96.0f)
      close()
      moveTo(152.0f, 72.0f)
      horizontalLineTo(176.0f)
      verticalLineTo(60.0f)
      horizontalLineTo(152.0f)
      verticalLineTo(72.0f)
      close()
    }
  }
}.build()
