package com.aptoide.android.aptoidegames.firebase

import android.Manifest
import android.annotation.SuppressLint
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
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.installer.notifications.ImageDownloader
import com.aptoide.android.aptoidegames.notifications.getNotificationIcon
import com.aptoide.android.aptoidegames.theme.Palette
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseNotificationBuilder @Inject constructor(
  @ApplicationContext private val context: Context,
  private val imageDownloader: ImageDownloader,
) {

  companion object {
    const val FCM_NOTIFICATION_CHANNEL_ID = "fcm_notification_channel"
    const val FCM_NOTIFICATION_CHANNEL_NAME = "Remote Notification Channel"
  }

  init {
    setupNotificationChannel(context)
  }

  private fun setupNotificationChannel(context: Context) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (notificationManager.getNotificationChannel(FCM_NOTIFICATION_CHANNEL_ID) == null) {
      NotificationChannel(
        FCM_NOTIFICATION_CHANNEL_ID,
        FCM_NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        setSound(null, null)
      }.let {
        notificationManager.createNotificationChannel(it)
      }
    }
  }

  @SuppressLint("MissingPermission")
  fun showFirebaseNotification(message: RemoteMessage) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {
          message.notification?.let {
            val notificationId =
              message.messageId?.hashCode() ?: "FCM.${UUID.randomUUID()}".hashCode()

            val resources = context.resources
            val uiMode = resources.configuration.uiMode
            val isNightMode =
              (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            val colorToUse = if (isNightMode) Palette.Primary.toArgb() else Palette.Black.toArgb()

            val notificationSmallIcon = BuildConfig.FLAVOR.getNotificationIcon()

            val clickIntent = PendingIntent.getActivity(
              context,
              notificationId,
              Intent(context, MainActivity::class.java)
                .putExtras(message.toIntent())
                .putExtra(FirebaseConstants.FIREBASE_MESSAGE_ID, message.messageId)
                .putExtra(FirebaseConstants.FIREBASE_ANALYTICS_DATA, message.toIntent().extras),
              PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, FCM_NOTIFICATION_CHANNEL_ID)
              .setShowWhen(true)
              .setColor(colorToUse)
              .setSmallIcon(notificationSmallIcon)
              .setLargeIcon(imageDownloader.downloadImageFrom(it.imageUrl.toString()))
              .setContentTitle(it.title)
              .setContentText(it.body)
              .setPriority(NotificationCompat.PRIORITY_DEFAULT)
              .setAutoCancel(true)
              .setContentIntent(clickIntent)
              .build()

            NotificationManagerCompat.from(context).notify(notificationId, notification)
          }
        }
      } catch (e: Throwable) {
        e.printStackTrace()
      }
    }
  }
}
