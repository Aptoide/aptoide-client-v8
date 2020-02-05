package cm.aptoide.pt.themes

import android.app.Activity
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.util.TypedValue
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate.*
import cm.aptoide.pt.BuildConfig

/**
 * Responsible for managing the theme for the application.
 *
 * The Aptoide themes are built in combination of two layers:
 *  - Base theme (e.g. default, red, green, etc.)
 *  - Light/Dark variants of the base theme
 *
 * Unless specified, a "theme" refers to the combination of both layers (e.g. red-light, red-dark...)
 *
 * The base theme is used in two contexts:
 *  - The overall Aptoide app look & feel, set in BuildConfig on compile-time
 *  - Stores themes, which overrides the overall base theme when the user enters a store page
 *
 *  @see StoreTheme
 */
class ThemeManager(private val activity: Activity,
                   private val sharedPreferences: SharedPreferences) {

  private var THEME_PREFERENCE_KEY: String = "app_theme_preference"

  /**
   * Sets the current active theme.
   * If the theme does not exist or could not be found, the default theme will be used instead.
   */
  fun setTheme(theme: String?) {
    val storeTheme = StoreTheme.get(theme, isThemeDark())
    setDefaultNightMode(getThemeOption())
    activity.setTheme(storeTheme.themeResource)
    setStatusBarThemeColor(theme)
  }

  /**
   * Resets the currently active theme to the default theme (set in BuildConfig on compile-time)
   */
  fun resetToBaseTheme() {
    setTheme(BuildConfig.APTOIDE_THEME)
  }

  fun getStoreTheme(storeThemeName: String?): StoreTheme {
    return StoreTheme.get(storeThemeName, isThemeDark())
  }

  fun getBaseTheme(): StoreTheme {
    return StoreTheme.get(BuildConfig.APTOIDE_THEME, isThemeDark())
  }

  /**
   * Retrieves the specified attribute value for the currently active theme.
   *
   * @return a TypedValue of the specified attribute
   */
  fun getAttributeForTheme(attributeResourceId: Int): TypedValue {
    val value = TypedValue()
    activity.theme.resolveAttribute(attributeResourceId, value, true)
    return value
  }

  /**
   * Retrieves the specified attribute value for the specified theme.
   * If the theme does not exist or could not be found, the default theme will be used instead.
   *
   * @return a TypedValue of the specified attribute
   */
  fun getAttributeForTheme(themeName: String?, attributeResourceId: Int): TypedValue {
    return activity.theme.obtainStyledAttributes(
        StoreTheme.get(themeName, isThemeDark()).themeResource,
        intArrayOf(attributeResourceId)).peekValue(0)
  }

  fun resetStatusBarColor() {
    if (Build.VERSION.SDK_INT >= 21) {
      setStatusBarThemeColor(getAttributeForTheme(android.R.attr.statusBarColor).data)
    }
  }

  fun setStatusBarThemeColor(theme: String?) {
    if (Build.VERSION.SDK_INT >= 21) {
      setStatusBarThemeColor(getAttributeForTheme(theme, android.R.attr.statusBarColor).data)
    }
  }

  fun setStatusBarThemeColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= 21) {
      val window = activity.window
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
      window.statusBarColor = color
    }
  }

  enum class ThemeOption {
    SYSTEM_DEFAULT, LIGHT, DARK
  }

  fun setThemeOption(themeOption: ThemeOption) {
    sharedPreferences.edit().putInt(THEME_PREFERENCE_KEY, themeOption.ordinal).apply()
  }

  private fun setDefaultNightMode(themeOption: ThemeOption) {
    when (themeOption) {
      ThemeOption.SYSTEM_DEFAULT -> setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
      ThemeOption.LIGHT -> setDefaultNightMode(MODE_NIGHT_NO)
      ThemeOption.DARK -> setDefaultNightMode(MODE_NIGHT_YES)
    }
  }

  fun isThemeDark(): Boolean {
    val option = getThemeOption()
    return option == ThemeOption.DARK ||
        (option == ThemeOption.SYSTEM_DEFAULT
            && activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
  }

  fun getThemeOption(): ThemeOption {
    return ThemeOption.values()[sharedPreferences.getInt(THEME_PREFERENCE_KEY, 0)]
  }

}