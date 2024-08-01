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
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.extensions.isAllowed
import cm.aptoide.pt.feature_apps.presentation.toPackageNameParam
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
  private val imageDownloader: ImageDownloader,
) {

  companion object {
    const val INSTALLER_NOTIFICATION_CHANNEL_ID = "installer_notification_channel"
    const val INSTALLER_NOTIFICATION_CHANNEL_NAME = "Installer Notification Channel"
    const val ALLOW_METERED_DOWNLOAD_FOR_PACKAGE = "allowMeteredDownloadForPackage"
  }

  init {
    setupNotificationChannel(context)
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
        vibrationPattern = LongArray(0)
      }

      notificationManager.createNotificationChannel(channel)
    }
  }

  suspend fun showInstallationStateNotification(
    packageName: String,
    appDetails: AppDetails?,
    state: State,
    progress: Int,
    size: Long,
  ) {
    val notificationId = stringToIntConverter.getStringId(packageName)

    if (state == State.CANCELED || state == State.ABORTED || state == State.OUT_OF_SPACE || state == State.FAILED) {
      cancelNotification(notificationId)
    } else {
      val notification = buildNotification(
        requestCode = notificationId,
        packageName = packageName,
        appDetails = appDetails,
        progress = when (state) {
          State.DOWNLOADING -> max(progress, 0)
          State.INSTALLING,
          State.READY_TO_INSTALL,
          State.PENDING,
          -> -1

          else -> null
        },
        contentText = when (state) {
          State.COMPLETED -> context.getString(R.string.notification_1_installed_title)
          State.DOWNLOADING ->
            context.getString(
              R.string.notification_downloading_body, max(progress, 0).toString(),
              TextFormatter.formatBytes(size)
            )

          State.INSTALLING -> context.getString(R.string.notification_installing_title)
          else -> ""
        }
      )

      notification?.let { showNotification(notificationId, notification) }
    }
  }

  suspend fun showWaitingForDownloadNotification(
    packageName: String,
    appDetails: AppDetails?,
  ) {
    val notificationId = stringToIntConverter.getStringId(packageName)

    val notification = buildNotification(
      requestCode = notificationId,
      packageName = packageName,
      appDetails = appDetails,
      contentText = context.getString(R.string.notification_waiting_body),
      progress = -1,
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

  suspend fun showWaitingForWifiNotification(
    packageName: String,
    appDetails: AppDetails?,
  ) {
    val notificationId = stringToIntConverter.getStringId(packageName)

    val notification = buildNotification(
      requestCode = notificationId,
      packageName = packageName,
      appDetails = appDetails,
      contentText = context.getString(R.string.notification_waiting_for_wifi_title),
      progress = -1,
      hasAction = true
    )

    notification?.let { showNotification(notificationId, notification) }
  }

  private suspend fun buildNotification(
    requestCode: Int,
    packageName: String,
    appDetails: AppDetails?,
    progress: Int? = null,
    contentText: String,
    hasAction: Boolean = false,
  ): Notification? = if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {

    val deepLink = buildAppViewDeepLinkUri(packageName.toPackageNameParam())

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

    NotificationCompat.Builder(context, INSTALLER_NOTIFICATION_CHANNEL_ID)
      .setShowWhen(true)
      .setSmallIcon(notificationIcon)
      .setColor(colorToUse)
      .setContentTitle(appDetails?.name)
      .setContentText(contentText)
      .setLargeIcon(imageDownloader.downloadImageFrom(appDetails?.iconUrl))
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)
      .setContentIntent(clickIntent)
      .apply {
        when (progress) {
          null -> setStyle(
            NotificationCompat.BigPictureStyle()
          )

          -1 -> setProgress(0, 0, true)
          else -> setProgress(100, progress, false)
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
