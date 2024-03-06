package cm.aptoide.pt.app_games.home.domain

import cm.aptoide.pt.app_games.home.repository.ThemePreferencesManager
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class AppThemeUseCase @Inject constructor(private val themePreferencesManager: ThemePreferencesManager) {

  suspend fun setSystemDefault() = themePreferencesManager.removeIsDarkTheme()

  suspend fun setLightTheme() = themePreferencesManager.setIsDarkTheme(false)

  suspend fun setDarkTheme() = themePreferencesManager.setIsDarkTheme(true)

  fun isDarkTheme(): Flow<Boolean?> = themePreferencesManager.isDarkTheme()
}
