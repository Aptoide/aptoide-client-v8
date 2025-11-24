package com.aptoide.android.aptoidegames.play_and_earn.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.aptoide.android.aptoidegames.play_and_earn.di.PaEPreferencesDataStore
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
}
