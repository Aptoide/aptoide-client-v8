package com.aptoide.android.aptoidegames

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideIdsRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
  IdsRepository {

  override suspend fun getId(key: String): String {
    return dataStore.data
      .map { preferences ->
        preferences[stringPreferencesKey(key)] ?: ""
      }.first()
  }

  override suspend fun saveId(key: String, id: String) {
    dataStore.edit { it[stringPreferencesKey(key)] = id }
  }
}
