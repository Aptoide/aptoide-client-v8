package cm.aptoide.pt.notification

import android.content.Context
import android.content.SharedPreferences
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import cm.aptoide.pt.app.aptoideinstall.AptoideInstallManager
import cm.aptoide.pt.crashreports.CrashReport
import cm.aptoide.pt.home.apps.AppMapper
import cm.aptoide.pt.sync.SyncScheduler
import cm.aptoide.pt.sync.alarm.SyncStorage
import cm.aptoide.pt.updates.UpdateRepository
import cm.aptoide.pt.view.app.AppCenter

class AptoideWorkerFactory(private val updateRepository: UpdateRepository,
                           private val sharedPreferences: SharedPreferences,
                           private val aptoideInstallManager: AptoideInstallManager,
                           private val appMapper: AppMapper,
                           private val syncScheduler: SyncScheduler,
                           private val syncStorage: SyncStorage,
                           private val crashReport: CrashReport,
                           private val appCenter: AppCenter) :
    WorkerFactory() {

  override fun createWorker(appContext: Context, workerClassName: String,
                            workerParameters: WorkerParameters): ListenableWorker? {
    return when (workerClassName) {
      UpdatesNotificationWorker::class.java.name ->
        UpdatesNotificationWorker(appContext, workerParameters, updateRepository,
            sharedPreferences, aptoideInstallManager, appMapper)
      NotificationWorker::class.java.name -> NotificationWorker(appContext,
          workerParameters, syncScheduler, syncStorage, crashReport)
      ComingSoonNotificationWorker::class.java.name -> ComingSoonNotificationWorker(appContext,
          workerParameters, appCenter)
      else ->
        null
    }
  }
}