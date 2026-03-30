package com.aptoide.android.aptoidegames.notifications

import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.aptoide.android.aptoidegames.firebase.FirebaseNotificationBuilder
import com.aptoide.android.aptoidegames.gamesfeed.GamesFeedNotificationBuilder
import com.aptoide.android.aptoidegames.gamesfeed.presentation.GamesFeedManager
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

  @Inject
  lateinit var gamesFeedNotificationBuilder: GamesFeedNotificationBuilder

  companion object {
    private const val GAMES_FEED_TOPIC_PREFIX = "/topics/${GamesFeedManager.TOPIC_PREFIX}"
  }

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    Timber.d("New Token: $token")
  }

  override fun onMessageReceived(message: RemoteMessage) {
    super.onMessageReceived(message)
    if (message.notification == null) return

    if (isGamesFeedTopicMessage(message)) {
      gamesFeedNotificationBuilder.handleGamesFeedNotification(message)
    } else {
      firebaseNotificationBuilder.showFirebaseNotification(message)
    }
  }

  private fun isGamesFeedTopicMessage(message: RemoteMessage): Boolean {
    return message.from?.contains(GAMES_FEED_TOPIC_PREFIX) == true
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
