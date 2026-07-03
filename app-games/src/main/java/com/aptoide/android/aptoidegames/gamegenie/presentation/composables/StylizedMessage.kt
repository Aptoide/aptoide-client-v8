package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

private const val STREAM_FADE_DURATION_MS = 220

private data class InlineSegment(
  val text: String,
  val style: SpanStyle?,
  val globalStart: Int,
)

@Composable
private fun InlineText(
  segments: List<InlineSegment>,
  textColor: Color,
  fadeStartOffset: Int,
  fadeAlpha: Float,
) {
  if (segments.isEmpty()) return
  val annotated = buildAnnotatedString {
    segments.forEach { seg ->
      val localStart = length
      if (seg.style != null) withStyle(seg.style) { append(seg.text) } else append(seg.text)
      val localEnd = length
      val segGlobalEnd = seg.globalStart + seg.text.length
      if (fadeAlpha < 1f && segGlobalEnd > fadeStartOffset) {
        val fadeFromGlobal = maxOf(seg.globalStart, fadeStartOffset)
        val localFadeStart = localStart + (fadeFromGlobal - seg.globalStart)
        addStyle(
          SpanStyle(color = textColor.copy(alpha = fadeAlpha)),
          localFadeStart,
          localEnd
        )
      }
    }
  }
  Text(
    text = annotated,
    style = AGTypography.Chat.copy(color = textColor)
  )
}

@Composable
fun StylizedMessage(
  message: String?,
  @StringRes fallbackResId: Int,
  onLinkClick: (String) -> Unit,
  isUserMessage: Boolean,
  isStreaming: Boolean = false,
) {
  val rawText = message?.replace("\"", "") ?: stringResource(fallbackResId)
  val textToRender = if (isStreaming) stripPartialTrailingMarkdown(rawText) else rawText
  val paragraphs = remember(textToRender) { textToRender.split("\n\n") }
  val parsedParagraphs = remember(paragraphs) {
    paragraphs.map { paragraphText -> parseStylizedText(paragraphText) }
  }

  val totalVisibleLength = remember(parsedParagraphs) {
    parsedParagraphs.sumOf { segs ->
      segs.sumOf { seg ->
        when (seg) {
          is TextSegment.Plain -> seg.text.length
          is TextSegment.Bold -> seg.text.length
          is TextSegment.Link -> 0
        }
      }
    }
  }

  var stableLength by remember {
    mutableIntStateOf(if (isStreaming) 0 else Int.MAX_VALUE)
  }
  val tailAlpha = remember { Animatable(if (isStreaming) 0f else 1f) }

  LaunchedEffect(isStreaming) {
    if (!isStreaming) {
      stableLength = Int.MAX_VALUE
      tailAlpha.snapTo(1f)
    }
  }

  LaunchedEffect(totalVisibleLength, isStreaming) {
    if (!isStreaming) return@LaunchedEffect
    if (totalVisibleLength > stableLength) {
      tailAlpha.animateTo(1f, tween(durationMillis = STREAM_FADE_DURATION_MS))
      stableLength = totalVisibleLength
      tailAlpha.snapTo(0f)
    }
  }

  val textColor = if (isUserMessage && BuildConfig.FLAVOR_brand != "vanilla") {
    Palette.Black
  } else {
    Palette.White
  }
  val fadeStartOffset = stableLength
  val fadeAlpha = tailAlpha.value

  Column {
    var visibleOffset = 0
    parsedParagraphs.forEachIndexed { pIndex, segments ->
      val inlineSegments = mutableListOf<InlineSegment>()

      segments.forEach { segment ->
        when (segment) {
          is TextSegment.Plain -> {
            inlineSegments.add(InlineSegment(segment.text, null, visibleOffset))
            visibleOffset += segment.text.length
          }

          is TextSegment.Bold -> {
            inlineSegments.add(
              InlineSegment(
                segment.text,
                SpanStyle(fontWeight = FontWeight.Bold),
                visibleOffset
              )
            )
            visibleOffset += segment.text.length
          }

          is TextSegment.Link -> {
            InlineText(inlineSegments, textColor, fadeStartOffset, fadeAlpha)
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

      InlineText(inlineSegments, textColor, fadeStartOffset, fadeAlpha)

      if (pIndex < parsedParagraphs.lastIndex) {
        Spacer(Modifier.height(8.dp))
      }
    }
  }
}
