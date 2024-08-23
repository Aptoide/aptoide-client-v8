package com.aptoide.android.aptoidegames.permissions

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPermissionsManager @Inject constructor(
  private val dataStore: DataStore<Preferences>,
) {

  suspend fun hasRequestedPermission(permission: String): Boolean =
    dataStore.data.map { it[booleanPreferencesKey("${permission}_requested")] }.first() ?: false

  suspend fun setPermissionRequested(permission: String) {
    dataStore.edit { preferences ->
      preferences[booleanPreferencesKey("${permission}_requested")] = true
    }
  }
}
