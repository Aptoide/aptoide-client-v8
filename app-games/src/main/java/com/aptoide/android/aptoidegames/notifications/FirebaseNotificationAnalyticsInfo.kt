package com.aptoide.android.aptoidegames.notifications

data class FirebaseNotificationAnalyticsInfo(
  val messageId: String,
  val messageName: String,
  val messageDeviceTime: Long,
  val label: String? = null,
)
