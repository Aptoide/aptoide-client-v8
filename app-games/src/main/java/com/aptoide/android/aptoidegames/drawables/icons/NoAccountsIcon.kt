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
private fun TestNoAccounts() {
  Image(
    imageVector = getNoAccountsIcon(Palette.Primary),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getNoAccountsIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "NoAccounts",
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
      moveTo(15.2f, 10.95f)
      lineTo(10.55f, 6.3f)
      curveTo(10.783f, 6.2f, 11.021f, 6.125f, 11.262f, 6.075f)
      curveTo(11.504f, 6.025f, 11.75f, 6.0f, 12.0f, 6.0f)
      curveTo(12.983f, 6.0f, 13.813f, 6.338f, 14.488f, 7.012f)
      curveTo(15.163f, 7.688f, 15.5f, 8.517f, 15.5f, 9.5f)
      curveTo(15.5f, 9.75f, 15.475f, 9.996f, 15.425f, 10.238f)
      curveTo(15.375f, 10.479f, 15.3f, 10.717f, 15.2f, 10.95f)
      close()
      moveTo(5.85f, 17.1f)
      curveTo(6.7f, 16.45f, 7.65f, 15.938f, 8.7f, 15.563f)
      curveTo(9.75f, 15.188f, 10.85f, 15.0f, 12.0f, 15.0f)
      curveTo(12.3f, 15.0f, 12.587f, 15.012f, 12.863f, 15.038f)
      curveTo(13.137f, 15.063f, 13.425f, 15.1f, 13.725f, 15.15f)
      lineTo(11.525f, 12.95f)
      curveTo(10.742f, 12.85f, 10.071f, 12.521f, 9.512f, 11.962f)
      curveTo(8.954f, 11.404f, 8.625f, 10.733f, 8.525f, 9.95f)
      lineTo(5.675f, 7.1f)
      curveTo(5.142f, 7.783f, 4.729f, 8.538f, 4.438f, 9.363f)
      curveTo(4.146f, 10.188f, 4.0f, 11.067f, 4.0f, 12.0f)
      curveTo(4.0f, 12.983f, 4.162f, 13.908f, 4.488f, 14.775f)
      curveTo(4.813f, 15.642f, 5.267f, 16.417f, 5.85f, 17.1f)
      close()
      moveTo(18.3f, 16.9f)
      curveTo(18.833f, 16.217f, 19.25f, 15.462f, 19.55f, 14.637f)
      curveTo(19.85f, 13.813f, 20.0f, 12.933f, 20.0f, 12.0f)
      curveTo(20.0f, 9.783f, 19.221f, 7.896f, 17.663f, 6.338f)
      curveTo(16.104f, 4.779f, 14.217f, 4.0f, 12.0f, 4.0f)
      curveTo(11.067f, 4.0f, 10.188f, 4.15f, 9.363f, 4.45f)
      curveTo(8.538f, 4.75f, 7.783f, 5.167f, 7.1f, 5.7f)
      lineTo(18.3f, 16.9f)
      close()
      moveTo(12.0f, 22.0f)
      curveTo(10.633f, 22.0f, 9.342f, 21.737f, 8.125f, 21.212f)
      curveTo(6.908f, 20.688f, 5.846f, 19.971f, 4.938f, 19.063f)
      curveTo(4.029f, 18.154f, 3.313f, 17.092f, 2.787f, 15.875f)
      curveTo(2.263f, 14.658f, 2.0f, 13.367f, 2.0f, 12.0f)
      curveTo(2.0f, 10.617f, 2.263f, 9.321f, 2.787f, 8.113f)
      curveTo(3.313f, 6.904f, 4.029f, 5.846f, 4.938f, 4.938f)
      curveTo(5.846f, 4.029f, 6.908f, 3.313f, 8.125f, 2.787f)
      curveTo(9.342f, 2.263f, 10.633f, 2.0f, 12.0f, 2.0f)
      curveTo(13.383f, 2.0f, 14.679f, 2.263f, 15.887f, 2.787f)
      curveTo(17.096f, 3.313f, 18.154f, 4.029f, 19.063f, 4.938f)
      curveTo(19.971f, 5.846f, 20.688f, 6.904f, 21.212f, 8.113f)
      curveTo(21.737f, 9.321f, 22.0f, 10.617f, 22.0f, 12.0f)
      curveTo(22.0f, 13.367f, 21.737f, 14.658f, 21.212f, 15.875f)
      curveTo(20.688f, 17.092f, 19.971f, 18.154f, 19.063f, 19.063f)
      curveTo(18.154f, 19.971f, 17.096f, 20.688f, 15.887f, 21.212f)
      curveTo(14.679f, 21.737f, 13.383f, 22.0f, 12.0f, 22.0f)
      close()
      moveTo(12.0f, 20.0f)
      curveTo(12.883f, 20.0f, 13.717f, 19.871f, 14.5f, 19.612f)
      curveTo(15.283f, 19.354f, 16.0f, 18.983f, 16.65f, 18.5f)
      curveTo(16.0f, 18.017f, 15.283f, 17.646f, 14.5f, 17.388f)
      curveTo(13.717f, 17.129f, 12.883f, 17.0f, 12.0f, 17.0f)
      curveTo(11.117f, 17.0f, 10.283f, 17.129f, 9.5f, 17.388f)
      curveTo(8.717f, 17.646f, 8.0f, 18.017f, 7.35f, 18.5f)
      curveTo(8.0f, 18.983f, 8.717f, 19.354f, 9.5f, 19.612f)
      curveTo(10.283f, 19.871f, 11.117f, 20.0f, 12.0f, 20.0f)
      close()
    }
  }
}.build()
