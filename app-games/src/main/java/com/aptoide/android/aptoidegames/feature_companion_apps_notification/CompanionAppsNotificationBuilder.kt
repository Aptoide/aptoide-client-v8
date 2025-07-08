package com.aptoide.android.aptoidegames.feature_companion_apps_notification

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
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.feature_apps.presentation.buildSeeMoreDeepLinkUri
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
class CompanionAppsNotificationBuilder @Inject constructor(
  @ApplicationContext private val context: Context,
  private val imageDownloader: ImageDownloader,
  private val notificationsAnalytics: NotificationsAnalytics
) {

  companion object {
    const val COMPANION_APPS_NOTIFICATION_CHANNEL_ID = "companion_apps_notification_channel"
    const val COMPANION_APPS_NOTIFICATION_CHANNEL_NAME = "Companion Apps Notification Channel"
    const val COMPANION_APPS_NOTIFICATION_TAG = "companion_apps_notification"
  }

  init {
    setupNotificationChannel(context)
  }

  private fun setupNotificationChannel(context: Context) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (notificationManager.getNotificationChannel(COMPANION_APPS_NOTIFICATION_CHANNEL_ID) == null) {
      val name = COMPANION_APPS_NOTIFICATION_CHANNEL_NAME
      val descriptionText = "Companion Apps notification channel"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(
        COMPANION_APPS_NOTIFICATION_CHANNEL_ID,
        name,
        importance
      ).apply {
        description = descriptionText
        setSound(null, null)
      }

      notificationManager.createNotificationChannel(channel)
    }
  }

  suspend fun showCompanionAppsNotification(
    app: App,
    title: String,
    message: String,
  ) {
    val notificationId = "CompanionApps${app.packageName}".hashCode()
    val notification = buildNotification(
      requestCode = notificationId,
      contentText = message,
      contentTitle = title,
      app = app
    )

    notification?.let {
      showNotification(
        notificationId = notificationId,
        notification = notification,
        notificationTag = COMPANION_APPS_NOTIFICATION_TAG,
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
      notificationsAnalytics.sendNotificationImpression(notificationTag, notificationPackage)
      notificationsAnalytics.sendRobloxNofiticationShown()
      NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
  }

  private suspend fun buildNotification(
    requestCode: Int,
    contentTitle: String? = null,
    contentText: String,
    channel: String = COMPANION_APPS_NOTIFICATION_CHANNEL_ID,
    app: App
  ): Notification? = if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {

    val deepLink = buildSeeMoreDeepLinkUri(
      context.resources.getString(R.string.companion_apps_roblox_bundle_title),
      "ab-test-companion-app-bundle"
    )

    val clickIntent = PendingIntent.getActivity(
      context,
      requestCode,
      Intent(context, MainActivity::class.java)
        .putDeeplink(deepLink)
        .putNotificationSource()
        .putNotificationTag(COMPANION_APPS_NOTIFICATION_TAG)
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
