package com.aptoide.android.aptoidegames.settings.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

class SettingsAnalytics(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendDownloadOverWifiDisabled() = genericAnalytics.logEvent(
    name = "download_over_wifi_disabled",
    params = emptyMap()
  )

  fun sendDownloadOverWifiEnabled() = genericAnalytics.logEvent(
    name = "download_over_wifi_enabled",
    params = emptyMap()
  )

  fun sendSendFeedbackClicked() = genericAnalytics.logEvent(
    name = "send_feedback_clicked",
    params = emptyMap()
  )

  fun sendAGDevOptionsEnabled() = genericAnalytics.logEvent(
    name = "ag_dev_options_enabled",
    params = emptyMap()
  )
}
