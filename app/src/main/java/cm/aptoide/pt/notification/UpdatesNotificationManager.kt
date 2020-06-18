package cm.aptoide.pt.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

class UpdatesNotificationManager(private val context: Context) {

  private lateinit var uploadWorkRequest: WorkRequest

  fun setUpNotification() {
    setUpChannel()
    setUpWorkRequest()
  }

  private fun setUpChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "channel_name"
      val descriptionText = "channel_text"
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
    val constraints = Constraints.Builder()
        //.setRequiresCharging(true)
        //.setRequiredNetworkType(NetworkType.UNMETERED)
        .build()
    Log.d("MainActivity", "constraints")

    uploadWorkRequest = PeriodicWorkRequestBuilder<UpdatesNotificationWorker>(
        15, TimeUnit.MINUTES)
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    WorkManager
        .getInstance(context)
        .enqueue(uploadWorkRequest)
  }

  companion object {
    const val CHANNEL_ID = "channel_id"
    const val NOTIFICATION_ID = 666
  }

}