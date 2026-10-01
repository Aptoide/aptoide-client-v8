package com.aptoide.android.aptoidegames.play_and_earn.presentation.notifications

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
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.extensions.isAllowed
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.installer.notifications.ImageDownloader
import com.aptoide.android.aptoidegames.notifications.analytics.NotificationsAnalytics
import com.aptoide.android.aptoidegames.notifications.getNotificationIcon
import com.aptoide.android.aptoidegames.putNotificationPackage
import com.aptoide.android.aptoidegames.putNotificationRoute
import com.aptoide.android.aptoidegames.putNotificationSource
import com.aptoide.android.aptoidegames.putNotificationTag
import com.aptoide.android.aptoidegames.theme.BrandPrimary
import com.aptoide.android.aptoidegames.theme.FixedColors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Announces a mission the heartbeat reported as completed. Replaces the former screen overlay: the
 * notification survives being ignored, and tapping it opens the game's appview on the Rewards tab.
 */
@Singleton
class PaEMissionCompletedNotificationBuilder @Inject constructor(
  @ApplicationContext private val context: Context,
  private val imageDownloader: ImageDownloader,
  private val notificationsAnalytics: NotificationsAnalytics
) {

  companion object {
    const val MISSION_COMPLETED_NOTIFICATION_CHANNEL_ID =
      "pae_mission_completed_notification_channel"
    const val MISSION_COMPLETED_NOTIFICATION_CHANNEL_NAME =
      "Play & Earn Mission Notification Channel"
    const val MISSION_COMPLETED_NOTIFICATION_TAG = "pae_mission_completed_notification"

    private const val ICON_DOWNLOAD_TIMEOUT_MS = 5_000L
  }

  init {
    setupNotificationChannel(context)
  }

  private fun setupNotificationChannel(context: Context) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val existingChannel =
      notificationManager.getNotificationChannel(MISSION_COMPLETED_NOTIFICATION_CHANNEL_ID)

    if (existingChannel == null) {
      val channel = NotificationChannel(
        MISSION_COMPLETED_NOTIFICATION_CHANNEL_ID,
        MISSION_COMPLETED_NOTIFICATION_CHANNEL_NAME,
        // HIGH so the banner peeks over the running game, the way the overlay it replaces did.
        // Silent on purpose: it fires mid-gameplay and must not cut into game audio. Channel
        // importance is immutable once created, so this cannot be raised later in place.
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Play & Earn mission completed notification channel"
        setSound(null, null)
      }

      notificationManager.createNotificationChannel(channel)
    }
  }

  suspend fun showMissionCompletedNotification(
    mission: PaEMission,
    packageName: String
  ) {
    val notificationId = "PaEMissionCompleted$packageName${mission.title}".hashCode()

    val notification = buildNotification(
      requestCode = notificationId,
      mission = mission,
      packageName = packageName
    ) ?: return

    showNotification(
      notificationId = notificationId,
      notification = notification,
      notificationPackage = packageName
    )
  }

  @SuppressLint("MissingPermission")
  private fun showNotification(
    notificationId: Int,
    notification: Notification,
    notificationPackage: String
  ) {
    if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {
      notificationsAnalytics.sendNotificationReceived(
        MISSION_COMPLETED_NOTIFICATION_TAG,
        notificationPackage
      )
      NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
  }

  private suspend fun buildNotification(
    requestCode: Int,
    mission: PaEMission,
    packageName: String
  ): Notification? = if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {

    val clickIntent = PendingIntent.getActivity(
      context,
      requestCode,
      Intent(context, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .putNotificationRoute(missionCompletedRoute(packageName))
        .putNotificationSource()
        .putNotificationTag(MISSION_COMPLETED_NOTIFICATION_TAG)
        .putNotificationPackage(packageName),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notificationIcon = BuildConfig.FLAVOR.getNotificationIcon()

    val uiMode = context.resources.configuration.uiMode
    val isNightMode =
      (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    val colorToUse = if (isNightMode) BrandPrimary.toArgb() else FixedColors.Dark.toArgb()

    NotificationCompat.Builder(context, MISSION_COMPLETED_NOTIFICATION_CHANNEL_ID)
      .setShowWhen(true)
      .setColor(colorToUse)
      .setSmallIcon(notificationIcon)
      // A hung icon fetch must not hold the notification back; the small icon alone is fine.
      .setLargeIcon(withTimeoutOrNull(ICON_DOWNLOAD_TIMEOUT_MS) {
        imageDownloader.downloadImageFrom(mission.icon)
      })
      .setContentTitle(context.getString(R.string.play_and_earn_challenge_completed_title))
      .setContentText(
        context.getString(
          R.string.play_and_earn_mission_completed_notification_body,
          mission.units,
          mission.title
        )
      )
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(clickIntent)
      .build()
  } else {
    null
  }
}
