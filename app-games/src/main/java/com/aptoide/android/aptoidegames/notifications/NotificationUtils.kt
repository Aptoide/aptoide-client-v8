package com.aptoide.android.aptoidegames.notifications

import android.os.Bundle
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.firebase.FirebaseConstants

fun String.getNotificationIcon() =
  when (this) {
    "dev" -> R.drawable.notification_icon_dev
    else -> R.drawable.notification_icon
  }

fun Bundle.toFirebaseNotificationAnalyticsInfo(): FirebaseNotificationAnalyticsInfo? {
  val messageId = getString(FirebaseConstants.FIREBASE_MESSAGE_ID)
  val messageName = getString(FirebaseConstants.FIREBASE_MESSAGE_NAME)

  return if (messageId != null && messageName != null) {
    FirebaseNotificationAnalyticsInfo(
      messageId = messageId,
      messageName = messageName,
      messageDeviceTime = System.currentTimeMillis(),
      label = getString(FirebaseConstants.FIREBASE_MESSAGE_LABEL)
    )
  } else null
}
