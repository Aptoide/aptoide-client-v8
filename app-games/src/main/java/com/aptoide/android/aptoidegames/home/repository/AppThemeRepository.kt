package com.aptoide.android.aptoidegames.home.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemePreferencesManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

  suspend fun removeIsDarkTheme() {
    dataStore.edit { it.remove(DARK_THEME) }
  }

  suspend fun setIsDarkTheme(isDarkTheme: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[DARK_THEME] = isDarkTheme
    }
  }

  fun isDarkTheme(): Flow<Boolean?> = dataStore.data.map { it[DARK_THEME] }

  private companion object {
    val DARK_THEME = booleanPreferencesKey("dark_theme")
  }
}
