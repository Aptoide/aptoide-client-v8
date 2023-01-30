package cm.aptoide.pt.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import cm.aptoide.pt.home.AppComingSoonRegistrationManager
import rx.Completable
import java.util.concurrent.TimeUnit

class ComingSoonNotificationManager(private val context: Context,
                                    private val appComingSoonPreferencesManager: AppComingSoonRegistrationManager) {

  companion object {
    const val WORKER_TAG = "ComingSoonNotificationWorker"
    const val PACKAGE_NAME = "package_name"
    const val CHANNEL_ID = "coming_soon_notification_channel"
    const val NOTIFICATION_ID = 1994
  }


  fun setupNotification(url: String): Completable {
    return Completable.fromAction {
      setUpChannel()
      setUpWorkRequest(url)
    }.andThen(appComingSoonPreferencesManager.registerUserNotification(url))
  }

  private fun setUpWorkRequest(url: String) {

  }

  private fun setUpChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "Coming Soon notifications"
      val descriptionText = "Coming Soon"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel =
          NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
          }
      val notificationManager: NotificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun cancelScheduledNotification(packageName: String): Completable {
    return Completable.fromAction {
      appComingSoonPreferencesManager.cancelScheduledNotification(packageName)
    }
  }
}