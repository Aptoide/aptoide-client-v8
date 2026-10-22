package com.aptoide.android.aptoidegames.ads.native_ads

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

/** Emits `<prefix>_loaded`, `<prefix>_impression`, `<prefix>_clicked`, `<prefix>_failed`. */
class NativeAdAnalytics(
  private val genericAnalytics: GenericAnalytics,
  placement: NativeAdPlacement,
) {

  private val prefix = placement.flagPrefix

  fun sendLoaded(geo: String, network: String) = genericAnalytics.logEvent(
    name = "${prefix}_loaded",
    params = mapOf(
      "geo" to geo,
      "network" to network,
    )
  )

  fun sendImpression(geo: String, network: String, ecpm: Double) = genericAnalytics.logEvent(
    name = "${prefix}_impression",
    params = mapOf(
      "geo" to geo,
      "network" to network,
      "ecpm" to ecpm,
    )
  )

  fun sendClicked(geo: String, network: String) = genericAnalytics.logEvent(
    name = "${prefix}_clicked",
    params = mapOf(
      "geo" to geo,
      "network" to network,
    )
  )

  fun sendFailed(geo: String, errorCode: String) = genericAnalytics.logEvent(
    name = "${prefix}_failed",
    params = mapOf(
      "geo" to geo,
      "error_code" to errorCode,
    )
  )
}
