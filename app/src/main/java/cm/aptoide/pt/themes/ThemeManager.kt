package cm.aptoide.pt.themes

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.util.TypedValue
import android.view.WindowManager
import androidx.annotation.ColorInt
import cm.aptoide.pt.BuildConfig


class ThemeManager(private val activity: Activity) {

  fun getAttributeForTheme(attributeResourceId: Int): TypedValue {
    val value = TypedValue()
    activity.theme.resolveAttribute(attributeResourceId, value, true)
    return value
  }

  fun setTheme(theme: String?) {
    val storeTheme = StoreTheme.get(theme, isThemeDark())
    activity.setTheme(storeTheme.themeResource)
    setStatusBarThemeColor(theme)
  }

  fun getAttributeForTheme(themeName: String?, attributeResourceId: Int): TypedValue {
    return activity.theme.obtainStyledAttributes(
        StoreTheme.get(themeName, isThemeDark()).themeResource,
        intArrayOf(attributeResourceId)).peekValue(0)
  }

  fun getStoreTheme(storeThemeName: String?): StoreTheme {
    return StoreTheme.get(storeThemeName, isThemeDark())
  }

  fun getBaseTheme(): StoreTheme {
    return StoreTheme.get(BuildConfig.APTOIDE_THEME, isThemeDark())
  }

  fun resetToBaseTheme() {
    setTheme(BuildConfig.APTOIDE_THEME)
  }

  fun resetStatusBarColor() {
    setStatusBarThemeColor(getAttributeForTheme(android.R.attr.statusBarColor).data)
  }

  fun setStatusBarThemeColor(theme: String?) {
    setStatusBarThemeColor(getAttributeForTheme(theme, android.R.attr.statusBarColor).data)
  }

  fun setStatusBarThemeColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= 21) {
      val window = activity.window
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
      window.statusBarColor = color
    }
  }

  private fun isThemeDark(): Boolean {
    return activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
  }

}