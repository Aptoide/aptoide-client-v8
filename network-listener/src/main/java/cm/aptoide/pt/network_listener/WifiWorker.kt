package cm.aptoide.pt.network_listener

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy.KEEP
import androidx.work.NetworkType.UNMETERED
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters

class WifiWorker(appContext: Context, workerParams: WorkerParameters) :
  Worker(appContext, workerParams) {
  override fun doWork(): Result {
    return Result.success()
  }

  companion object {
    private const val WORK_NAME = "WifiWork"

    fun enqueue(context: Context) {
      val constraints = Constraints.Builder()
        .setRequiredNetworkType(UNMETERED)
        .build()

      WorkManager.getInstance(context)
        .enqueueUniqueWork(
          WORK_NAME,
          KEEP,
          OneTimeWorkRequestBuilder<WifiWorker>()
            .setConstraints(constraints)
            .build()
        )
    }

    fun cancel(context: Context) {
      WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
  }
}
