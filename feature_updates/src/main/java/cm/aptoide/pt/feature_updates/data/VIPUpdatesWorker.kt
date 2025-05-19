package cm.aptoide.pt.feature_updates.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy.UPDATE
import androidx.work.NetworkType.CONNECTED
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import cm.aptoide.pt.feature_updates.domain.Updates
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration

@HiltWorker
class VIPUpdatesWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted workerParams: WorkerParameters,
  private val vipUpdatesProvider: VIPUpdatesProvider,
  private val updates: Updates
) : CoroutineWorker(appContext, workerParams) {

  override suspend fun doWork(): Result {
    val updatesList = vipUpdatesProvider.getVIPUpdatesList()
    if (updatesList.isNotEmpty()) {
      updates.checkVIPUpdates(updatesList)
    }
    return Result.success()
  }

  companion object {
    private const val WORK_NAME = "VIPUpdatesWorker"

    fun enqueue(context: Context) {
      WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
          WORK_NAME,
          UPDATE,
          PeriodicWorkRequestBuilder<VIPUpdatesWorker>(Duration.ofHours(1))
            .setInitialDelay(Duration.ofSeconds(30))
            .setConstraints(
              Constraints.Builder()
                .setRequiredNetworkType(CONNECTED)
                .build()
            )
            .build()
        )
    }

    fun cancel(context: Context) {
      WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
  }
}
