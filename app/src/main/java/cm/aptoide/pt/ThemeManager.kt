package cm.aptoide.pt

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.util.TypedValue
import android.view.WindowManager
import androidx.annotation.ColorInt
import cm.aptoide.pt.store.StoreTheme

class ThemeManager(private val activity: Activity) {

  fun getAttributeForTheme(attributeResourceId: Int): TypedValue {
    val value = TypedValue()
    activity.theme.resolveAttribute(attributeResourceId, value, true)
    return value
  }

  fun setTheme(theme: String) {
    val hasDarkMode =
        activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val storeTheme = StoreTheme.get(theme, hasDarkMode)
    activity.setTheme(storeTheme.themeResource)
    setStatusBarThemeColor(getAttributeForTheme(android.R.attr.statusBarColor).data)
  }

  fun resetToBaseTheme() {
    setTheme(BuildConfig.APTOIDE_THEME)
  }

  fun resetStatusBarColor() {
    setStatusBarThemeColor(getAttributeForTheme(android.R.attr.statusBarColor).data)
  }

  fun setStatusBarThemeColor(theme: String) {
    val storeTheme = StoreTheme.get(theme)
    setStatusBarThemeColor(activity.resources.getColor(storeTheme.darkerColor))
  }

  fun setStatusBarThemeColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= 21) {
      val window = activity.window
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
      window.statusBarColor = color
    }
  }

}