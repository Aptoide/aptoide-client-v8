package com.aptoide.android.aptoidegames.feature_promotional.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import cm.aptoide.pt.appcomingsoon.repository.SubscribedAppsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscribedAppsManager @Inject constructor(private val dataStore: DataStore<Preferences>) :
  SubscribedAppsRepository {

  override suspend fun isAppSubscribed(key: String): Flow<Boolean> {
    return dataStore.data
      .map { preferences ->
        val isSubscribed = preferences[booleanPreferencesKey(key)] ?: false
        isSubscribed
      }
  }

  override suspend fun saveSubscribedApp(packageName: String, subscribe: Boolean) {
    dataStore.edit { it[booleanPreferencesKey(packageName)] = subscribe }
  }
}
