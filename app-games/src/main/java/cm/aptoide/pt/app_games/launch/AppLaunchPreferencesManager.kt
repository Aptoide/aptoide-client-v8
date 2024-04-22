package cm.aptoide.pt.app_games.launch

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import cm.aptoide.pt.app_games.launch.AppLaunchPreferencesManager.PreferencesKeys.SHOULD_SHOW_NOTIFICATIONS_DIALOG
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLaunchPreferencesManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

  private object PreferencesKeys {
    val SHOULD_SHOW_NOTIFICATIONS_DIALOG = booleanPreferencesKey("should_show_notifications_dialog")
  }

  suspend fun setNotificationDialogShown() {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[SHOULD_SHOW_NOTIFICATIONS_DIALOG] = false
    }
  }

  suspend fun shouldShowNotificationsDialog(): Boolean =
    dataStore.data.map { preferences -> (preferences[SHOULD_SHOW_NOTIFICATIONS_DIALOG]
        ?: true)
    }.first()
}
