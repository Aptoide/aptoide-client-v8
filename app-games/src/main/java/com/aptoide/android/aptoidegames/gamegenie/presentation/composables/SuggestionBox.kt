package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun SuggestionBox(
  suggestion: String,
  onClick: (String, Int) -> Unit,
  index: Int,
  emoji: String? = null,
) {
  val density = LocalDensity.current
  val textStyle = AGTypography.Body
  val emojiFontSize = 16.sp

  val emojiWidthPx = rememberEmojiWidthPx(emoji, emojiFontSize)
  val emojiSpacingPx = with(density) { 8.dp.roundToPx() }
  val horizontalPaddingPx = with(density) { 24.dp.roundToPx() }

  val boxWidthPx = rememberTwoLineTextWidthPx(
    text = suggestion,
    style = textStyle,
    emojiWidthPx = emojiWidthPx,
    spacingPx = emojiSpacingPx + horizontalPaddingPx
  )

  val boxWidthDp = with(density) { boxWidthPx.toDp() }

  Row(
    modifier = Modifier
      .height(56.dp)
      .width(boxWidthDp)
      .background(Palette.Primary.copy(alpha = 0.1f))
      .border(2.dp, Palette.Primary)
      .clickable { onClick(suggestion, index) }
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    emoji?.let {
      Text(
        text = it,
        fontSize = emojiFontSize,
        modifier = Modifier.padding(end = 8.dp)
      )
    }

    Text(
      text = suggestion,
      style = textStyle,
      color = Palette.Primary,
      minLines = 2,
      maxLines = 2
    )
  }
}

@Composable
private fun rememberTwoLineTextWidthPx(
  text: String,
  style: TextStyle,
  emojiWidthPx: Int,
  spacingPx: Int
): Int {
  val textMeasurer = rememberTextMeasurer()

  return remember(text, emojiWidthPx) {
    var low = 0
    var high = 2000
    var result = high

    while (low <= high) {
      val mid = (low + high) / 2

      val layoutResult = textMeasurer.measure(
        text = text,
        style = style,
        constraints = Constraints(
          maxWidth = mid - emojiWidthPx - spacingPx
        )
      )

      if (layoutResult.lineCount > 2) {
        low = mid + 1
      } else {
        if (layoutResult.lineCount == 2) {
          result = mid
        }
        high = mid - 1
      }
    }

    result
  }
}


@Composable
private fun rememberEmojiWidthPx(
  emoji: String?,
  fontSize: TextUnit,
): Int {
  if (emoji == null) return 0

  val measurer = rememberTextMeasurer()

  return remember(emoji, fontSize) {
    measurer.measure(
      text = emoji,
      style = TextStyle(fontSize = fontSize)
    ).size.width
  }
}
