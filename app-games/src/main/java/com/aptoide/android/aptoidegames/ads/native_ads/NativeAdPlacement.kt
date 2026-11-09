package com.aptoide.android.aptoidegames.ads.native_ads

import com.aptoide.android.aptoidegames.BuildConfig

/** Which manual MAX template renders the ad. See native_ad_view.xml / native_ad_compact_view.xml. */
enum class NativeAdTemplate { STANDARD, COMPACT }

/**
 * One entry per screen slot that renders a MAX native ad.
 *
 * @property flagPrefix prefix of the Remote Config keys (`<prefix>_enabled`, `<prefix>_position`,
 *   `<prefix>_excluded_geos`, ...) and of the analytics event names (`<prefix>_loaded`, ...).
 *   Keep every `<prefix>_impression` under Firebase's 40-character event name limit.
 * @property maxPlacementName the placement string reported to MAX for per-slot reporting. Several
 *   placements may share one MAX ad unit; the placement name keeps their reporting apart.
 * @property defaultPosition default for `<prefix>_position`: the number of organic items rendered
 *   before the ad (lists) or the bundle index the ad is inserted before (home).
 * @property debugEnabledByDefault whether a debug build treats a missing `<prefix>_enabled` key as
 *   enabled. Release defaults are defined by NativeAdConfig; Search MREC defaults on.
 */
enum class NativeAdPlacement(
  val flagPrefix: String,
  val maxPlacementName: String,
  val template: NativeAdTemplate,
  val defaultPosition: Int,
  val debugEnabledByDefault: Boolean = true,
) {
  /** Games home feed, rendered as a bundle before the bundle at the configured position. */
  HOME_BUNDLE(
    flagPrefix = "home_native",
    maxPlacementName = "home_bundle",
    template = NativeAdTemplate.COMPACT,
    defaultPosition = 2,
  ),

  /**
   * Optional second, deeper home slot. Shares the home ad unit. Only rendered when at least
   * `home_native_deep_min_gap` bundles separate it from [HOME_BUNDLE] and no sponsored bundle is
   * adjacent. Enabled by default in both build types; Remote Config can disable it.
   */
  HOME_DEEP(
    flagPrefix = "home_native_deep",
    maxPlacementName = "home_bundle_deep",
    template = NativeAdTemplate.COMPACT,
    defaultPosition = 6,
  ),

  /** Search tab landing screen, rendered below the recent / popular searches. */
  SEARCH_LANDING(
    flagPrefix = "search_native",
    maxPlacementName = "search_landing",
    template = NativeAdTemplate.STANDARD,
    defaultPosition = 0,
  ),

  /** App detail page, Details tab: right after the screenshots, before the release notes. */
  APP_DETAIL(
    flagPrefix = "appview_native",
    maxPlacementName = "app_detail",
    template = NativeAdTemplate.STANDARD,
    defaultPosition = 0,
  ),

  /**
   * App detail page, Details tab: after the description, only when the description is at least
   * `appview_native_bottom_min_content_length` characters so the two slots never sit side by side.
   * Shares the MAX ad unit with [APP_DETAIL]; the placement name tells them apart in reporting.
   */
  APP_DETAIL_BOTTOM(
    flagPrefix = "appview_native_bottom",
    maxPlacementName = "app_detail_bottom",
    template = NativeAdTemplate.STANDARD,
    defaultPosition = 0,
  ),

  /** Category detail list: one card after the configured number of organic rows. */
  CATEGORY_LIST(
    flagPrefix = "browse_native_category",
    maxPlacementName = "category_list",
    template = NativeAdTemplate.COMPACT,
    defaultPosition = 3,
  ),

  /** Top Charts tab: one unranked card after the configured number of ranked rows. */
  TOP_CHARTS(
    flagPrefix = "browse_native_top_charts",
    maxPlacementName = "top_charts",
    template = NativeAdTemplate.COMPACT,
    defaultPosition = 3,
  ),

  /**
   * Search landing MREC treatment (300x250dp), the alternative to [SEARCH_LANDING] for users in the
   * MREC arm of the format experiment. Its own MAX unit (`SEARCH_MREC_AD_UNIT_ID`), flags
   * (`search_mrec_enabled`, `search_mrec_excluded_geos`) and analytics prefix; subject to the same
   * distribution, unit, flag, geo and SDK gates as every native placement. Not rendered through
   * the native templates, so [template] is unused.
   */
  SEARCH_MREC(
    flagPrefix = "search_mrec",
    maxPlacementName = "search_landing_mrec",
    template = NativeAdTemplate.STANDARD,
    defaultPosition = 0,
  ),

  /** Ordinary (non-RTB) See All / collection lists: one card after the configured rows. */
  COLLECTION_LIST(
    flagPrefix = "browse_native_collection",
    maxPlacementName = "collection_list",
    template = NativeAdTemplate.COMPACT,
    defaultPosition = 3,
  );

  /** MAX ad unit id for this slot; blank disables the slot entirely. */
  val adUnitId: String
    get() = when (this) {
      HOME_BUNDLE, HOME_DEEP -> BuildConfig.HOME_NATIVE_AD_UNIT_ID
      SEARCH_LANDING -> BuildConfig.SEARCH_NATIVE_AD_UNIT_ID
      SEARCH_MREC -> BuildConfig.SEARCH_MREC_AD_UNIT_ID
      APP_DETAIL, APP_DETAIL_BOTTOM -> BuildConfig.APP_DETAIL_NATIVE_AD_UNIT_ID
      CATEGORY_LIST, TOP_CHARTS, COLLECTION_LIST -> BuildConfig.BROWSE_NATIVE_AD_UNIT_ID
    }

  /** Browsing placements share the Browse Native unit and the list card component. */
  val isBrowseList: Boolean
    get() = this == CATEGORY_LIST || this == TOP_CHARTS || this == COLLECTION_LIST

  companion object {
    /** Distinct ad unit ids to declare to the MAX SDK at initialization. */
    fun declaredAdUnitIds(): List<String> =
      entries.map { it.adUnitId }.filter { it.isNotBlank() }.distinct()
  }
}
