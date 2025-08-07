package cm.aptoide.pt.feature_updates.repository

import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import cm.aptoide.pt.extensions.isMIUI
import cm.aptoide.pt.extensions.isMiuiOptimizationDisabled
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.feature_updates.di.UpdatesPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatesPreferencesRepository @Inject constructor(
  @UpdatesPreferencesDataStore private val dataStore: DataStore<Preferences>,
  private val featureFlags: FeatureFlags
) {

  companion object PreferencesKeys {
    private val AUTO_UPDATE_GAMES = booleanPreferencesKey("auto_update_games")
  }

  suspend fun setAutoUpdateGames(shouldAutoUpdateGames: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[AUTO_UPDATE_GAMES] = shouldAutoUpdateGames
    }
  }

  fun shouldAutoUpdateGames(): Flow<Boolean?> = flow {
    if (shouldHideAutoUpdate() || Build.VERSION.SDK_INT < Build.VERSION_CODES.S || areSilentUpdatesUnsupported()) {
      emit(null)
    } else {
      emitAll(dataStore.data.map { preferences ->
        preferences[AUTO_UPDATE_GAMES] ?: true
      })
    }
  }

  private fun areSilentUpdatesUnsupported() = isMIUI() && !isMiuiOptimizationDisabled()

  private suspend fun shouldHideAutoUpdate(): Boolean {
    val variant = featureFlags.getFlagAsString("updates_notification_type")
    return variant == "auto_update_off" || variant == "generic_only"
  }
}
