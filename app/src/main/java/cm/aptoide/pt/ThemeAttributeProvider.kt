package cm.aptoide.pt

import android.content.res.Resources.Theme
import android.util.TypedValue

class ThemeAttributeProvider(private val theme: Theme) {

  fun getAttributeForTheme(attributeResourceId: Int): TypedValue {
    val value = TypedValue()
    theme.resolveAttribute(attributeResourceId, value, true)
    return value
  }

}