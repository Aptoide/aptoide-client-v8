package com.aptoide.android.aptoidegames.drawables.banners

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.secondary

@Preview
@Composable
fun TestChessPatternBanner() {
  Column(
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    Image(
      imageVector = getChessPatternBanner(),
      contentDescription = null,
    )
    Image(
      imageVector = getChessPatternBanner(
        color = secondary,
        blockOffset = 1
      ),
      contentDescription = null,
    )
  }
}

@Composable
fun getChessPatternBanner(
  color: Color = AppTheme.colors.primary,
  blockOffset: Int = 0,
): ImageVector = ImageVector.Builder(
  name = "ChessPatternBanner",
  defaultWidth = 360.dp,
  defaultHeight = 32.dp,
  viewportWidth = 360f,
  viewportHeight = 32f,
).apply {
  for (i in 0..10) {
    path(
      fill = SolidColor(color),
    ) {
      moveTo(blockOffset * 16.73f + i * 32.7268f, 0f)
      horizontalLineToRelative(16f)
      verticalLineToRelative(16f)
      horizontalLineToRelative(-16f)
      close()
    }
  }
  for (i in 0..10) {
    path(
      fill = SolidColor(color),
    ) {
      moveTo((blockOffset + 1) * 16.73f + i * 32.7268f, 16f)
      horizontalLineToRelative(16f)
      verticalLineToRelative(16f)
      horizontalLineToRelative(-16f)
      close()
    }
  }
}.build()
