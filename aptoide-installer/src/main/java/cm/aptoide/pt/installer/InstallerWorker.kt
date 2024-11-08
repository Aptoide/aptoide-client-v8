package cm.aptoide.pt.installer

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType.CONNECTED
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes

@HiltWorker
class InstallerWorker @AssistedInject constructor(
  @Assisted private val appContext: Context,
  @Assisted private val workerParams: androidx.work.WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
  override suspend fun doWork(): Result {
    delay(10.minutes)
    return Result.success()
  }

  companion object {
    private const val WORK_NAME = "InstallerWork"

    fun enqueue(context: Context) = WorkManager.getInstance(context)
      .enqueueUniqueWork(
        WORK_NAME,
        ExistingWorkPolicy.REPLACE,
        OneTimeWorkRequestBuilder<InstallerWorker>()
          .setConstraints(
            Constraints.Builder()
              .setRequiredNetworkType(CONNECTED)
              .build()
          )
          .build()
      )

    fun cancel(context: Context) = WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
  }
}
