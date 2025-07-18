package com.aptoide.android.aptoidegames.feature_editors_choice_recommendation

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType.UNMETERED
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration

@HiltWorker
class EditorsChoiceRecommendationAppsWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted workerParams: WorkerParameters,
  private val editorsChoiceAppsRecommendationNotificationBuilder: EditorsChoiceAppsRecommendationNotificationBuilder,
) : CoroutineWorker(appContext, workerParams) {

  override suspend fun doWork(): Result {
    editorsChoiceAppsRecommendationNotificationBuilder.showEditorsChoiceAppsRecommendationNotification()
    return Result.success()
  }

  companion object {
    private const val WORK_NAME = "EditorsChoiceRecommendationAppsWorker"

    fun enqueue(context: Context, delay: Long) {
      val data: Data.Builder = Data.Builder()

      WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
          uniqueWorkName = WORK_NAME,
          existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
          request = PeriodicWorkRequestBuilder<EditorsChoiceRecommendationAppsWorker>(
            Duration.ofDays(delay)
          )
            .setInitialDelay(Duration.ofMinutes(15))
            .setConstraints(
              Constraints.Builder()
                .setRequiredNetworkType(UNMETERED)
                .build()
            )
            .setInputData(data.build())
            .build()
        )
    }

    fun cancel(context: Context) = WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
  }
}
