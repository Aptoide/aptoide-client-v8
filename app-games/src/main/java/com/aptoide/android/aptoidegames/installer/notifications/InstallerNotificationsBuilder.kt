package com.aptoide.android.aptoidegames.installer.notifications

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
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.extensions.isAllowed
import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.install_manager.Task.State
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.appview.buildAppViewDeepLinkUri
import com.aptoide.android.aptoidegames.installer.AppDetails
import com.aptoide.android.aptoidegames.notifications.getNotificationIcon
import com.aptoide.android.aptoidegames.putDeeplink
import com.aptoide.android.aptoidegames.putNotificationSource
import com.aptoide.android.aptoidegames.theme.Palette
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class InstallerNotificationsBuilder @Inject constructor(
  @ApplicationContext private val context: Context,
  private val stringToIntConverter: StringToIntConverter,
) {

  companion object {
    const val INSTALLER_NOTIFICATION_CHANNEL_ID = "installer_notification_channel"
    const val INSTALLER_NOTIFICATION_CHANNEL_NAME = "Installer Notification Channel"
    const val READY_TO_INSTALL_NOTIFICATION_CHANNEL_ID = "ready_to_install_notification_channel"
    const val READY_TO_INSTALL_NOTIFICATION_CHANNEL_NAME = "Ready To Install Notification Channel"
    const val ALLOW_METERED_DOWNLOAD_FOR_PACKAGE = "allowMeteredDownloadForPackage"
    const val DOWNLOADING_NOTIFICATIONS_GROUP = "downloading_notifications_group"
    const val READY_TO_INSTALL_NOTIFICATIONS_GROUP = "ready_to_install_notifications_group"
  }

  init {
    setupNotificationChannel(context)
    setupReadyToInstallNotificationChannel(context)
  }

  private fun setupNotificationChannel(context: Context) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (notificationManager.getNotificationChannel(INSTALLER_NOTIFICATION_CHANNEL_ID) == null) {
      val name = INSTALLER_NOTIFICATION_CHANNEL_NAME
      val descriptionText = "Installation notification channel"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(
        INSTALLER_NOTIFICATION_CHANNEL_ID,
        name,
        importance
      ).apply {
        description = descriptionText
        setSound(null, null)
      }

      notificationManager.createNotificationChannel(channel)
    }
  }

  private fun setupReadyToInstallNotificationChannel(context: Context) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (notificationManager.getNotificationChannel(READY_TO_INSTALL_NOTIFICATION_CHANNEL_ID) == null) {
      val name = READY_TO_INSTALL_NOTIFICATION_CHANNEL_NAME
      val descriptionText = "Ready for installations notification channel"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(
        READY_TO_INSTALL_NOTIFICATION_CHANNEL_ID,
        name,
        importance
      ).apply {
        description = descriptionText
        setSound(null, null)
      }
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun showInstallationStateNotification(
    packageName: String,
    appDetails: AppDetails?,
    appIcon: Bitmap?,
    state: State,
    size: Long,
  ) {
    val notificationId = stringToIntConverter.getStringId(packageName)

    if (state == State.Canceled || state == State.Aborted || state == State.OutOfSpace || state == State.Failed) {
      cancelNotification(notificationId)
    } else {
      val notification = buildNotification(
        requestCode = notificationId,
        packageName = packageName,
        appDetails = appDetails,
        progress = when (state) {
          is State.Downloading -> max(state.progress, 0)
          is State.Installing,
          State.ReadyToInstall,
          is State.Pending,
            -> -1

          else -> null
        },
        contentText = when (state) {
          State.Completed -> context.getString(R.string.notification_1_installed_title)
          is State.Downloading ->
            context.getString(
              R.string.notification_downloading_body, max(state.progress, 0).toString(),
              TextFormatter.formatBytes(size)
            )

          is State.Installing -> context.getString(R.string.notification_preparing_title)
          else -> ""
        },
        largeIcon = appIcon,
        notificationGroup = when (state) {
          is State.Downloading -> DOWNLOADING_NOTIFICATIONS_GROUP + appDetails?.name
          is State.Installing -> READY_TO_INSTALL_NOTIFICATIONS_GROUP
          else -> null
        }
      )

      notification?.let { showNotification(notificationId, notification) }
    }
  }

  fun showWaitingForDownloadNotification(
    packageName: String,
    appDetails: AppDetails?,
    appIcon: Bitmap?
  ) {
    val notificationId = stringToIntConverter.getStringId(packageName)

    val notification = buildNotification(
      requestCode = notificationId,
      packageName = packageName,
      appDetails = appDetails,
      progress = -1,
      contentText = context.getString(R.string.notification_waiting_body),
      largeIcon = appIcon,
      notificationGroup = null
    )

    notification?.let { showNotification(notificationId, notification) }
  }

  fun showReadyToInstallNotification(
    packageName: String,
    appDetails: AppDetails?,
    appIcon: Bitmap?
  ) {
    val notificationId = stringToIntConverter.getStringId(packageName)

    cancelNotification(notificationId)

    val notification = buildNotification(
      requestCode = notificationId,
      packageName = packageName,
      appDetails = appDetails,
      progress = null,
      contentTitle = context.getString(R.string.notification_ready_install_title),
      contentText = context.getString(R.string.notification_ready_install_body, appDetails?.name),
      channel = READY_TO_INSTALL_NOTIFICATION_CHANNEL_ID,
      largeIcon = appIcon,
      notificationGroup = READY_TO_INSTALL_NOTIFICATIONS_GROUP
    )

    notification?.let { showNotification(notificationId, notification) }
  }

  private fun cancelNotification(notificationId: Int) {
    if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {
      NotificationManagerCompat.from(context).cancel(notificationId)
    }
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

  fun showWaitingForWifiNotification(
    packageName: String,
    appDetails: AppDetails?,
    appIcon: Bitmap?
  ) {
    val notificationId = stringToIntConverter.getStringId(packageName)

    val notification = buildNotification(
      requestCode = notificationId,
      packageName = packageName,
      appDetails = appDetails,
      progress = -1,
      contentText = context.getString(R.string.notification_waiting_for_wifi_title),
      hasAction = true,
      largeIcon = appIcon,
      notificationGroup = null
    )

    notification?.let { showNotification(notificationId, notification) }
  }

  private fun buildNotification(
    requestCode: Int,
    packageName: String,
    appDetails: AppDetails?,
    progress: Int? = null,
    contentTitle: String? = null,
    contentText: String,
    hasAction: Boolean = false,
    channel: String = INSTALLER_NOTIFICATION_CHANNEL_ID,
    largeIcon: Bitmap?,
    notificationGroup: String?,
  ): Notification? = if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {

    val deepLink = buildAppViewDeepLinkUri(
      appSource = appDetails ?: AppSource.of(appId = null, packageName = packageName)
    )

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
      .setSmallIcon(notificationIcon)
      .setColor(colorToUse)
      .setContentTitle(contentTitle ?: appDetails?.name)
      .setContentText(contentText)
      .setLargeIcon(largeIcon)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)
      .setContentIntent(clickIntent)
      .setOnlyAlertOnce(true)
      .apply {
        when (progress) {
          null -> setStyle(
            NotificationCompat.BigPictureStyle()
          )

          -1 -> setProgress(0, 0, true)
          else -> setProgress(100, progress, false)
        }

        if (notificationGroup != null) {
          setGroup(notificationGroup)
        }

        if (hasAction) {
          addAction(
            /* icon = */ 0,
            /* title = */ context.getString(R.string.download_now_button),
            /* intent = */ PendingIntent.getActivity(
              /* context = */ context,
              /* requestCode = */ requestCode,
              /* intent = */ Intent(context, MainActivity::class.java)
                .putDeeplink(deepLink)
                .putExtra(ALLOW_METERED_DOWNLOAD_FOR_PACKAGE, packageName)
                .putNotificationSource(),
              /* flags = */ PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE
            )
          )
        }
      }.build()
  } else {
    null
  }
}
