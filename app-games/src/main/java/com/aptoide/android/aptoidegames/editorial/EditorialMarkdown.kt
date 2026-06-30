package com.aptoide.android.aptoidegames.editorial

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

// Editorial paragraphs from the new service may contain INLINE markdown only:
// **bold**, *italic*, [label](https://url). There is never block-level markdown or raw HTML.
private val INLINE_MARKDOWN = Regex(
  """\[([^\]]+)]\((https?://[^)\s]+)\)""" + // [label](url)
    """|\*\*([^*]+)\*\*""" + //               **bold**
    """|\*([^*]+)\*""" //                      *italic*
)

/**
 * Renders the supported inline marks as spans. Well-formed marks become styled (and, for
 * links, clickable) spans; anything else is left verbatim, so plain text is unchanged.
 */
fun String.toEditorialAnnotatedString(
  linkColor: Color,
  onLinkClick: (String) -> Unit,
): AnnotatedString {
  val source = this
  return buildAnnotatedString {
    var cursor = 0
    for (match in INLINE_MARKDOWN.findAll(source)) {
      if (match.range.first > cursor) append(source.substring(cursor, match.range.first))
      val (linkLabel, linkUrl, bold, italic) = match.destructured
      when {
        linkUrl.isNotEmpty() -> withLink(
          LinkAnnotation.Clickable(
            tag = linkUrl,
            linkInteractionListener = { onLinkClick(linkUrl) },
          )
        ) {
          withStyle(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)) {
            append(linkLabel)
          }
        }

        bold.isNotEmpty() -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(bold) }

        italic.isNotEmpty() -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(italic) }
      }
      cursor = match.range.last + 1
    }
    if (cursor < source.length) append(source.substring(cursor))
  }
}
