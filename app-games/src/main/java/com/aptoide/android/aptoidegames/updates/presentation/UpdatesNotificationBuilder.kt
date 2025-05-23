package com.aptoide.android.aptoidegames.updates.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import cm.aptoide.pt.extensions.isAllowed
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.feature_updates.presentation.UpdatesNotificationProvider
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
class UpdatesNotificationBuilder @Inject constructor(
  @ApplicationContext private val context: Context,
  private val imageDownloader: ImageDownloader,
  private val notificationsAnalytics: NotificationsAnalytics
) : UpdatesNotificationProvider {

  companion object {
    const val UPDATES_NOTIFICATION_CHANNEL_ID = "updates_notification_channel"
    const val UPDATES_NOTIFICATION_CHANNEL_NAME = "Updates Notification Channel"
    const val UPDATES_NOTIFICATION_TAG = "general_updates_notification"
    const val VIP_UPDATES_NOTIFICATION_TAG = "vip_updates_notification"
  }

  init {
    setupNotificationChannel(context)
  }

  private fun setupNotificationChannel(context: Context) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (notificationManager.getNotificationChannel(UPDATES_NOTIFICATION_CHANNEL_ID) == null) {
      val name = UPDATES_NOTIFICATION_CHANNEL_NAME
      val descriptionText = "Updates notification channel"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(
        UPDATES_NOTIFICATION_CHANNEL_ID,
        name,
        importance
      ).apply {
        description = descriptionText
        setSound(null, null)
      }

      notificationManager.createNotificationChannel(channel)
    }
  }

  override suspend fun showUpdatesNotification(
    updates: List<App>
  ) {
    val title =
      if (updates.size == 1 && updates.first().packageName == BuildConfig.APPLICATION_ID) {
        context.resources.getString(R.string.update_aptoide_games_update_notification)
      } else {
        context.resources.getQuantityString(
          R.plurals.update_notification_title,
          updates.size, updates.size
        )
      }
    val notificationId = "Updates".hashCode()
    val notification = buildNotification(
      requestCode = notificationId,
      contentText = context.getString(R.string.update_notification_body),
      contentTitle = title,
      notificationTag = UPDATES_NOTIFICATION_TAG
    )

    notification?.let {
      showNotification(notificationId, notification, UPDATES_NOTIFICATION_TAG, "n-a")
    }
  }

  override suspend fun showVIPUpdateNotification(app: App) {
    val deepLink = buildAppViewDeepLinkUri(
      appSource = AppSource.of(appId = null, packageName = app.packageName)
    )
    val appIcon = imageDownloader.downloadImageFrom(app.icon)
    val title = "${app.name} has an update"
    val notificationId = "VIPUpdates${app.packageName}".hashCode()
    val notification = buildNotification(
      requestCode = notificationId,
      contentText = context.getString(R.string.update_notification_body),
      contentTitle = title,
      deepLink = deepLink,
      largeIcon = appIcon,
      notificationTag = VIP_UPDATES_NOTIFICATION_TAG,
      notificationPackage = app.packageName
    )

    notification?.let {
      showNotification(
        notificationId = notificationId,
        notification = notification,
        notificationTag = VIP_UPDATES_NOTIFICATION_TAG,
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
      NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
  }

  private fun buildNotification(
    requestCode: Int,
    contentTitle: String? = null,
    contentText: String,
    channel: String = UPDATES_NOTIFICATION_CHANNEL_ID,
    deepLink: String = buildUpdatesDeepLinkUri(),
    largeIcon: Bitmap? = null,
    notificationTag: String,
    notificationPackage: String? = null
  ): Notification? = if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {

    val clickIntent = PendingIntent.getActivity(
      context,
      requestCode,
      Intent(context, MainActivity::class.java)
        .putDeeplink(deepLink)
        .putNotificationSource()
        .putNotificationTag(notificationTag)
        .putNotificationPackage(notificationPackage),
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
      .setLargeIcon(
        largeIcon ?: VectorDrawableCompat.create(context.resources, R.drawable.app_icon, null)
          ?.toBitmap()
      )
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
