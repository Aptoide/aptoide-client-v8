package cm.aptoide.pt.themes

enum class DarkThemeMode {
  LIGHT, DARK, SYSTEM_LIGHT, SYSTEM_DARK;

  fun isDark(): Boolean {
    return this == DARK || this == SYSTEM_DARK
  }

}
