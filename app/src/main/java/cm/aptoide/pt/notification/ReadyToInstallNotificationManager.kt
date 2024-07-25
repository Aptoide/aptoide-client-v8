package cm.aptoide.pt.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import cm.aptoide.pt.AptoideApplication
import cm.aptoide.pt.DeepLinkIntentReceiver
import cm.aptoide.pt.R
import cm.aptoide.pt.install.InstallManager
import cm.aptoide.pt.networking.image.ImageLoader
import cm.aptoide.pt.view.MainActivity
import rx.Single

class ReadyToInstallNotificationManager(
  val installManager: InstallManager,
  val notificationIdsMapper: NotificationIdsMapper
) {
  companion object {
    const val CHANNEL_ID = "ready_to_install_notification_channel"
    const val ORIGIN = "ready_to_install"
  }

  private var isNotificationDisplayed: Boolean = false

  @RequiresApi(Build.VERSION_CODES.O)
  fun getNotificationChannel(): NotificationChannel {
    val name = "Install notifications"
    val descriptionText = "Install"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    return NotificationChannel(CHANNEL_ID, name, importance).apply {
      description = descriptionText
    }
  }

  @Synchronized
  fun setIsNotificationDisplayed(isActive: Boolean) {
    isNotificationDisplayed = isActive
  }

  fun buildNotification(
    aptoideNotification: AptoideNotification,
    context: Context
  ): Single<Notification> {
    if (isNotificationDisplayed) {
      return buildMultiReadyToInstallNotification(context)
    }
    return buildSingleReadyToInstallNotification(
      aptoideNotification.appName,
      aptoideNotification.img, aptoideNotification.url, context
    )
  }

  private fun buildSingleReadyToInstallNotification(
    appName: String, icon: String, url: String,
    context: Context
  ): Single<Notification> {
    return Single.fromCallable {
      val title = context.getString(R.string.notification_install_ready_singular_title)
      val body = context.getString(R.string.notification_install_ready_singular_body, appName)
      val notification =
        NotificationCompat.Builder(context)
          .setContentIntent(getSingleAppPressIntentAction(url, context))
          .setDeleteIntent(getOnDismissAction(context))
          .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
          .setContentTitle(title)
          .setContentText(body)
          .setLargeIcon(
            ImageLoader.with(context)
              .loadBitmap(icon)
          )
          .setStyle(NotificationCompat.BigTextStyle().bigText(body))
          .setAutoCancel(true)
          .setOngoing(true)
          .setChannelId(CHANNEL_ID)
          .setOnlyAlertOnce(true)
          .build()
      notification
    }
  }

  private fun buildMultiReadyToInstallNotification(context: Context): Single<Notification> {
    return Single.fromCallable {
      val title = context.getString(R.string.notification_install_ready_plural_title)
      val body = context.getString(R.string.notification_install_ready_plural_body)
      val notification =
        NotificationCompat.Builder(context)
          .setContentIntent(getMultiAppPressIntentAction(context))
          .setDeleteIntent(getOnDismissAction(context))
          .setSmallIcon(R.drawable.ic_stat_aptoide_notification)
          .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
          .setContentTitle(title)
          .setContentText(body)
          .setStyle(NotificationCompat.BigTextStyle().bigText(body))
          .setAutoCancel(true)
          .setChannelId(CHANNEL_ID)
          .setOnlyAlertOnce(true)
          .setOngoing(true)
          .build()
      notification
    }
  }

  private fun getSingleAppPressIntentAction(
    url: String,
    context: Context
  ): PendingIntent {
    val resultIntent = Intent(
      Intent.ACTION_VIEW, Uri.parse(url), context,
      DeepLinkIntentReceiver::class.java
    )
    return PendingIntent.getActivity(
      context, 0, resultIntent,
      PendingIntent.FLAG_IMMUTABLE
    )
  }

  private fun getMultiAppPressIntentAction(context: Context): PendingIntent {
    val resultIntent = Intent(
      context,
      AptoideApplication.getActivityProvider()
        .mainActivityFragmentClass
    )
    resultIntent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.APPS, true)
    resultIntent.putExtra(
      DeepLinkIntentReceiver.DeepLinksKeys.FROM_NOTIFICATION_READY_TO_INSTALL,
      true
    )
    return PendingIntent.getActivity(
      context, 0, resultIntent,
      PendingIntent.FLAG_IMMUTABLE
    )
  }

  private fun getOnDismissAction(context: Context): PendingIntent? {
    val notificationId =
      notificationIdsMapper.getNotificationId(AptoideNotification.APPS_READY_TO_INSTALL)
    val resultIntent = Intent(
      context,
      MainActivity::class.java
    )
    resultIntent.action = SystemNotificationShower.NOTIFICATION_DISMISSED_ACTION
    resultIntent.putExtra(SystemNotificationShower.NOTIFICATION_NOTIFICATION_ID, notificationId)
    return PendingIntent.getActivity(
      context, notificationId, resultIntent,
      PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
  }
}