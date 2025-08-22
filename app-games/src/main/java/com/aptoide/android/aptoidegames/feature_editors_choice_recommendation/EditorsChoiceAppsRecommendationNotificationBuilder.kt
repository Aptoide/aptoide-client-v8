package com.aptoide.android.aptoidegames.feature_editors_choice_recommendation

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
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.MainActivity
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.feature_apps.presentation.buildSeeMoreDeepLinkUri
import com.aptoide.android.aptoidegames.notifications.analytics.NotificationsAnalytics
import com.aptoide.android.aptoidegames.notifications.getNotificationIcon
import com.aptoide.android.aptoidegames.putDeeplink
import com.aptoide.android.aptoidegames.putNotificationSource
import com.aptoide.android.aptoidegames.putNotificationTag
import com.aptoide.android.aptoidegames.theme.Palette
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditorsChoiceAppsRecommendationNotificationBuilder @Inject constructor(
  @ApplicationContext private val context: Context,
  private val notificationsAnalytics: NotificationsAnalytics
) {

  companion object {
    const val EDITORS_CHOICE_RECOMMENDATION_NOTIFICATION_CHANNEL_ID =
      "editors_choice_recommendation_notification_channel"
    const val EDITORS_CHOICE_RECOMMENDATION_NOTIFICATION_CHANNEL_NAME =
      "Editors Choice Recommendation Notification Channel"
    const val EDITORS_CHOICE_RECOMMENDATION_NOTIFICATION_TAG =
      "editors_choice_recommendation_notification"
  }

  init {
    setupNotificationChannel(context)
  }

  private fun setupNotificationChannel(context: Context) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (notificationManager.getNotificationChannel(
        EDITORS_CHOICE_RECOMMENDATION_NOTIFICATION_CHANNEL_ID
      ) == null
    ) {
      val name = EDITORS_CHOICE_RECOMMENDATION_NOTIFICATION_CHANNEL_NAME
      val descriptionText = "Editors Choice Recommendation notification channel"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(
        EDITORS_CHOICE_RECOMMENDATION_NOTIFICATION_CHANNEL_ID,
        name,
        importance
      ).apply {
        description = descriptionText
        setSound(null, null)
      }

      notificationManager.createNotificationChannel(channel)
    }
  }

  fun showEditorsChoiceAppsRecommendationNotification() {
    val notificationId = "EditorsChoice".hashCode()
    val notification = buildNotification(
      requestCode = notificationId,
      contentTitle = context.resources.getString(R.string.editors_choice_recommendation_notification_title),
      contentText = context.resources.getString(R.string.editors_choice_recommendation_notification_body),
    )

    notification?.let {
      showNotification(
        notificationId = notificationId,
        notification = notification,
        notificationTag = EDITORS_CHOICE_RECOMMENDATION_NOTIFICATION_TAG,
      )
    }
  }

  @SuppressLint("MissingPermission")
  private fun showNotification(
    notificationId: Int,
    notification: Notification,
    notificationTag: String,
  ) {
    if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {
      notificationsAnalytics.sendNotificationReceived(notificationTag, null)
      notificationsAnalytics.sendEditorsChoiceNotificationShown()
      NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
  }

  private fun buildNotification(
    requestCode: Int,
    contentTitle: String? = null,
    contentText: String,
    channel: String = EDITORS_CHOICE_RECOMMENDATION_NOTIFICATION_CHANNEL_ID,
  ): Notification? = if (context.isAllowed(Manifest.permission.POST_NOTIFICATIONS)) {

    val deepLink = buildSeeMoreDeepLinkUri(
      context.resources.getString(R.string.fixed_bundle_editors_choice_title),
      "apps-group-editors-choice"
    )

    val clickIntent = PendingIntent.getActivity(
      context,
      requestCode,
      Intent(context, MainActivity::class.java)
        .putDeeplink(deepLink)
        .putNotificationSource()
        .putNotificationTag(EDITORS_CHOICE_RECOMMENDATION_NOTIFICATION_TAG),
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
