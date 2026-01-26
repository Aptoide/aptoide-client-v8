package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import cm.aptoide.pt.extensions.toAnnotatedString
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun OverlayLaunchButton(
  onClick: () -> Unit,
  showTooltip: Boolean,
  modifier: Modifier = Modifier
) {
  val density = LocalDensity.current
  val tooltipText = stringResource(R.string.gamegenie_companion_play_smarter)
  val textStyle = AGTypography.Chat
  val horizontalPaddingPx = with(density) { 48.dp.roundToPx() }

  val boxWidthPx = rememberTwoLineTextWidthPx(
    text = tooltipText,
    style = textStyle,
    spacingPx = horizontalPaddingPx
  )
  val boxWidthDp = with(density) { boxWidthPx.toDp() }

  Box(
    modifier = modifier.size(48.dp)
  ) {
    Box(
      modifier = Modifier
        .size(48.dp)
        .background(Palette.Secondary)
        .clickable { onClick() },
      contentAlignment = Alignment.Center
    ) {
      Icon(
        painter = painterResource(R.drawable.companion_arrow_forward),
        contentDescription = "Launch overlay",
        tint = Palette.White,
        modifier = Modifier.size(24.dp)
      )

      AnimationComposable(
        modifier = Modifier.size(48.dp),
        resId = R.raw.game_genie_launch_overlay_small
      )
    }

    if (showTooltip) {
      Popup(
        alignment = Alignment.TopEnd,
        offset = with(density) {
          IntOffset(
            x = 0,
            y = (-60).dp.roundToPx()
          )
        },
        properties = PopupProperties(clippingEnabled = false)
      ) {
        Column(
          horizontalAlignment = Alignment.End
        ) {
          Box(
            modifier = Modifier
              .width(boxWidthDp)
              .background(Palette.Secondary),
            contentAlignment = Alignment.Center
          ) {
            val styledText = tooltipText.toAnnotatedString(
              SpanStyle(fontWeight = FontWeight.Bold, color = Palette.White)
            )
            Text(
              text = styledText,
              style = textStyle,
              textAlign = TextAlign.End,
              minLines = 2,
              maxLines = 2,
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
          }
          Box(
            modifier = Modifier
              .width(48.dp)
              .height(8.dp)
              .background(Palette.Secondary),
          )
        }
      }
    }
  }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
private fun OverlayLaunchButtonPreview() {
  AptoideTheme {
    Box(modifier = Modifier.padding(16.dp)) {
      OverlayLaunchButton(
        onClick = {},
        showTooltip = true
      )
    }
  }
}

@Composable
private fun rememberTwoLineTextWidthPx(
  text: String,
  style: TextStyle,
  spacingPx: Int
): Int {
  val textMeasurer = rememberTextMeasurer()

  return remember(text, spacingPx) {
    // Strip HTML-like tags (e.g., <1>, </1>, <b>, </b>) for accurate measurement
    val cleanText = text.replace(Regex("</?\\d+>|</?[a-zA-Z]+>"), "")

    var low = 0
    var high = 2000
    var result = high

    while (low <= high) {
      val mid = (low + high) / 2

      val layoutResult = textMeasurer.measure(
        text = cleanText,
        style = style,
        constraints = Constraints(
          maxWidth = mid
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

    result + spacingPx
  }
}
