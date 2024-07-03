package cm.aptoide.pt.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import cm.aptoide.pt.AptoideApplication
import cm.aptoide.pt.DeepLinkIntentReceiver
import cm.aptoide.pt.R
import cm.aptoide.pt.app.aptoideinstall.AptoideInstallManager
import cm.aptoide.pt.home.apps.AppMapper
import cm.aptoide.pt.home.apps.model.UpdateApp
import cm.aptoide.pt.preferences.managed.ManagerPreferences
import cm.aptoide.pt.updates.UpdateRepository
import cm.aptoide.pt.utils.AptoideUtils
import rx.Observable

class UpdatesNotificationWorker(
  private val context: Context, workerParameters: WorkerParameters,
  private val updateRepository: UpdateRepository,
  private val sharedPreferences: SharedPreferences,
  private val aptoideInstallManager: AptoideInstallManager,
  private val appMapper: AppMapper
) :
  Worker(context, workerParameters) {

  override fun doWork(): Result {
    updateRepository.sync(true, false, false)
      .andThen(updateRepository.getAll(false))
      .first()
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
              && updateApp2.isInstalledWithAptoide
            ) 1
            else 0)
          }
          .doOnNext { updates ->
            handleNotification(updates)
          }
      }.toBlocking().first()
    return Result.success()
  }

  private fun handleNotification(updates: List<UpdateApp>) {
    if (shouldShowNotification(updates.size)) {
      val resultIntent = Intent(
        applicationContext,
        AptoideApplication.getActivityProvider()
          .mainActivityFragmentClass
      )
      resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_UPDATES, true)
      val resultPendingIntent =
        PendingIntent.getActivity(
          applicationContext, 0, resultIntent,
          PendingIntent.FLAG_IMMUTABLE
        )

      val tickerText =
        AptoideUtils.StringU.getFormattedString(
          R.string.has_updates,
          applicationContext.resources,
          applicationContext.getString(R.string.app_name)
        )

      val notification = getNotificationDefaultDesign(updates, resultPendingIntent, tickerText)

      with(NotificationManagerCompat.from(context)) {
        notify(UpdatesNotificationManager.UPDATE_NOTIFICATION_ID, notification)
      }
      ManagerPreferences.setLastUpdates(updates.size, sharedPreferences)
    }
  }

  private fun getNotificationDefaultDesign(
    updates: List<UpdateApp>,
    resultPendingIntent: PendingIntent,
    tickerText: String
  ): Notification {

    val contentTitle = applicationContext.getString(R.string.app_name)
    var contentText =
      AptoideUtils.StringU.getFormattedString(
        R.string.new_updates, applicationContext.resources,
        updates.size
      )
    if (updates.size == 1) {
      contentText = AptoideUtils.StringU.getFormattedString(
        R.string.one_new_update,
        applicationContext.resources,
        updates.size
      )
    }

    val builder = NotificationCompat.Builder(context, UpdatesNotificationManager.CHANNEL_ID)
      .setContentIntent(
        resultPendingIntent
      )
      .setOngoing(false)
      .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
      .setContentTitle(contentTitle)
      .setContentText(contentText)
      .setTicker(tickerText)
      .setAutoCancel(true)

    return builder.build()
  }

  private fun shouldShowNotification(updates: Int): Boolean {
    return (ManagerPreferences.isUpdateNotificationEnable(sharedPreferences) && updates > 0
      && updates != ManagerPreferences.getLastUpdates(sharedPreferences))
  }
}