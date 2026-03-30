package com.aptoide.android.aptoidegames.gamesfeed

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import cm.aptoide.pt.extensions.isAllowed
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.firebase.FirebaseNotificationBuilder
import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedLocalRepository
import com.aptoide.android.aptoidegames.notifications.getNotificationIcon
import com.aptoide.android.aptoidegames.putNotificationSource
import com.aptoide.android.aptoidegames.theme.Palette
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamesFeedNotificationBuilder @Inject constructor(
  @ApplicationContext private val context: Context,
  private val gamesFeedLocalRepository: GamesFeedLocalRepository,
) {

  companion object {
    const val GAMESFEED_ROUTE_KEY = "gamesfeed_route"
    private const val PACKAGE_NAME_KEY = "package_name"
    private const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L
  }

  fun handleGamesFeedNotification(message: RemoteMessage) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        if (shouldShowGamesFeedNotification()) {
          gamesFeedLocalRepository.saveLastGamesFeedNotificationTimestamp(System.currentTimeMillis())
          showGamesFeedNotification(message)
          Timber.d("GamesFeed notification shown.")
        } else {
          Timber.d("GamesFeed notification throttled (already shown within last 24h).")
        }
      } catch (e: Exception) {
        Timber.e(e, "Error handling gamesfeed notification")
      }
    }
  }

  private suspend fun shouldShowGamesFeedNotification(): Boolean {
    val lastTimestamp = gamesFeedLocalRepository.getLastGamesFeedNotificationTimestamp()
    val now = System.currentTimeMillis()
    return lastTimestamp == null || (now - lastTimestamp) >= ONE_DAY_MILLIS
  }

  @SuppressLint("MissingPermission")
  private fun showGamesFeedNotification(message: RemoteMessage) {
    if (!context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) return

    val notification = message.notification ?: return
    val packageName = message.data[PACKAGE_NAME_KEY]

    val notificationId =
      message.messageId?.hashCode() ?: "FCM.${UUID.randomUUID()}".hashCode()

    val resources = context.resources
    val uiMode = resources.configuration.uiMode
    val isNightMode =
      (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    val colorToUse = if (isNightMode) Palette.Primary.toArgb() else Palette.Black.toArgb()

    val smallIcon = BuildConfig.FLAVOR.getNotificationIcon()

    val largeIcon = packageName?.let {
      try {
        context.packageManager.getApplicationIcon(it).toBitmap()
      } catch (e: Exception) {
        Timber.e(e, "Failed to get app icon for $it")
        null
      }
    }

    val route = if (packageName != null) {
      "gamesFeed?package_name=$packageName"
    } else {
      "gamesFeed"
    }

    val clickIntent = PendingIntent.getActivity(
      context,
      notificationId,
      Intent(context, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .putExtra(GAMESFEED_ROUTE_KEY, route)
        .putNotificationSource(),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builtNotification = NotificationCompat.Builder(
      context,
      FirebaseNotificationBuilder.FCM_NOTIFICATION_CHANNEL_ID
    )
      .setShowWhen(true)
      .setColor(colorToUse)
      .setSmallIcon(smallIcon)
      .apply { if (largeIcon != null) setLargeIcon(largeIcon) }
      .setContentTitle(notification.title)
      .setContentText(notification.body)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)
      .setContentIntent(clickIntent)
      .build()

    NotificationManagerCompat.from(context).notify(notificationId, builtNotification)
  }
}
