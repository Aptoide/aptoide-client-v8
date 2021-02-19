package cm.aptoide.aptoideviews.common

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.text.HtmlCompat

fun String.formatWithHtmlImage(@DrawableRes drawableId: Int, width: Int, height: Int,
                               resources: Resources): CharSequence {
  val image = "<img width='${width}px' height='${height}px' src='${drawableId}'/>"
  return HtmlCompat.fromHtml(this.format(image), HtmlCompat.FROM_HTML_MODE_LEGACY, getImageGetter(resources), null)
}

private fun getImageGetter(resources: Resources): Html.ImageGetter {
  return Html.ImageGetter { source: String ->
    var drawable: Drawable? = null
    try {
      drawable = resources.getDrawable(source.toInt())
      drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    } catch (e: Resources.NotFoundException) {
      Log.e("FormatWithHtmlImage", "Image not found. Check the ID.", e)
    } catch (e: NumberFormatException) {
      Log.e("FormatWithHtmlImage", "Source string not a valid resource ID.", e)
    }
    drawable
  }
}