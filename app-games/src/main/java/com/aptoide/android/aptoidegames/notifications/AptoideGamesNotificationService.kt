package com.aptoide.android.aptoidegames.notifications

import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.aptoide.android.aptoidegames.firebase.FirebaseNotificationBuilder
import com.aptoide.android.aptoidegames.markAsAhab
import com.aptoide.android.aptoidegames.notifications.analytics.FirebaseNotificationAnalytics
import com.aptoide.android.aptoidegames.putNotificationSource
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AptoideGamesNotificationsService : FirebaseMessagingService() {

  @Inject
  lateinit var firebaseNotificationAnalytics: FirebaseNotificationAnalytics

  @Inject
  lateinit var firebaseNotificationBuilder: FirebaseNotificationBuilder

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    Timber.d("New Token: $token")
  }

  override fun onMessageReceived(message: RemoteMessage) {
    super.onMessageReceived(message)
    if (message.notification != null) {
      firebaseNotificationBuilder.showFirebaseNotification(message)
    }
  }

  override fun handleIntent(intent: Intent?) {
    super.handleIntent(intent?.putNotificationSource()?.markAsAhab())

    intent?.extras?.toFirebaseNotificationAnalyticsInfo()?.let {
      firebaseNotificationAnalytics.sendNotificationReceived(
        notificationAnalyticsInfo = it,
        hasNotificationPermissions = NotificationManagerCompat.from(applicationContext)
          .areNotificationsEnabled()
      )
    }
  }
}
