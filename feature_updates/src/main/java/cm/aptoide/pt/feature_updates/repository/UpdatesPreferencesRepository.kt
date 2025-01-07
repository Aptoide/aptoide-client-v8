package cm.aptoide.pt.feature_updates.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import cm.aptoide.pt.feature_updates.di.UpdatesPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatesPreferencesRepository @Inject constructor(
  @UpdatesPreferencesDataStore private val dataStore: DataStore<Preferences>,
) {

  companion object PreferencesKeys {
    private val AUTO_UPDATE_GAMES = booleanPreferencesKey("auto_update_games")
  }

  suspend fun setAutoUpdateGames(shouldAutoUpdateGames: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[AUTO_UPDATE_GAMES] = shouldAutoUpdateGames
    }
  }

  fun shouldAutoUpdateGames(): Flow<Boolean> =
    dataStore.data.map { preferences ->
      preferences[AUTO_UPDATE_GAMES] ?: true
    }
}
