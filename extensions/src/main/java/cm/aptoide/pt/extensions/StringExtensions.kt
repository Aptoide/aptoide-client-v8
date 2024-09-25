package cm.aptoide.pt.extensions

import android.text.Spannable
import android.text.Spanned
import android.text.style.StrikethroughSpan
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.parseDate(pattern: String = "yyyy-MM-dd HH:mm:ss"): Date? = try {
  SimpleDateFormat(pattern, Locale.getDefault()).parse(this)!!
} catch (t: Throwable) {
  t.printStackTrace()
  null
}

fun String.isYoutubeURL(): Boolean {
  val pattern = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+"
  return matches(Regex(pattern))
}

fun String.extractVideoId(): String {
  // Ensure the URL starts with a valid scheme
  val url = if (this.lowercase().startsWith("http")) {
    this.toUri()
  } else {
    "https://$this".toUri()
  }

  // Try to extract the video ID from the query parameters (removes / for the video to work if needed)
  if (url.query != null) {
    if (url.getQueryParameter("v") != null)
      return url.getQueryParameter("v")!!.replace("/", "")
  }

  // If not found in query parameters, try the path component
  return url.lastPathSegment ?: ""
}

fun String.getTextSpans(): Pair<Spanned, List<TextSpan>> {
  val textSpans = mutableListOf<TextSpan>()
  val htmlText =
    HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY, null) { opening, tag, output, _ ->
      if (tag.matches(Regex("_\\d+"))) {
        val len: Int = output.length
        if (opening) {
          output.setSpan(StrikethroughSpan(), len, len, Spannable.SPAN_MARK_MARK)
        } else {
          val obj = output.getSpans<StrikethroughSpan>(0, output.length)
            .lastOrNull { output.getSpanFlags(it) == Spannable.SPAN_MARK_MARK }
          val where = output.getSpanStart(obj)
          textSpans.add(
            TextSpan(
              pos = tag.removePrefix("_").toInt(),
              start = where,
              end = len,
              text = output.substring(where, len)
            )
          )
          output.removeSpan(obj)
        }
      }
    }

  return htmlText to textSpans
}

fun String.toAnnotatedString(spanStyle: SpanStyle) = this.getTextSpans().let {
  buildAnnotatedString {
    append(it.first)
    it.second.forEach {
      addStyle(
        style = spanStyle,
        start = it.start,
        end = it.end
      )
    }
  }
}

class TextSpan(
  val pos: Int,
  val start: Int,
  val end: Int,
  val text: String,
)
