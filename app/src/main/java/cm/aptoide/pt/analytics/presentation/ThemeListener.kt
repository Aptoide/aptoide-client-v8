package cm.aptoide.pt.analytics.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.settings.presentation.themePreferences

@Composable
fun ThemeListener(content: @Composable () -> Unit) {
  val isDarkTheme = themePreferences(key = "settingsDarkTheme").first
  val darkTheme = isDarkTheme ?: isSystemInDarkTheme()
  val analytics = hiltViewModel<AnalyticsViewModel>()

  LaunchedEffect(key1 = darkTheme) {
    analytics.setUserProperties(isDarkTheme = darkTheme)
  }

  content()
}
