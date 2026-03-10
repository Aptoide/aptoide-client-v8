package com.aptoide.android.aptoidegames.play_and_earn.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.aptoide.android.aptoidegames.play_and_earn.di.PaEPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaEPreferencesRepository @Inject constructor(
  @PaEPreferencesDataStore private val dataStore: DataStore<Preferences>,
) {

  companion object {
    private val HAS_SHOWN_HEADER_BUNDLE = booleanPreferencesKey("has_shown_header_bundle")
    private val HEARTBEAT_INTERVAL_SECONDS = intPreferencesKey("heartbeat_interval_seconds")
    private val PAE_SERVICE_ENABLED = booleanPreferencesKey("pae_service_enabled")
    const val MIN_HEARTBEAT_INTERVAL_SECONDS = 15
  }

  suspend fun hasShownHeaderBundle(): Boolean {
    return dataStore.data.map { preferences ->
      preferences[HAS_SHOWN_HEADER_BUNDLE]
    }.first() ?: false
  }

  suspend fun setHeaderBundleShown() {
    dataStore.edit { preferences ->
      preferences[HAS_SHOWN_HEADER_BUNDLE] = true
    }
  }

  suspend fun getHeartbeatIntervalSeconds(): Int {
    val storedInterval = dataStore.data.map { preferences ->
      preferences[HEARTBEAT_INTERVAL_SECONDS]
    }.first() ?: MIN_HEARTBEAT_INTERVAL_SECONDS
    return storedInterval.coerceAtLeast(MIN_HEARTBEAT_INTERVAL_SECONDS)
  }

  suspend fun setHeartbeatIntervalSeconds(intervalSeconds: Int) {
    dataStore.edit { preferences ->
      preferences[HEARTBEAT_INTERVAL_SECONDS] =
        intervalSeconds.coerceAtLeast(MIN_HEARTBEAT_INTERVAL_SECONDS)
    }
  }

  fun isPaEServiceEnabled(): Flow<Boolean> =
    dataStore.data.map { preferences ->
      preferences[PAE_SERVICE_ENABLED] ?: true
    }

  suspend fun setPaEServiceEnabled(enabled: Boolean) {
    dataStore.edit { preferences ->
      preferences[PAE_SERVICE_ENABLED] = enabled
    }
  }
}
