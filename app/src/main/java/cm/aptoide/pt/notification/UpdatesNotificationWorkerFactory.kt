package cm.aptoide.pt.notification

import android.content.Context
import android.content.SharedPreferences
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import cm.aptoide.pt.app.aptoideinstall.AptoideInstallManager
import cm.aptoide.pt.home.apps.AppMapper
import cm.aptoide.pt.updates.UpdateRepository

class UpdatesNotificationWorkerFactory(private val updateRepository: UpdateRepository,
                                       private val sharedPreferences: SharedPreferences,
                                       private val aptoideInstallManager: AptoideInstallManager,
                                       private val appMapper: AppMapper) : WorkerFactory() {

  override fun createWorker(appContext: Context, workerClassName: String,
                            workerParameters: WorkerParameters): ListenableWorker? {
    return UpdatesNotificationWorker(appContext, workerParameters, updateRepository,
        sharedPreferences, aptoideInstallManager, appMapper)
  }
}