package com.aptoide.android.aptoidegames.play_and_earn.presentation.service

import androidx.annotation.StringRes
import com.aptoide.android.aptoidegames.R

/** The tracking service's notification text. */
data class PaETrackingNotificationText(
  @StringRes val title: Int,
  @StringRes val body: Int,
)

/**
 * What the tracking notification says. While the game in front waits for the user to turn a VPN
 * off (AND-964) the service keeps running but counts nothing, so it says what to do instead of
 * claiming that play time is tracked.
 */
fun trackingNotificationText(waitingForTheUser: Boolean): PaETrackingNotificationText =
  if (waitingForTheUser) {
    PaETrackingNotificationText(
      title = R.string.play_and_earn_refused_title,
      body = R.string.play_and_earn_refused_vpn_body,
    )
  } else {
    PaETrackingNotificationText(
      title = R.string.play_and_earn_notification_recording_title,
      body = R.string.play_and_earn_notification_recording_body,
    )
  }
