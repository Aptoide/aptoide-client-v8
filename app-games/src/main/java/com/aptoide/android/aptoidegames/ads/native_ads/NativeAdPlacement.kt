package com.aptoide.android.aptoidegames.ads.native_ads

import com.aptoide.android.aptoidegames.BuildConfig

/**
 * One entry per screen slot that renders a MAX native ad with the shared template.
 *
 * @property flagPrefix prefix of the Remote Config keys (`<prefix>_enabled`, `<prefix>_position`,
 *   `<prefix>_excluded_geos`) and of the analytics event names (`<prefix>_loaded`, ...).
 * @property maxPlacementName the placement string reported to MAX for per-slot reporting.
 */
enum class NativeAdPlacement(
  val flagPrefix: String,
  val maxPlacementName: String,
) {
  /** Games home feed, rendered as a bundle before the bundle at the configured position. */
  HOME_BUNDLE(flagPrefix = "home_native", maxPlacementName = "home_bundle"),

  /** Search tab landing screen, rendered below the recent / popular searches. */
  SEARCH_LANDING(flagPrefix = "search_native", maxPlacementName = "search_landing");

  /** MAX ad unit id for this slot; blank disables the slot entirely. */
  val adUnitId: String
    get() = when (this) {
      HOME_BUNDLE -> BuildConfig.HOME_NATIVE_AD_UNIT_ID
      SEARCH_LANDING -> BuildConfig.SEARCH_NATIVE_AD_UNIT_ID
    }
}
