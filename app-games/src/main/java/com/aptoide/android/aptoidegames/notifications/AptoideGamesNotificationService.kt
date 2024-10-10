package com.aptoide.android.aptoidegames.notifications

import android.content.Intent
import com.aptoide.android.aptoidegames.markAsAhab
import com.aptoide.android.aptoidegames.putNotificationSource
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AptoideGamesNotificationsService : FirebaseMessagingService() {

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    Timber.d("New Token: $token")
  }

  override fun handleIntent(intent: Intent?) {
    super.handleIntent(intent?.putNotificationSource()?.markAsAhab())
  }
}
