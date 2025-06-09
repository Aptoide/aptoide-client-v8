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
private fun TestRocket() {
  Image(
    imageVector = getRocketIcon(Palette.Primary),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getRocketIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "Rocket",
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
      moveTo(6.0f, 19.05f)
      lineTo(7.975f, 18.25f)
      curveTo(7.808f, 17.767f, 7.654f, 17.275f, 7.512f, 16.775f)
      curveTo(7.371f, 16.275f, 7.258f, 15.775f, 7.175f, 15.275f)
      lineTo(6.0f, 16.075f)
      verticalLineTo(19.05f)
      close()
      moveTo(10.0f, 18.0f)
      horizontalLineTo(14.0f)
      curveTo(14.3f, 17.333f, 14.542f, 16.521f, 14.725f, 15.563f)
      curveTo(14.908f, 14.604f, 15.0f, 13.625f, 15.0f, 12.625f)
      curveTo(15.0f, 10.975f, 14.725f, 9.413f, 14.175f, 7.938f)
      curveTo(13.625f, 6.463f, 12.9f, 5.325f, 12.0f, 4.525f)
      curveTo(11.1f, 5.325f, 10.375f, 6.463f, 9.825f, 7.938f)
      curveTo(9.275f, 9.413f, 9.0f, 10.975f, 9.0f, 12.625f)
      curveTo(9.0f, 13.625f, 9.092f, 14.604f, 9.275f, 15.563f)
      curveTo(9.458f, 16.521f, 9.7f, 17.333f, 10.0f, 18.0f)
      close()
      moveTo(12.0f, 13.0f)
      curveTo(11.45f, 13.0f, 10.979f, 12.804f, 10.587f, 12.413f)
      curveTo(10.196f, 12.021f, 10.0f, 11.55f, 10.0f, 11.0f)
      curveTo(10.0f, 10.45f, 10.196f, 9.979f, 10.587f, 9.588f)
      curveTo(10.979f, 9.196f, 11.45f, 9.0f, 12.0f, 9.0f)
      curveTo(12.55f, 9.0f, 13.021f, 9.196f, 13.413f, 9.588f)
      curveTo(13.804f, 9.979f, 14.0f, 10.45f, 14.0f, 11.0f)
      curveTo(14.0f, 11.55f, 13.804f, 12.021f, 13.413f, 12.413f)
      curveTo(13.021f, 12.804f, 12.55f, 13.0f, 12.0f, 13.0f)
      close()
      moveTo(18.0f, 19.05f)
      verticalLineTo(16.075f)
      lineTo(16.825f, 15.275f)
      curveTo(16.742f, 15.775f, 16.629f, 16.275f, 16.487f, 16.775f)
      curveTo(16.346f, 17.275f, 16.192f, 17.767f, 16.025f, 18.25f)
      lineTo(18.0f, 19.05f)
      close()
      moveTo(12.0f, 1.975f)
      curveTo(13.65f, 3.175f, 14.896f, 4.7f, 15.738f, 6.55f)
      curveTo(16.579f, 8.4f, 17.0f, 10.55f, 17.0f, 13.0f)
      lineTo(19.1f, 14.4f)
      curveTo(19.383f, 14.583f, 19.604f, 14.825f, 19.763f, 15.125f)
      curveTo(19.921f, 15.425f, 20.0f, 15.742f, 20.0f, 16.075f)
      verticalLineTo(22.0f)
      lineTo(15.025f, 20.0f)
      horizontalLineTo(8.975f)
      lineTo(4.0f, 22.0f)
      verticalLineTo(16.075f)
      curveTo(4.0f, 15.742f, 4.079f, 15.425f, 4.238f, 15.125f)
      curveTo(4.396f, 14.825f, 4.617f, 14.583f, 4.9f, 14.4f)
      lineTo(7.0f, 13.0f)
      curveTo(7.0f, 10.55f, 7.421f, 8.4f, 8.262f, 6.55f)
      curveTo(9.104f, 4.7f, 10.35f, 3.175f, 12.0f, 1.975f)
      close()
    }
  }
}.build()
