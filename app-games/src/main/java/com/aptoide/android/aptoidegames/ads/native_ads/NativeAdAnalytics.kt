package com.aptoide.android.aptoidegames.ads.native_ads

import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics

/**
 * Placement funnel events, one prefix per placement (native) or format slot (MREC):
 * `<prefix>_skipped`, `<prefix>_requested`, `<prefix>_loaded`, `<prefix>_failed`,
 * `<prefix>_expired`, `<prefix>_rendered` (the ad view was bound / attached; not viewability),
 * `<prefix>_impression` (paid, from the revenue callback), `<prefix>_clicked`. MAX documents the
 * display callbacks as fullscreen-only, so no event is derived from them.
 *
 * Every event carries `format`, `version_code` and, when the slot belongs to an experiment,
 * `variant`, so the funnel can be read per placement, format, app version and cohort.
 * No user or device identifiers are attached beyond the country already used for geo gating.
 */
class NativeAdAnalytics(
  private val genericAnalytics: GenericAnalytics,
  private val prefix: String,
  private val format: String = FORMAT_NATIVE,
  private val variant: String? = null,
) {

  constructor(
    genericAnalytics: GenericAnalytics,
    placement: NativeAdPlacement,
    variant: String? = null,
  ) : this(genericAnalytics, placement.flagPrefix, FORMAT_NATIVE, variant)

  fun sendSkipped(reason: String, geo: String? = null) = log(
    "skipped",
    "reason" to reason,
    "geo" to geo,
  )

  fun sendRequested(geo: String, attempt: Int) = log(
    "requested",
    "geo" to geo,
    "attempt" to attempt,
  )

  fun sendLoaded(geo: String, network: String) = log(
    "loaded",
    "geo" to geo,
    "network" to network,
  )

  fun sendFailed(geo: String, errorCode: String, attempt: Int = 0) = log(
    "failed",
    "geo" to geo,
    "error_code" to errorCode,
    "attempt" to attempt,
  )

  fun sendExpired(geo: String, network: String) = log(
    "expired",
    "geo" to geo,
    "network" to network,
  )

  fun sendRendered(geo: String, network: String) = log(
    "rendered",
    "geo" to geo,
    "network" to network,
  )

  fun sendImpression(geo: String, network: String, ecpm: Double) = log(
    "impression",
    "geo" to geo,
    "network" to network,
    "ecpm" to ecpm,
  )

  fun sendClicked(geo: String, network: String) = log(
    "clicked",
    "geo" to geo,
    "network" to network,
  )

  private fun log(action: String, vararg params: Pair<String, Any?>) {
    val all = buildMap<String, Any> {
      put("format", format)
      put("version_code", BuildConfig.VERSION_CODE)
      variant?.let { put("variant", it) }
      params.forEach { (key, value) -> if (value != null) put(key, value) }
    }
    genericAnalytics.logEvent(name = "${prefix}_$action", params = all)
  }

  companion object {
    const val FORMAT_NATIVE = "native"
    const val VARIANT_NATIVE_CONTROL = "native_control"
    const val FORMAT_MREC = "mrec"
  }
}
