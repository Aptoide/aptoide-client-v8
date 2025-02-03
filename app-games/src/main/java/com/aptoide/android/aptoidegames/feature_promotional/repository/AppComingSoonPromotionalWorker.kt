package com.aptoide.android.aptoidegames.feature_promotional.repository

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy.UPDATE
import androidx.work.NetworkType.CONNECTED
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import cm.aptoide.pt.extensions.hasNotificationsPermission
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.feature_apps.domain.AppSource.Companion.appendIfRequired
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.feature_promotional.AppComingSoonNotificationBuilder
import com.aptoide.android.aptoidegames.feature_promotional.domain.AppComingSoonManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration

@HiltWorker
class AppComingSoonPromotionalWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted workerParams: WorkerParameters,
  private val appMetaUseCase: AppMetaUseCase,
  private val appComingSoonNotificationBuilder: AppComingSoonNotificationBuilder,
  private val appComingSoonManager: AppComingSoonManager,
) : CoroutineWorker(appContext, workerParams) {

  override suspend fun doWork(): Result {
    val packageName = inputData.getString(PACKAGE_NAME)
    packageName?.let {
      if (!applicationContext.hasNotificationsPermission()) {
        appComingSoonManager.updateSubscribedApp(it, false)
      } else {
        try {
          val app = appMetaUseCase.getMetaInfo(
            source = AppSource.of(null, packageName).asSource()
              .appendIfRequired(BuildConfig.MARKET_NAME)
          )
          appComingSoonNotificationBuilder.showAppComingSoonNotification(app)
          appComingSoonManager.updateSubscribedApp(app.packageName, false)
        } catch (t: Throwable) {
          t.printStackTrace()
        }
      }
    }
    return Result.success()
  }

  companion object {
    private const val WORK_NAME = "AppComingSoonWork"
    private const val PACKAGE_NAME = "package_name"

    fun enqueue(context: Context, packageName: String) {
      val data: Data.Builder = Data.Builder()
      data.putString(PACKAGE_NAME, packageName)

      WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
          WORK_NAME + packageName,
          UPDATE,
          PeriodicWorkRequestBuilder<AppComingSoonPromotionalWorker>(Duration.ofHours(24))
            .setInitialDelay(Duration.ofSeconds(30))
            .setConstraints(
              Constraints.Builder()
                .setRequiredNetworkType(CONNECTED)
                .build()
            )
            .setInputData(data.build())
            .build()
        )
    }

    fun cancel(context: Context, packageName: String) {
      WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME + packageName)
    }
  }
}
