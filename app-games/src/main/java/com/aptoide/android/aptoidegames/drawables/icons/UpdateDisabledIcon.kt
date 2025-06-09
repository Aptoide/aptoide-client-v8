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
private fun TestUpdateDisabled() {
  Image(
    imageVector = getUpdateDisabledIcon(Palette.Error),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getUpdateDisabledIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "UpdateDisabled",
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
      moveTo(19.663f, 22.325f)
      lineTo(16.738f, 19.4f)
      curveTo(16.046f, 19.833f, 15.304f, 20.171f, 14.513f, 20.413f)
      curveTo(13.721f, 20.654f, 12.888f, 20.775f, 12.013f, 20.775f)
      curveTo(10.796f, 20.775f, 9.656f, 20.544f, 8.594f, 20.081f)
      curveTo(7.531f, 19.619f, 6.604f, 18.992f, 5.813f, 18.2f)
      curveTo(5.021f, 17.408f, 4.394f, 16.481f, 3.931f, 15.419f)
      curveTo(3.469f, 14.356f, 3.238f, 13.217f, 3.238f, 12.0f)
      curveTo(3.238f, 11.125f, 3.358f, 10.292f, 3.6f, 9.5f)
      curveTo(3.842f, 8.708f, 4.183f, 7.971f, 4.625f, 7.287f)
      lineTo(1.663f, 4.325f)
      lineTo(3.0f, 2.987f)
      lineTo(21.0f, 20.987f)
      lineTo(19.663f, 22.325f)
      close()
      moveTo(12.013f, 18.9f)
      curveTo(12.621f, 18.9f, 13.206f, 18.825f, 13.769f, 18.675f)
      curveTo(14.331f, 18.525f, 14.867f, 18.313f, 15.375f, 18.038f)
      lineTo(5.975f, 8.637f)
      curveTo(5.7f, 9.137f, 5.488f, 9.669f, 5.338f, 10.231f)
      curveTo(5.188f, 10.794f, 5.113f, 11.383f, 5.113f, 12.0f)
      curveTo(5.113f, 13.917f, 5.783f, 15.546f, 7.125f, 16.888f)
      curveTo(8.467f, 18.229f, 10.096f, 18.9f, 12.013f, 18.9f)
      close()
      moveTo(14.963f, 9.875f)
      verticalLineTo(8.0f)
      horizontalLineTo(17.638f)
      curveTo(16.971f, 7.108f, 16.15f, 6.402f, 15.175f, 5.881f)
      curveTo(14.2f, 5.36f, 13.154f, 5.1f, 12.038f, 5.1f)
      curveTo(11.421f, 5.1f, 10.829f, 5.175f, 10.263f, 5.325f)
      curveTo(9.696f, 5.475f, 9.163f, 5.692f, 8.663f, 5.975f)
      lineTo(7.3f, 4.612f)
      curveTo(7.992f, 4.171f, 8.733f, 3.829f, 9.525f, 3.587f)
      curveTo(10.317f, 3.346f, 11.154f, 3.225f, 12.038f, 3.225f)
      curveTo(13.363f, 3.225f, 14.615f, 3.508f, 15.794f, 4.075f)
      curveTo(16.973f, 4.642f, 17.979f, 5.442f, 18.813f, 6.475f)
      verticalLineTo(4.15f)
      horizontalLineTo(20.688f)
      verticalLineTo(9.875f)
      horizontalLineTo(14.963f)
      close()
      moveTo(12.963f, 10.275f)
      lineTo(11.088f, 8.4f)
      verticalLineTo(7.125f)
      horizontalLineTo(12.963f)
      verticalLineTo(10.275f)
      close()
      moveTo(19.4f, 16.712f)
      lineTo(18.038f, 15.35f)
      curveTo(18.238f, 14.983f, 18.402f, 14.602f, 18.531f, 14.206f)
      curveTo(18.66f, 13.81f, 18.754f, 13.4f, 18.813f, 12.975f)
      horizontalLineTo(20.738f)
      curveTo(20.654f, 13.675f, 20.5f, 14.337f, 20.275f, 14.962f)
      curveTo(20.05f, 15.587f, 19.758f, 16.171f, 19.4f, 16.712f)
      close()
    }
  }
}.build()
