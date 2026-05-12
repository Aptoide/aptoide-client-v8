package com.aptoide.android.aptoidegames.app_open_ads

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

class AppOpenAnalytics(private val genericAnalytics: GenericAnalytics) {

  fun sendShown(
    geo: String,
    network: String,
    ecpm: Double,
  ) = genericAnalytics.logEvent(
    name = "appopen_shown",
    params = mapOf(
      "geo" to geo,
      "network" to network,
      "ecpm" to ecpm,
    )
  )

  fun sendDismissed(
    geo: String,
    dwellTimeMs: Long,
  ) = genericAnalytics.logEvent(
    name = "appopen_dismissed",
    params = mapOf(
      "geo" to geo,
      "dwell_time_ms" to dwellTimeMs,
    )
  )

  fun sendFailed(
    geo: String,
    errorCode: String,
  ) = genericAnalytics.logEvent(
    name = "appopen_failed",
    params = mapOf(
      "geo" to geo,
      "error_code" to errorCode,
    )
  )
}
