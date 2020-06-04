package cm.aptoide.aptoideviews.common

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.SpannableStringBuilder
import android.text.style.ReplacementSpan
import androidx.core.text.inSpans
import kotlin.math.roundToInt


inline fun SpannableStringBuilder.nonBreakingSpan(
    builderAction: SpannableStringBuilder.() -> Unit) =
    inSpans(NonbreakingSpan(), builderAction = builderAction)

class NonbreakingSpan : ReplacementSpan() {

  override fun draw(
      canvas: Canvas,
      text: CharSequence, start: Int, end: Int,
      x: Float, top: Int, y: Int, bottom: Int,
      paint: Paint) {
    canvas.drawText(text, start, end, x, y.toFloat(), paint)
  }

  override fun getSize(
      paint: Paint,
      text: CharSequence?, start: Int, end: Int,
      fm: FontMetricsInt?): Int {
    return paint.measureText(text, start, end).roundToInt()
  }
}