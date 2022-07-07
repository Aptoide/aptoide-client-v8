package cm.aptoide.aptoideviews.common

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import cm.aptoide.aptoideviews.R

/**
 * Originally from https://github.com/TheCodeYard/EllipsizedTextView
 * Modified to correctly handle text expansion
 */
class EllipsizedTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                   defStyleAttr: Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr) {
  var ellipsis = getDefaultEllipsis().toString()
  var ellipsisColor = getDefaultEllipsisColor()

  private val ellipsisSpannable: SpannableString
  private val spannableStringBuilder = SpannableStringBuilder()
  private var originalText: CharSequence?

  init {
    if (attrs != null) {
      val typedArray =
          context.theme.obtainStyledAttributes(attrs, R.styleable.EllipsizedTextView, 0, 0)
      typedArray?.let {
        ellipsis = typedArray.getString(R.styleable.EllipsizedTextView_ellipsis)
            ?: getDefaultEllipsis().toString()
        ellipsisColor = typedArray.getColor(R.styleable.EllipsizedTextView_ellipsisColor,
            getDefaultEllipsisColor())
        typedArray.recycle()
      }
    }
    originalText = text
    ellipsisSpannable = SpannableString(ellipsis)
    ellipsisSpannable.setSpan(ForegroundColorSpan(ellipsisColor), 0, ellipsis.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val availableScreenWidth =
        measuredWidth - compoundPaddingLeft.toFloat() - compoundPaddingRight.toFloat()
    var availableTextWidth = availableScreenWidth * maxLines
    var ellipsizedText = TextUtils.ellipsize(text, paint, availableTextWidth, ellipsize)

    if (ellipsizedText.toString() != text.toString()) {
      // If the ellipsizedText is different than the original text, this means that it didn't fit and got indeed ellipsized.
      // Calculate the new availableTextWidth by taking into consideration the size of the custom ellipsis, too.
      availableTextWidth = (availableScreenWidth - paint.measureText(ellipsis)) * maxLines
      ellipsizedText = TextUtils.ellipsize(text, paint, availableTextWidth, ellipsize)
      val defaultEllipsisStart = ellipsizedText.indexOf(getDefaultEllipsis())
      val defaultEllipsisEnd = defaultEllipsisStart + 1

      spannableStringBuilder.clear()

      // Update the text with the
      // ellipsized version and replace the default ellipsis with the custom one.
      val ogText = originalText
      text = spannableStringBuilder.append(ellipsizedText)
          .replace(defaultEllipsisStart, defaultEllipsisEnd, ellipsisSpannable)
      originalText = ogText
    } else {
      text = originalText
    }
  }

  override fun setText(text: CharSequence, type: BufferType?) {
    super.setText(text, type)
    originalText = text
  }

  private fun getDefaultEllipsis(): Char {
    return Typography.ellipsis
  }

  private fun getDefaultEllipsisColor(): Int {
    return textColors.defaultColor
  }
}