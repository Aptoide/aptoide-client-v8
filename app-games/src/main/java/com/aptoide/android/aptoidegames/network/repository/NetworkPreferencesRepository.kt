package com.aptoide.android.aptoidegames.network.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.aptoide.android.aptoidegames.di.NetworkPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkPreferencesRepository @Inject constructor(
  @NetworkPreferencesDataStore private val dataStore: DataStore<Preferences>,
) {

  companion object PreferencesKeys {
    private val DOWNLOAD_ONLY_OVER_WIFI = booleanPreferencesKey("download_only_over_wifi")
  }

  suspend fun setDownloadOnlyOverWifi(downloadOnlyOverWifi: Boolean) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[DOWNLOAD_ONLY_OVER_WIFI] = downloadOnlyOverWifi
    }
  }

  fun shouldDownloadOnlyOverWifi(): Flow<Boolean> =
    dataStore.data.map { preferences ->
      preferences[DOWNLOAD_ONLY_OVER_WIFI] ?: false
    }
}
