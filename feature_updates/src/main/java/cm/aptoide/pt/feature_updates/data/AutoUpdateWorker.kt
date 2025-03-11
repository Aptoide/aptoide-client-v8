package cm.aptoide.pt.feature_updates.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.NetworkType.UNMETERED
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import cm.aptoide.pt.feature_updates.domain.Updates
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import java.util.concurrent.TimeUnit

@HiltWorker
class AutoUpdateWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted workerParams: WorkerParameters,
  private val updates: Updates,
) : CoroutineWorker(appContext, workerParams) {

  override suspend fun doWork(): Result {
    if (runAttemptCount > 1) {
      return Result.failure()
    }

    if (!isInternetAvailable()) {
      return Result.retry()
    } else {
      updates.autoUpdate()
      return Result.success()
    }
  }

  companion object {
    private const val WORK_NAME = "AutoUpdateWork"

    fun enqueue(context: Context) = WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        WORK_NAME,
        KEEP,
        PeriodicWorkRequestBuilder<AutoUpdateWorker>(Duration.ofHours(24))
          .setInitialDelay(Duration.ofHours(24))
          .setConstraints(
            Constraints.Builder()
              .setRequiredNetworkType(UNMETERED)
              .build()
          )
          .setBackoffCriteria(
            BackoffPolicy.LINEAR,
            30, TimeUnit.SECONDS
          )
          .build()
      )

    fun cancel(context: Context) = WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
  }

  private fun isInternetAvailable(): Boolean {
    return try {
      val url = URL("https://www.aptoide.com")
      val connection = url.openConnection() as HttpURLConnection
      connection.requestMethod = "HEAD"
      connection.readTimeout = 10000
      connection.connectTimeout = 10000
      connection.connect()
      val responseCode = connection.responseCode
      responseCode in 200..599
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }
}
