package com.aptoide.android.aptoidegames.notifications

import android.os.Bundle
import com.aptoide.android.aptoidegames.R

fun String.getNotificationIcon() =
  when (this) {
    "dev" -> R.drawable.notification_icon_dev
    else -> R.drawable.notification_icon
  }

//TODO: recheck this code in case [firebase-messaging] dependency is updated.
//The code relies on internal data names defined by firebase messaging.
fun Bundle.toFirebaseNotificationAnalyticsInfo(): FirebaseNotificationAnalyticsInfo? {
  val messageId = getString("google.message_id")
  val messageName = getString("google.c.a.c_l")

  return if (messageId != null && messageName != null) {
    FirebaseNotificationAnalyticsInfo(
      messageId = messageId,
      messageName = messageName,
      messageDeviceTime = System.currentTimeMillis(),
      label = getString("google.c.a.m_l")
    )
  } else null
}
