package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun TestLaunchGameGenieArrow() {
  Image(
    imageVector = getLaunchGameGenieArrow(Color.White, Color.Black),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getLaunchGameGenieArrow(
  color: Color = Palette.White,
  bgColor: Color = Palette.Secondary,
): ImageVector = ImageVector.Builder(
  name = "LaunchGameGenie",
  defaultWidth = 34.dp,
  defaultHeight = 34.dp,
  viewportWidth = 34f,
  viewportHeight = 34f
).apply {
  group {
    // Background rectangle
    path(
      fill = SolidColor(bgColor),
      fillAlpha = 1.0f,
      stroke = null,
      strokeAlpha = 1.0f,
      strokeLineWidth = 1.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 1.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(0f, 0f)
      horizontalLineTo(34f)
      verticalLineTo(34f)
      horizontalLineTo(0f)
      verticalLineTo(0f)
      close()
    }
    // Arrow shape
    path(
      fill = SolidColor(color),
      fillAlpha = 1.0f,
      stroke = null,
      strokeAlpha = 1.0f,
      strokeLineWidth = 1.0f,
      strokeLineCap = StrokeCap.Butt,
      strokeLineJoin = StrokeJoin.Miter,
      strokeLineMiter = 1.0f,
      pathFillType = PathFillType.NonZero
    ) {
      moveTo(14.3334f, 12.3334f)
      lineTo(21.6667f, 17.0001f)
      lineTo(14.3334f, 21.6667f)
      lineTo(14.3334f, 12.3334f)
      close()
    }
  }
}.build()
