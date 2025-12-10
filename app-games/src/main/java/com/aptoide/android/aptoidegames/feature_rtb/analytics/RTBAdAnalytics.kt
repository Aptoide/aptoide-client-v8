package com.aptoide.android.aptoidegames.feature_rtb.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

class RTBAdAnalytics(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendRTBAdLoadSuccess(
    initialUrl: String,
    finalUrl: String
  ) = genericAnalytics.logEvent(
    name = "rtb_ad_load_success",
    params = mapOf(
      "initial_url" to initialUrl,
      "final_url" to finalUrl
    )
  )

  fun sendRTBAdLoadError(
    initialUrl: String,
    errorMessage: String
  ) = genericAnalytics.logEvent(
    name = "rtb_ad_load_error",
    params = mapOf(
      "initial_url" to initialUrl,
      "error_message" to errorMessage
    )
  )
}
