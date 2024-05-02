package com.aptoide.android.aptoidegames.home.domain

import com.aptoide.android.aptoidegames.home.repository.ThemePreferencesManager
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
