package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun StylizedMessage(
  message: String?,
  @StringRes fallbackResId: Int,
  onLinkClick: (String) -> Unit,
  isUserMessage: Boolean,
) {
  val textToRender = message?.replace("\"", "") ?: stringResource(fallbackResId)
  val segments = remember(textToRender) { parseStylizedText(textToRender) }

  val textColor = if (isUserMessage) Palette.Black else Palette.White

  // Build annotated string with URL annotations
  val annotatedString = remember(segments) {
    AnnotatedString.Builder().apply {
      segments.forEach { segment ->
        when (segment) {
          is TextSegment.Plain -> append(segment.text)
          is TextSegment.Bold -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(segment.text)
          }

          is TextSegment.Link -> {
            val start = length
            append(segment.text)
            val end = length
            addStyle(
              SpanStyle(
                color = Palette.Primary,
                textDecoration = TextDecoration.Underline
              ), start, end
            )
            addStringAnnotation(
              tag = "URL",
              annotation = segment.url,
              start = start,
              end = end
            )
          }
        }
      }
    }.toAnnotatedString()
  }

  var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

  BasicText(
    text = annotatedString,
    style = AGTypography.Chat.copy(color = textColor),
    onTextLayout = { textLayoutResult = it },
    modifier = Modifier.pointerInput(Unit) {
      detectTapGestures { offset ->
        textLayoutResult?.let { layoutResult ->
          val position = layoutResult.getOffsetForPosition(offset)
          annotatedString.getStringAnnotations(tag = "URL", start = position, end = position)
            .firstOrNull()?.let { annotation ->
              onLinkClick(annotation.item)
            }
        }
      }
    }
  )
}
