package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.FixedColors
import com.aptoide.android.aptoidegames.theme.Palette

@Preview
@Composable
fun GameGenieAiToolbarIconPreview() {
  Image(
    imageVector = getGameGenieAiToolbarIcon(Palette.Primary),
    contentDescription = null,
  )
}

/**
 * The GameGenie "AI" toolbar badge. The white pill and dark "AI" glyphs are
 * theme-invariant (via [FixedColors]); the three decorative pixels take the
 * brand accent [pixelColor] (lime in Aptoide Games, orange in Vanilla when the
 * caller passes [Palette.Primary]).
 */
fun getGameGenieAiToolbarIcon(pixelColor: Color): ImageVector = ImageVector.Builder(
  name = "gamegenie_ai_toolbar_icon",
  defaultWidth = 17.dp,
  defaultHeight = 15.dp,
  viewportWidth = 17f,
  viewportHeight = 15f,
).apply {
  // White pill background
  path(fill = SolidColor(FixedColors.White)) {
    moveTo(1.5f, 0f)
    horizontalLineToRelative(12f)
    verticalLineToRelative(12f)
    horizontalLineToRelative(-12f)
    close()
  }
  // "I" glyph
  path(fill = SolidColor(FixedColors.Dark)) {
    moveTo(9.377f, 3f)
    horizontalLineTo(10.5f)
    verticalLineTo(9f)
    horizontalLineTo(9.377f)
    verticalLineTo(3f)
    close()
  }
  path(fill = SolidColor(FixedColors.Scrim), fillAlpha = 0.2f) {
    moveTo(9.377f, 3f)
    horizontalLineTo(10.5f)
    verticalLineTo(9f)
    horizontalLineTo(9.377f)
    verticalLineTo(3f)
    close()
  }
  // "A" glyph (EvenOdd for the inner counter)
  path(fill = SolidColor(FixedColors.Dark), pathFillType = PathFillType.EvenOdd) {
    moveTo(5.615f, 3f)
    horizontalLineTo(6.639f)
    lineTo(8.753f, 9f)
    horizontalLineTo(7.614f)
    lineTo(7.143f, 7.671f)
    horizontalLineTo(5.111f)
    lineTo(4.64f, 9f)
    horizontalLineTo(3.5f)
    lineTo(5.615f, 3f)
    close()
    moveTo(6.895f, 6.711f)
    lineTo(6.127f, 4.431f)
    horizontalLineTo(6.11f)
    lineTo(5.367f, 6.711f)
    horizontalLineTo(6.895f)
    close()
  }
  path(
    fill = SolidColor(FixedColors.Scrim),
    fillAlpha = 0.2f,
    pathFillType = PathFillType.EvenOdd,
  ) {
    moveTo(5.615f, 3f)
    horizontalLineTo(6.639f)
    lineTo(8.753f, 9f)
    horizontalLineTo(7.614f)
    lineTo(7.143f, 7.671f)
    horizontalLineTo(5.111f)
    lineTo(4.64f, 9f)
    horizontalLineTo(3.5f)
    lineTo(5.615f, 3f)
    close()
    moveTo(6.895f, 6.711f)
    lineTo(6.127f, 4.431f)
    horizontalLineTo(6.11f)
    lineTo(5.367f, 6.711f)
    horizontalLineTo(6.895f)
    close()
  }
  // Three brand-accent pixels (drawn on top)
  path(fill = SolidColor(pixelColor)) {
    moveTo(0f, 2f)
    horizontalLineToRelative(3f)
    verticalLineToRelative(3f)
    horizontalLineToRelative(-3f)
    close()
  }
  path(fill = SolidColor(pixelColor)) {
    moveTo(11.97f, 1f)
    horizontalLineToRelative(5f)
    verticalLineToRelative(5f)
    horizontalLineToRelative(-5f)
    close()
  }
  path(fill = SolidColor(pixelColor)) {
    moveTo(5.985f, 10f)
    horizontalLineToRelative(5f)
    verticalLineToRelative(5f)
    horizontalLineToRelative(-5f)
    close()
  }
}.build()
