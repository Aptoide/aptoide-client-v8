package com.aptoide.android.aptoidegames.feature_rtb.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

class RTBAdAnalytics(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendRTBAdLoadSuccess(
    initialUrl: String,
    finalUrl: String,
    campaignId: String?
  ) = genericAnalytics.logEvent(
    name = "rtb_ad_load_success",
    params = buildMap {
      put("initial_url", initialUrl)
      put("final_url", finalUrl)
      campaignId?.let { put("campaign_id", it) }
    }
  )

  fun sendRTBAdLoadError(
    initialUrl: String,
    errorMessage: String,
    campaignId: String? = null,
    lastUrl: String? = null,
    lastErrorType: String? = null,
    lastErrorDescription: String? = null
  ) = genericAnalytics.logEvent(
    name = "rtb_ad_load_error",
    params = buildMap {
      put("initial_url", initialUrl)
      put("error_message", errorMessage)
      campaignId?.let { put("campaign_id", it) }
      lastUrl?.let { put("last_url", it) }
      lastErrorType?.let { put("last_error_type", it) }
      lastErrorDescription?.let { put("last_error_description", it) }
    }
  )
}
