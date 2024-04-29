package cm.aptoide.pt.app_games

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import cm.aptoide.pt.app_games.installer.notifications.InstallerNotificationsManager
import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "permissions")
val Context.userFeatureFlagsDataStore: DataStore<Preferences> by preferencesDataStore(name = "userFeatureFlags")
val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "themePreferences")
val Context.networkPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "networkPreferences")
val Context.appLaunchDataStore: DataStore<Preferences> by preferencesDataStore(name = "appLaunch")

@HiltAndroidApp
class AptoideApplication : Application() {

  @Inject
  lateinit var installManager: InstallManager

  @Inject
  lateinit var installerNotificationsManager: InstallerNotificationsManager

  override fun onCreate() {
    super.onCreate()
    initTimber()
    startInstallManager()
  }

  private fun startInstallManager() {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        installManager.restore()
      } catch (e: Exception) {
        Timber.e(e)
        e.printStackTrace()
      }

      installerNotificationsManager.initialize()
    }
  }

  private fun initTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }
}
