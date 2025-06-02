package com.aptoide.android.aptoidegames.developer

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.aptoide.android.aptoidegames.developer.di.AGDeveloperPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AGDeveloperPreferencesRepository @Inject constructor(
  @AGDeveloperPreferencesDataStore private val dataStore: DataStore<Preferences>,
) {

  companion object PreferencesKeys {
    private val AG_DEVELOPER_OPTIONS_ENABLED = booleanPreferencesKey("ag_developer_options_enabled")
  }

  suspend fun setAGDeveloperOptionsState(enabled: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[AG_DEVELOPER_OPTIONS_ENABLED] = enabled
    }
  }

  fun areAGDeveloperOptionsEnabled(): Flow<Boolean> {
    return dataStore.data.map { preferences ->
      preferences[AG_DEVELOPER_OPTIONS_ENABLED] == true
    }
  }
}
