package com.aptoide.android.aptoidegames.feature_companion_apps_notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.NetworkType.UNMETERED
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import cm.aptoide.pt.feature_apps.data.AppRepository
import com.aptoide.android.aptoidegames.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration

@HiltWorker
class CompanionAppsWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted workerParams: WorkerParameters,
  private val companionAppsNotificationBuilder: CompanionAppsNotificationBuilder,
  private val appRepository: AppRepository

) : CoroutineWorker(appContext, workerParams) {

  override suspend fun doWork(): Result {
    val packageName = inputData.getString(PACKAGE_NAME)
    packageName?.let {
      val app = appRepository.getApp(it)
      companionAppsNotificationBuilder.showCompanionAppsNotification(
        app = app,
        title = applicationContext.resources.getString(
          R.string.companion_apps_roblox_notification_title
        ),
        message = applicationContext.resources.getString(R.string.companion_apps_roblox_notification_body)
      )
    }
    return Result.success()
  }

  companion object {
    private const val WORK_NAME = "CompanionAppsWorker"
    private const val PACKAGE_NAME = "package_name "

    fun enqueue(context: Context, packageName: String, delayDays: Long) {
      val data: Data.Builder = Data.Builder()
      data.putString(PACKAGE_NAME, packageName)

      WorkManager.getInstance(context)
        .enqueueUniqueWork(
          uniqueWorkName = WORK_NAME,
          existingWorkPolicy = REPLACE,
          request = OneTimeWorkRequestBuilder<CompanionAppsWorker>()
            .setInitialDelay(Duration.ofDays(delayDays))
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
