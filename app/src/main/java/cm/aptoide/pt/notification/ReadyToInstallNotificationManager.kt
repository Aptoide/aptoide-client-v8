package cm.aptoide.pt.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.NotificationCompat
import cm.aptoide.pt.AptoideApplication
import cm.aptoide.pt.DeepLinkIntentReceiver
import cm.aptoide.pt.R
import cm.aptoide.pt.install.InstallManager
import cm.aptoide.pt.networking.image.ImageLoader
import rx.Single

class ReadyToInstallNotificationManager(val installManager: InstallManager,
                                        val notificationIdsMapper: NotificationIdsMapper) {

  private var isNotificationDisplayed: Boolean = false

  @Synchronized
  fun setIsNotificationDisplayed(isActive: Boolean) {
    isNotificationDisplayed = isActive
  }

  fun buildNotification(aptoideNotification: AptoideNotification,
                        context: Context): Single<Notification> {
    if (isNotificationDisplayed) {
      return buildMultiReadyToInstallNotification(context)
    }
    return buildSingleReadyToInstallNotification(aptoideNotification.appName,
        aptoideNotification.img, aptoideNotification.url, context)
  }

  private fun buildSingleReadyToInstallNotification(appName: String, icon: String, url: String,
                                                    context: Context): Single<Notification> {
    return Single.fromCallable {
      // TODO: Hardcoded
      val title = "Your app is ready to be installed!"
      val body = "Tap to install $appName"
      val notification =
          NotificationCompat.Builder(context)
              .setContentIntent(getSingleAppPressIntentAction(url, context))
              .setDeleteIntent(getOnDismissAction(context))
              .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
              .setContentTitle(title)
              .setContentText(body)
              .setLargeIcon(ImageLoader.with(context)
                  .loadBitmap(icon))
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
              .setStyle(NotificationCompat.BigTextStyle().bigText(body))
              .setAutoCancel(true)
              .setOngoing(false)
              .build()
      notification.flags =
          Notification.DEFAULT_LIGHTS or Notification.FLAG_AUTO_CANCEL
      notification
    }
  }

  private fun buildMultiReadyToInstallNotification(context: Context): Single<Notification> {
    return Single.fromCallable {
      // TODO: Hardcoded
      val title = "Your apps are ready to be installed!"
      val body = "Tap to install them."
      val notification =
          NotificationCompat.Builder(context)
              .setContentIntent(getMultiAppPressIntentAction(context))
              .setDeleteIntent(getOnDismissAction(context))
              .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
              .setContentTitle(title)
              .setContentText(body)
              .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
              .setStyle(NotificationCompat.BigTextStyle().bigText(body))
              .setAutoCancel(true)
              .setOngoing(false)
              .build()
      notification.flags =
          Notification.DEFAULT_LIGHTS or Notification.FLAG_AUTO_CANCEL
      notification
    }
  }

  private fun getSingleAppPressIntentAction(url: String,
                                            context: Context): PendingIntent {
    val resultIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url), context,
        DeepLinkIntentReceiver::class.java)
    return PendingIntent.getActivity(context, 0, resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT)
  }

  private fun getMultiAppPressIntentAction(context: Context): PendingIntent {
    val resultIntent = Intent(context,
        AptoideApplication.getActivityProvider()
            .mainActivityFragmentClass)
    resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.APPS, true)
    resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.FROM_NOTIFICATION_READY_TO_INSTALL,
        true)
    return PendingIntent.getActivity(context, 0, resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT)
  }

  private fun getOnDismissAction(context: Context): PendingIntent? {
    val notificationId =
        notificationIdsMapper.getNotificationId(AptoideNotification.APPS_READY_TO_INSTALL)
    val resultIntent = Intent(context,
        NotificationReceiver::class.java)
    resultIntent.action = NotificationReceiver.NOTIFICATION_DISMISSED_ACTION
    resultIntent.putExtra(NotificationReceiver.NOTIFICATION_NOTIFICATION_ID, notificationId)
    return PendingIntent.getBroadcast(context, notificationId, resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT)
  }
}