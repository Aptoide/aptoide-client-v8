package cm.aptoide.pt.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.*
import cm.aptoide.pt.abtesting.experiments.UpdatesNotificationExperiment
import rx.Completable
import java.util.concurrent.TimeUnit

class UpdatesNotificationManager(private val context: Context,
                                 private val updatesNotificationExperiment: UpdatesNotificationExperiment) {

  private lateinit var uploadWorkRequest: PeriodicWorkRequest

  fun setUpNotification(): Completable {
    return updatesNotificationExperiment.getConfiguration()
        .doOnSuccess { config ->
          setUpChannel()
          setUpWorkRequest(config)
        }
        .toCompletable()
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

  private fun setUpWorkRequest(config: String) {
    val data = Data.Builder().putString(CONFIGURATION_KEY, config).build()
    uploadWorkRequest = PeriodicWorkRequestBuilder<UpdatesNotificationWorker>(
        1, TimeUnit.DAYS)
        .setConstraints(getConstraints(config))
        .setInputData(data)
        .build()

    WorkManager
        .getInstance(context)
        .enqueueUniquePeriodicWork(WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, uploadWorkRequest)
  }

  private fun getConstraints(config: String): Constraints {
    when (config) {
      "wifi" -> {
        return Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
      }
      "charge" -> {
        return Constraints.Builder().setRequiresCharging(true).build()
      }
      "wifi_charge", "all" -> {
        return Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true).build()
      }
      "design", "control" -> {
        return Constraints.Builder().build()
      }
      else -> {
        return Constraints.Builder().build()
      }
    }
  }

  companion object {
    private const val WORKER_TAG = "UpdatesNotificationWorker"
    const val CHANNEL_ID = "updates_notification_channel"
    const val UPDATE_NOTIFICATION_ID = 123
    const val CONFIGURATION_KEY = "config"
  }

}