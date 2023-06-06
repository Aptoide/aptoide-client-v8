package cm.aptoide.pt

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

val Context.userProfileDataStore: DataStore<Preferences> by preferencesDataStore(name = "userProfile")

@HiltAndroidApp
class AptoideApplication : Application() {

  @Inject
  lateinit var installManager: InstallManager

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
        e.printStackTrace()
        Timber.e(e)
      }
    }
  }

  private fun initTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }
}
