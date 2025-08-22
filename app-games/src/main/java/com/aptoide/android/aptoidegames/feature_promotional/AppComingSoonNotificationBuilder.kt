package com.aptoide.android.aptoidegames.feature_promotional

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cm.aptoide.pt.extensions.isAllowed
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppSource
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.appview.buildAppViewDeepLinkUri
import com.aptoide.android.aptoidegames.installer.notifications.ImageDownloader
import com.aptoide.android.aptoidegames.notifications.analytics.NotificationsAnalytics
import com.aptoide.android.aptoidegames.notifications.getNotificationIcon
import com.aptoide.android.aptoidegames.putDeeplink
import com.aptoide.android.aptoidegames.putNotificationPackage
import com.aptoide.android.aptoidegames.putNotificationSource
import com.aptoide.android.aptoidegames.putNotificationTag
import com.aptoide.android.aptoidegames.theme.Palette
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppComingSoonNotificationBuilder @Inject constructor(
  @ApplicationContext private val context: Context,
  private val imageDownloader: ImageDownloader,
  private val notificationsAnalytics: NotificationsAnalytics
) {

  companion object {
    const val APP_COMING_SOON_NOTIFICATION_CHANNEL_ID = "app_coming_soon_notification_channel"
    const val APP_COMING_SOON_NOTIFICATION_CHANNEL_NAME = "App_Coming_Soon Notification Channel"
    const val APP_COMING_SOON_NOTIFICATION_TAG = "app_coming_soon_notification"
  }

  init {
    setupNotificationChannel(context)
  }

  private fun setupNotificationChannel(context: Context) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (notificationManager.getNotificationChannel(APP_COMING_SOON_NOTIFICATION_CHANNEL_ID) == null) {
      val name = APP_COMING_SOON_NOTIFICATION_CHANNEL_NAME
      val descriptionText = "App Coming Soon notification channel"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(
        APP_COMING_SOON_NOTIFICATION_CHANNEL_ID,
        name,
        importance
      ).apply {
        description = descriptionText
        setSound(null, null)
      }

      notificationManager.createNotificationChannel(channel)
    }
  }

  suspend fun showAppComingSoonNotification(
    app: App
  ) {
    val title = context.resources.getString(R.string.promotional_new_notification_title)
    val notificationId = "AppComingSoon${app.packageName}".hashCode()
    val notification = buildNotification(
      requestCode = notificationId,
      contentText = context.getString(R.string.promotional_new_notification_body, app.name),
      contentTitle = title,
      app = app
    )

    notification?.let {
      showNotification(
        notificationId = notificationId,
        notification = notification,
        notificationTag = APP_COMING_SOON_NOTIFICATION_TAG,
        notificationPackage = app.packageName
      )
    }
  }

  @SuppressLint("MissingPermission")
  private fun showNotification(
    notificationId: Int,
    notification: Notification,
    notificationTag: String,
    notificationPackage: String
  ) {
    if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {
      notificationsAnalytics.sendNotificationReceived(notificationTag, notificationPackage)
      NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
  }

  private suspend fun buildNotification(
    requestCode: Int,
    contentTitle: String? = null,
    contentText: String,
    channel: String = APP_COMING_SOON_NOTIFICATION_CHANNEL_ID,
    app: App
  ): Notification? = if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {

    val deepLink = buildAppViewDeepLinkUri(
      AppSource.of(
        appId = app.appId,
        packageName = app.packageName
      )
    )

    val clickIntent = PendingIntent.getActivity(
      context,
      requestCode,
      Intent(context, MainActivity::class.java)
        .putDeeplink(deepLink)
        .putNotificationSource()
        .putNotificationTag(APP_COMING_SOON_NOTIFICATION_TAG)
        .putNotificationPackage(app.packageName),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notificationIcon = BuildConfig.FLAVOR.getNotificationIcon()

    val resources = context.resources
    val uiMode = resources.configuration.uiMode
    val isNightMode =
      (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    val colorToUse = if (isNightMode) Palette.Primary.toArgb() else Palette.Black.toArgb()

    NotificationCompat.Builder(context, channel)
      .setShowWhen(true)
      .setColor(colorToUse)
      .setSmallIcon(notificationIcon)
      .setLargeIcon(imageDownloader.downloadImageFrom(app.icon))
      .setContentTitle(contentTitle)
      .setContentText(contentText)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)
      .setContentIntent(clickIntent)
      .build()
  } else {
    null
  }
}
