package com.aptoide.android.aptoidegames.updates

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
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import cm.aptoide.pt.extensions.isAllowed
import cm.aptoide.pt.feature_updates.presentation.UpdatesNotificationProvider
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.notifications.getNotificationIcon
import com.aptoide.android.aptoidegames.putDeeplink
import com.aptoide.android.aptoidegames.putNotificationSource
import com.aptoide.android.aptoidegames.theme.Palette
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatesNotificationBuilder @Inject constructor(
  @ApplicationContext private val context: Context,
) : UpdatesNotificationProvider {

  companion object {
    const val UPDATES_NOTIFICATION_CHANNEL_ID = "updates_notification_channel"
    const val UPDATES_NOTIFICATION_CHANNEL_NAME = "Updates Notification Channel"
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
    numberOfUpdates: Int
  ) {
    val notificationId = "Updates".hashCode()
    val notification = buildNotification(
      requestCode = notificationId,
      contentText = context.getString(R.string.update_notification_body),
      contentTitle = context.resources.getQuantityString(
        R.plurals.update_notification_title,
        numberOfUpdates, numberOfUpdates
      )
    )

    notification?.let { showNotification(notificationId, notification) }
  }

  @SuppressLint("MissingPermission")
  private fun showNotification(
    notificationId: Int,
    notification: Notification,
  ) {
    if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {
      NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
  }

  private fun buildNotification(
    requestCode: Int,
    contentTitle: String? = null,
    contentText: String,
    channel: String = UPDATES_NOTIFICATION_CHANNEL_ID
  ): Notification? = if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {

    val deepLink = buildUpdatesDeepLinkUri()

    val clickIntent = PendingIntent.getActivity(
      context,
      requestCode,
      Intent(context, MainActivity::class.java)
        .putDeeplink(deepLink)
        .putNotificationSource(),
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
        VectorDrawableCompat.create(context.resources, R.drawable.app_icon, null)?.toBitmap()
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
