package com.aptoide.android.aptoidegames.notifications

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.aptoide.android.aptoidegames.notifications.NotificationsPermissionManager.PreferencesKeys.NOTIFICATION_PERMISSION_REQUESTED
import cm.aptoide.pt.extensions.hasNotificationsPermission
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationsPermissionManager @Inject constructor(
  private val context: Context,
  private val dataStore: DataStore<Preferences>
) {

  private object PreferencesKeys {
    val NOTIFICATION_PERMISSION_REQUESTED =
      booleanPreferencesKey("notification_permission_requested")
  }

  suspend fun updatePermissionRequestedPreference(requestedPermissions: Boolean) {
    dataStore.edit { preferences ->
      preferences[NOTIFICATION_PERMISSION_REQUESTED] = requestedPermissions
    }
  }

  suspend fun hasRequestedPermissions(): Boolean =
    dataStore.data.map { it[NOTIFICATION_PERMISSION_REQUESTED] }.first() ?: false

  @SuppressLint("InlinedApi")
  fun hasNotificationsPermission(): Boolean = context.hasNotificationsPermission()

  fun openAppSystemSettings() = context.openAppSystemSettings()
}

private fun Context.openAppSystemSettings() {
  startActivity(Intent().apply {
    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    putExtra("app_package", packageName)
    putExtra("app_uid", applicationInfo.uid)
    putExtra("android.provider.extra.APP_PACKAGE", packageName)
  })
}
