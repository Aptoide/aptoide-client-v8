package cm.aptoide.pt.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.*
import cm.aptoide.pt.home.AppComingSoonPreferencesManager
import rx.Completable
import java.util.concurrent.TimeUnit

class ComingSoonNotificationManager(private val context: Context,
                                    private val appComingSoonPreferencesManager: AppComingSoonPreferencesManager) {

  companion object {
    const val WORKER_TAG = "ComingSoonNotificationWorker"
    const val PACKAGE_NAME = "package_name"
    const val CHANNEL_ID = "coming_soon_notification_channel"
    const val NOTIFICATION_ID = 1994
  }

  private lateinit var comingSoonWorkRequest: PeriodicWorkRequest

  fun setupNotification(url: String): Completable {
    return Completable.fromAction {
      setUpChannel()
      setUpWorkRequest(url)
      appComingSoonPreferencesManager.registerUserNotification(url)
    }
  }

  private fun setUpWorkRequest(url: String) {

    val data: Data.Builder = Data.Builder()
    data.putString(PACKAGE_NAME, url)

    comingSoonWorkRequest = PeriodicWorkRequestBuilder<ComingSoonNotificationWorker>(
        1, TimeUnit.DAYS)
        .addTag(WORKER_TAG + url)
        .setInputData(data.build())
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            WORKER_TAG + url, ExistingPeriodicWorkPolicy.KEEP,
            comingSoonWorkRequest)
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
      WorkManager.getInstance(context)
          .cancelAllWorkByTag(WORKER_TAG + packageName)
    }

  }

}