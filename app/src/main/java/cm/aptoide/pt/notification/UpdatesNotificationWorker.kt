package cm.aptoide.pt.notification

import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import cm.aptoide.pt.app.aptoideinstall.AptoideInstallManager
import cm.aptoide.pt.database.room.RoomUpdate
import cm.aptoide.pt.home.apps.AppMapper
import cm.aptoide.pt.home.apps.model.UpdateApp
import cm.aptoide.pt.preferences.managed.ManagerPreferences
import cm.aptoide.pt.updates.UpdateRepository
import rx.Observable

class UpdatesNotificationWorker(private val context: Context, workerParameters: WorkerParameters,
                                private val updateRepository: UpdateRepository,
                                private val sharedPreferences: SharedPreferences,
                                private val aptoideInstallManager: AptoideInstallManager,
                                private val appMapper: AppMapper) :
    Worker(context, workerParameters) {

  override fun doWork(): Result {
    updateRepository.sync(true, false)
        .andThen(updateRepository.getAll(false))
        .flatMap { updates: List<RoomUpdate> ->
          Observable.just(updates)
              .flatMapIterable { list: List<RoomUpdate>? -> list }
              .filter { update: RoomUpdate -> !update.isAppcUpgrade }
              .toList()
        }
        .first()
        .filter { ManagerPreferences.isUpdateNotificationEnable(sharedPreferences) }
        .flatMap { updates ->
          Observable.from(updates)
              .flatMapSingle({ update ->
                aptoideInstallManager.isInstalledWithAptoide(update.packageName)
                    .map { isAptoideInstalled ->
                      appMapper.mapUpdateToUpdateApp(update, isAptoideInstalled)
                    }
              }, false, 1)
              .toSortedList { updateApp, updateApp2 ->
                return@toSortedList (if (updateApp.isInstalledWithAptoide && !updateApp2.isInstalledWithAptoide) {
                  -1
                } else if (!updateApp.isInstalledWithAptoide
                    && updateApp2.isInstalledWithAptoide) 1
                else 0)
              }
              .doOnNext { updates -> handleNotification(updates) }
        }.toBlocking().first()
    return Result.success()
  }

  private fun handleNotification(updates: List<UpdateApp>) {
    val builder = NotificationCompat.Builder(context, UpdatesNotificationManager.CHANNEL_ID)
        .setSmallIcon(cm.aptoide.pt.R.drawable.ic_stat_aptoide_notification)
        .setContentTitle("Belo Titulo")
        .setContentText("Temos " + updates.size + " updates de qualidade")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    with(NotificationManagerCompat.from(context)) {
      // notificationId is a unique int for each notification that you must define
      notify(UpdatesNotificationManager.NOTIFICATION_ID, builder.build())
    }
  }

}