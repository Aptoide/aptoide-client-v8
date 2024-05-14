package com.aptoide.android.aptoidegames.notifications

import com.aptoide.android.aptoidegames.R

fun String.getNotificationIcon() =
  when (this) {
    "dev" -> R.drawable.notification_icon_dev
    else -> R.drawable.notification_icon
  }
