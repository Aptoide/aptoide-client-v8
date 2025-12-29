package com.aptoide.android.aptoidegames.launch

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.aptoide.android.aptoidegames.launch.AppLaunchPreferencesManager.PreferencesKeys.IS_FIRST_LAUNCH
import com.aptoide.android.aptoidegames.launch.AppLaunchPreferencesManager.PreferencesKeys.SHOULD_SHOW_NOTIFICATIONS_DIALOG
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLaunchPreferencesManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

  private var isFirstLaunch: Boolean? = null

  private object PreferencesKeys {
    val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    val SHOULD_SHOW_NOTIFICATIONS_DIALOG = booleanPreferencesKey("should_show_notifications_dialog")
  }

  suspend fun setIsNotFirstLaunch() {
    dataStore.edit { it[IS_FIRST_LAUNCH] = false }
  }

  suspend fun isFirstLaunch(): Boolean {
    if (isFirstLaunch == null) {
      isFirstLaunch = dataStore.data
        .map { it[IS_FIRST_LAUNCH] ?: true }
        .first()
    }
    return isFirstLaunch!!
  }

  suspend fun setNotificationDialogShown() {
    dataStore.edit { it[SHOULD_SHOW_NOTIFICATIONS_DIALOG] = false }
  }

  suspend fun shouldShowNotificationsDialog(): Boolean = dataStore.data
    .map { preferences ->
      (preferences[IS_FIRST_LAUNCH] == false) && (preferences[SHOULD_SHOW_NOTIFICATIONS_DIALOG]
        ?: true)
    }.first()
}
