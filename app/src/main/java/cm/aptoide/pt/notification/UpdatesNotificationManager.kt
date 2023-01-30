package cm.aptoide.pt.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import rx.Completable
import java.util.concurrent.TimeUnit

class UpdatesNotificationManager(private val context: Context) {

//  private lateinit var updatesWorkRequest: PeriodicWorkRequest

  fun setUpNotification(): Completable {
    return Completable.fromAction {
      setUpChannel()
      setUpWorkRequest()
    }
  }


  private fun setUpChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "Updates notifications"
      val descriptionText = "Updates"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
      }
      // Register the channel with the system
      val notificationManager: NotificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  private fun setUpWorkRequest() {
//    updatesWorkRequest = PeriodicWorkRequestBuilder<UpdatesNotificationWorker>(
//        1, TimeUnit.DAYS)
//        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build())
//        .build()
//
//    WorkManager
//        .getInstance(context)
//        .enqueueUniquePeriodicWork(WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, updatesWorkRequest)
  }

  companion object {
    private const val WORKER_TAG = "UpdatesNotificationWorker"
    const val CHANNEL_ID = "updates_notification_channel"
    const val UPDATE_NOTIFICATION_ID = 123
  }

}