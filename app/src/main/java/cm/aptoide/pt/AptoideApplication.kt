package cm.aptoide.pt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AptoideApplication : Application() {

  override fun onCreate() {
    super.onCreate()
  }
}