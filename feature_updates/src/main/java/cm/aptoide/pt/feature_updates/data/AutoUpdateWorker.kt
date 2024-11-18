package cm.aptoide.pt.feature_updates.data

import android.content.Context
import androidx.hilt.work.HiltWorker
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
import java.time.Duration

@HiltWorker
class AutoUpdateWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted workerParams: WorkerParameters,
  private val updates: Updates,
) : CoroutineWorker(appContext, workerParams) {

  override suspend fun doWork(): Result {
    updates.autoUpdate()
    return Result.success()
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
          .build()
      )

    fun cancel(context: Context) = WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
  }
}
