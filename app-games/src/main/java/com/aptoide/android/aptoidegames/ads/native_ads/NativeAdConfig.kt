package com.aptoide.android.aptoidegames.ads.native_ads

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.ads.AdGeoProvider
import com.aptoide.android.aptoidegames.ads.AdsDefaults
import java.util.Locale

/**
 * Remote Config driven settings for one native ad placement. Keys are prefixed with
 * [NativeAdPlacement.flagPrefix], e.g. `home_native_enabled`, `browse_native_category_position`.
 *
 * - `<prefix>_enabled` is the kill switch. It is intentionally NOT present in
 *   remote_config_defaults.xml. Search MREC, deep Home and browse lists default to enabled in
 *   both build types. Other placements default to enabled in debug and disabled in release.
 * - `<prefix>_position`: for list slots, the number of organic items rendered before the ad;
 *   for the home feed, the zero-based bundle index the ad is inserted before.
 * - `<prefix>_excluded_geos` geo blocklist. Like the kill switch it is NOT in
 *   remote_config_defaults.xml: without the key, release builds use [AdsDefaults.EXCLUDED_GEOS]
 *   (consent geos) and debug builds exclude nothing, so placements are testable from Europe
 *   before the key exists in Firebase.
 * - `<prefix>_min_content_length` minimum length (characters) of the content the slot is attached
 *   to, below which no ad is requested (default 0 = always). Used by the bottom app detail slot.
 * - `<prefix>_min_gap` minimum number of items between this slot and the previous ad slot on the
 *   same screen (default 3). Used by the deep home slot.
 */
data class NativeAdConfig(
  val enabled: Boolean,
  val position: Int,
  val excludedGeos: Set<String>,
  val minContentLength: Int,
  val minGap: Int,
) {

  /**
   * Whether ads may be requested for [geo]. With an active blocklist (every release build unless
   * Remote Config overrides it) an unknown country is NOT eligible: a device whose country cannot
   * be resolved must not bypass the consent-geo block. Debug builds have an empty blocklist.
   */
  fun isGeoEligible(geo: String): Boolean {
    if (excludedGeos.isEmpty()) return true
    if (geo == AdGeoProvider.UNKNOWN_GEO) return false
    return geo.uppercase(Locale.US) !in excludedGeos
  }

  fun isContentLongEnough(contentLength: Int): Boolean = contentLength >= minContentLength

  companion object {
    const val DEFAULT_POSITION = 2
    const val DEFAULT_MIN_CONTENT_LENGTH = 0
    const val DEFAULT_MIN_GAP = 3
    val DEFAULT_ENABLED: Boolean = BuildConfig.DEBUG
    val DEFAULT_EXCLUDED_GEOS: Set<String> =
      if (BuildConfig.DEBUG) emptySet() else AdsDefaults.EXCLUDED_GEOS

    fun enabledKey(placement: NativeAdPlacement) = "${placement.flagPrefix}_enabled"
    fun positionKey(placement: NativeAdPlacement) = "${placement.flagPrefix}_position"
    fun excludedGeosKey(placement: NativeAdPlacement) = "${placement.flagPrefix}_excluded_geos"
    fun minContentLengthKey(placement: NativeAdPlacement) =
      "${placement.flagPrefix}_min_content_length"
    fun minGapKey(placement: NativeAdPlacement) = "${placement.flagPrefix}_min_gap"

    fun defaultEnabled(
      placement: NativeAdPlacement,
      isDebug: Boolean = DEFAULT_ENABLED,
    ): Boolean = placement == NativeAdPlacement.SEARCH_MREC ||
      placement == NativeAdPlacement.HOME_DEEP ||
      placement.isBrowseList ||
      (isDebug && placement.debugEnabledByDefault)

    suspend fun from(featureFlags: FeatureFlags, placement: NativeAdPlacement): NativeAdConfig {
      val excludedGeos = (featureFlags.getStringListOrNull(excludedGeosKey(placement))
        ?: DEFAULT_EXCLUDED_GEOS.toList())
        .map { it.uppercase(Locale.US) }
        .toSet()

      return NativeAdConfig(
        enabled = featureFlags.getFlag(enabledKey(placement), defaultEnabled(placement)),
        position = featureFlags.getInt(positionKey(placement), placement.defaultPosition)
          .coerceAtLeast(0),
        excludedGeos = excludedGeos,
        minContentLength = featureFlags.getInt(
          minContentLengthKey(placement), DEFAULT_MIN_CONTENT_LENGTH
        ).coerceAtLeast(0),
        minGap = featureFlags.getInt(minGapKey(placement), DEFAULT_MIN_GAP).coerceAtLeast(0),
      )
    }
  }
}
