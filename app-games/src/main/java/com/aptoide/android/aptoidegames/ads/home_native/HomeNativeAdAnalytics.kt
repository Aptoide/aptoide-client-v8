package com.aptoide.android.aptoidegames.ads.home_native

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

class HomeNativeAdAnalytics(private val genericAnalytics: GenericAnalytics) {

  fun sendLoaded(geo: String, network: String) = genericAnalytics.logEvent(
    name = "home_native_loaded",
    params = mapOf(
      "geo" to geo,
      "network" to network,
    )
  )

  fun sendImpression(geo: String, network: String, ecpm: Double) = genericAnalytics.logEvent(
    name = "home_native_impression",
    params = mapOf(
      "geo" to geo,
      "network" to network,
      "ecpm" to ecpm,
    )
  )

  fun sendClicked(geo: String, network: String) = genericAnalytics.logEvent(
    name = "home_native_clicked",
    params = mapOf(
      "geo" to geo,
      "network" to network,
    )
  )

  fun sendFailed(geo: String, errorCode: String) = genericAnalytics.logEvent(
    name = "home_native_failed",
    params = mapOf(
      "geo" to geo,
      "error_code" to errorCode,
    )
  )
}
