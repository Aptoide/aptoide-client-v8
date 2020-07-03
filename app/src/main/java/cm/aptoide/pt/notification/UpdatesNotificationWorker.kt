package cm.aptoide.pt.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import cm.aptoide.pt.AptoideApplication
import cm.aptoide.pt.DeepLinkIntentReceiver
import cm.aptoide.pt.R
import cm.aptoide.pt.abtesting.analytics.UpdatesNotificationAnalytics
import cm.aptoide.pt.app.aptoideinstall.AptoideInstallManager
import cm.aptoide.pt.database.room.RoomUpdate
import cm.aptoide.pt.home.apps.AppMapper
import cm.aptoide.pt.home.apps.model.UpdateApp
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.preferences.managed.ManagerPreferences
import cm.aptoide.pt.updates.UpdateRepository
import cm.aptoide.pt.utils.AptoideUtils
import rx.Observable
import kotlin.math.min

class UpdatesNotificationWorker(private val context: Context, workerParameters: WorkerParameters,
                                private val updateRepository: UpdateRepository,
                                private val sharedPreferences: SharedPreferences,
                                private val aptoideInstallManager: AptoideInstallManager,
                                private val appMapper: AppMapper,
                                private val updatesNotificationAnalytics: UpdatesNotificationAnalytics) :
    Worker(context, workerParameters) {

  private lateinit var config: String

  override fun doWork(): Result {
    config = inputData.getString(UpdatesNotificationManager.CONFIGURATION_KEY) ?: "control"

    updateRepository.sync(true, false)
        .andThen(updateRepository.getAll(false))
        .flatMap { updates: List<RoomUpdate> ->
          Observable.just(updates)
              .flatMapIterable { list: List<RoomUpdate>? -> list }
              .filter { update: RoomUpdate -> !update.isAppcUpgrade }
              .toList()
        }
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
                    && updateApp2.isInstalledWithAptoide) 1
                else 0)
              }
              .doOnNext { updates ->
                updatesNotificationAnalytics.sendUpdatesNotificationReceivedEvent()
                handleNotification(updates, config)
              }
        }.toBlocking().first()
    return Result.success()
  }

  private fun handleNotification(updates: List<UpdateApp>, config: String) {
    if (shouldShowNotification(updates.size)) {
      val resultIntent = Intent(applicationContext,
          AptoideApplication.getActivityProvider()
              .mainActivityFragmentClass)
      resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_UPDATES, true)
      resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.UPDATES_NOTIFICATION_GROUP, config)
      val resultPendingIntent =
          PendingIntent.getActivity(applicationContext, 0, resultIntent,
              PendingIntent.FLAG_UPDATE_CURRENT)

      val tickerText =
          AptoideUtils.StringU.getFormattedString(R.string.has_updates,
              applicationContext.resources,
              applicationContext.getString(R.string.app_name))


      val notification = when (config) {
        "design", "all" -> {
          getNotificationNewDesign(updates, resultPendingIntent, tickerText)
        }
        else -> {
          getNotificationDefaultDesign(updates, resultPendingIntent, tickerText)
        }
      }

      with(NotificationManagerCompat.from(context)) {
        notify(UpdatesNotificationManager.UPDATE_NOTIFICATION_ID, notification)
      }
      updatesNotificationAnalytics.sendUpdatesNotificationImpressionEvent(config)
      ManagerPreferences.setLastUpdates(updates.size, sharedPreferences)
    }
  }

  private fun getNotificationDefaultDesign(
      updates: List<UpdateApp>,
      resultPendingIntent: PendingIntent,
      tickerText: String): Notification {

    val contentTitle = applicationContext.getString(R.string.app_name)
    var contentText =
        AptoideUtils.StringU.getFormattedString(R.string.new_updates, applicationContext.resources,
            updates.size)
    if (updates.size == 1) {
      contentText = AptoideUtils.StringU.getFormattedString(R.string.one_new_update,
          applicationContext.resources,
          updates.size)
    }

    val builder = NotificationCompat.Builder(context, UpdatesNotificationManager.CHANNEL_ID)
        .setContentIntent(
            resultPendingIntent)
        .setOngoing(false)
        .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
        .setContentTitle(contentTitle)
        .setContentText(contentText)
        .setTicker(tickerText)
        .setAutoCancel(true)

    return builder.build()
  }

  private fun getNotificationNewDesign(
      updates: List<UpdateApp>,
      resultPendingIntent: PendingIntent,
      tickerText: String): Notification {

    val remoteViews =
        RemoteViews(applicationContext.packageName, R.layout.updates_notification_with_icons)

    val updatesToShow = min(updates.size, 7)
    for (i in 0 until updatesToShow) {
      remoteViews.setImageViewBitmap(resIdMapper(i),
          ImageLoader.with(applicationContext)
              .load(updates[i].icon))
    }


    val builder = NotificationCompat.Builder(context, UpdatesNotificationManager.CHANNEL_ID)
        .setContentIntent(resultPendingIntent)
        .setOngoing(false)
        .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setCustomContentView(remoteViews)
        .setTicker(tickerText)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setAutoCancel(true)

    return builder.build()
  }

  private fun shouldShowNotification(updates: Int): Boolean {
    return (ManagerPreferences.isUpdateNotificationEnable(sharedPreferences) && updates > 0
        && updates != ManagerPreferences.getLastUpdates(sharedPreferences))
  }

  private fun resIdMapper(offset: Int): Int {
    return when (offset) {
      0 -> R.id.icon_0
      1 -> R.id.icon_1
      2 -> R.id.icon_2
      3 -> R.id.icon_3
      4 -> R.id.icon_4
      5 -> R.id.icon_5
      6 -> R.id.icon_6
      else -> 0
    }
  }
}