package cm.aptoide.pt

import android.app.Application
import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class AptoideApplication : Application() {

  @Inject
  lateinit var installManager: InstallManager<String>

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
