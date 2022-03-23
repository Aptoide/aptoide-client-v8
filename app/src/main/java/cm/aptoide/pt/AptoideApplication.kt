package cm.aptoide.pt

import android.app.Application
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

  override fun onCreate() {
    super.onCreate()
    syncInstalledApps()
    initTimber()
  }

  private fun syncInstalledApps() {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        installedAppsRepository.syncInstalledApps()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  private fun initTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }
}