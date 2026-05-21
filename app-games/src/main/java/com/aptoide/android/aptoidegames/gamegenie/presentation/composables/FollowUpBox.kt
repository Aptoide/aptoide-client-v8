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
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun FollowUpBox(
  suggestion: String,
  onClick: (String) -> Unit,
) {
  val density = LocalDensity.current
  val textStyle = AGTypography.InputsXSRegular

  val horizontalPaddingPx = with(density) { 16.dp.roundToPx() }

  val boxWidthPx = rememberFollowUpTwoLineWidthPx(
    text = suggestion,
    style = textStyle,
    horizontalPaddingPx = horizontalPaddingPx
  )

  val boxWidthDp = with(density) { boxWidthPx.toDp() + 8.dp }

  Row(
    modifier = Modifier
      .height(48.dp)
      .width(boxWidthDp)
      .background(Palette.Primary.copy(alpha = 0.1f))
      .border(0.5.dp, Palette.Primary)
      .clickable { onClick(suggestion) }
      .padding(horizontal = 8.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
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
private fun rememberFollowUpTwoLineWidthPx(
  text: String,
  style: TextStyle,
  horizontalPaddingPx: Int,
): Int {
  val textMeasurer = rememberTextMeasurer()

  return remember(text, style, horizontalPaddingPx) {
    var low = 0
    var high = 2000
    var result = high

    while (low <= high) {
      val mid = (low + high) / 2

      val layoutResult = textMeasurer.measure(
        text = text,
        style = style,
        constraints = Constraints(
          maxWidth = (mid - horizontalPaddingPx).coerceAtLeast(0)
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
