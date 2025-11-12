package com.aptoide.android.aptoidegames.gamesfeed.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideGamesFeedLocalRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
  GamesFeedLocalRepository {
  companion object PreferencesKeys {
    private val GAMES_FEED_VISIBILITY = booleanPreferencesKey("games_feed_visibility")
  }

  override suspend fun saveGamesFeedVisibility(visibility: Boolean) {
    dataStore.edit { it[GAMES_FEED_VISIBILITY] = visibility }
  }

  override suspend fun getGamesFeedVisibility(): Boolean? {
    return dataStore.data
      .map { preferences ->
        preferences[GAMES_FEED_VISIBILITY]
      }.first()
  }
}
