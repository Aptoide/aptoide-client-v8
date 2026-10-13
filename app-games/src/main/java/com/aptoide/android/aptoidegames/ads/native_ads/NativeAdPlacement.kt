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
  SEARCH_LANDING(flagPrefix = "search_native", maxPlacementName = "search_landing"),

  /** App detail page, Details tab: between the screenshots / what's new block and the description. */
  APP_DETAIL(flagPrefix = "appview_native", maxPlacementName = "app_detail"),

  /**
   * App detail page, Details tab: after the description, only when the description is at least
   * `appview_native_bottom_min_content_length` characters so the two slots never sit side by side.
   * Shares the MAX ad unit with [APP_DETAIL]; the placement name tells them apart in reporting.
   */
  APP_DETAIL_BOTTOM(flagPrefix = "appview_native_bottom", maxPlacementName = "app_detail_bottom");

  /** MAX ad unit id for this slot; blank disables the slot entirely. */
  val adUnitId: String
    get() = when (this) {
      HOME_BUNDLE -> BuildConfig.HOME_NATIVE_AD_UNIT_ID
      SEARCH_LANDING -> BuildConfig.SEARCH_NATIVE_AD_UNIT_ID
      APP_DETAIL, APP_DETAIL_BOTTOM -> BuildConfig.APP_DETAIL_NATIVE_AD_UNIT_ID
    }
}
