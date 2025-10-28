package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
private fun InlineText(
  segments: List<Pair<String, SpanStyle?>>,
  textColor: Color,
) {
  if (segments.isNotEmpty()) {
    val annotated = buildAnnotatedString {
      segments.forEach { (text, style) ->
        if (style != null) withStyle(style) { append(text) } else append(text)
      }
    }
    Text(
      text = annotated,
      style = AGTypography.Chat.copy(color = textColor)
    )
  }
}

@Composable
fun StylizedMessage(
  message: String?,
  @StringRes fallbackResId: Int,
  onLinkClick: (String) -> Unit,
  isUserMessage: Boolean,
) {
  val textToRender = message?.replace("\"", "") ?: stringResource(fallbackResId)
  val paragraphs = remember(textToRender) { textToRender.split("\n\n") }
  val parsedParagraphs = remember(paragraphs) {
    paragraphs.map { paragraphText -> parseStylizedText(paragraphText) }
  }

  val textColor = if (isUserMessage) Palette.Black else Palette.White

  Column {
    parsedParagraphs.forEachIndexed { pIndex, segments ->
      val inlineSegments = mutableListOf<Pair<String, SpanStyle?>>()

      segments.forEach { segment ->
        when (segment) {
          is TextSegment.Plain -> inlineSegments.add(segment.text to null)
          is TextSegment.Bold -> inlineSegments.add(
            segment.text to SpanStyle(fontWeight = FontWeight.Bold)
          )

          is TextSegment.Link -> {
            InlineText(inlineSegments, textColor)
            inlineSegments.clear()
            Spacer(modifier = Modifier.height(4.dp))
            LinkChip(
              text = segment.text,
              onClick = { onLinkClick(segment.url) },
              modifier = Modifier
            )
            Spacer(modifier = Modifier.height(4.dp))
          }
        }
      }

      InlineText(inlineSegments, textColor)

      if (pIndex < parsedParagraphs.lastIndex) {
        Spacer(Modifier.height(8.dp))
      }
    }
  }
}
