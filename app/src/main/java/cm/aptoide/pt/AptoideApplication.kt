package cm.aptoide.pt

import android.app.Application
import android.util.Log
import cm.aptoide.pt.aptoide_installer.InstallManager
import cm.aptoide.pt.installedapps.data.AptoideInstalledAppsRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class AptoideApplication : Application() {

  @Inject
  lateinit var installedAppsRepository: AptoideInstalledAppsRepository

  @Inject
  lateinit var installManager: InstallManager

  override fun onCreate() {
    super.onCreate()
    initTimber()
    syncInstalledApps()
    startInstallManager()
  }

  private fun startInstallManager() {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        installManager.start()
      } catch (e: Exception) {
        Log.d("lol", "startInstallManager: got error here")
        e.printStackTrace()
        Timber.e(e)
      }
    }
  }

  private fun syncInstalledApps() {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        installedAppsRepository.syncInstalledApps()
      } catch (e: Exception) {
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