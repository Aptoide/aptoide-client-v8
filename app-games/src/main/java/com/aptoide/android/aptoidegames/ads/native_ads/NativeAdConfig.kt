package com.aptoide.android.aptoidegames.ads.native_ads

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.ads.AdsDefaults
import java.util.Locale

/**
 * Remote Config driven settings for one native ad placement. Keys are prefixed with
 * [NativeAdPlacement.flagPrefix], e.g. `home_native_enabled`, `search_native_position`.
 *
 * - `<prefix>_enabled` is the kill switch. It is intentionally NOT present in
 *   remote_config_defaults.xml so that debug builds default to enabled (testable before the key
 *   exists in the Firebase console) while release builds default to disabled.
 * - `<prefix>_position` is the zero-based index the ad is inserted before, where the slot is a
 *   list (default 2 = third slot). Slots that render at a fixed spot ignore it.
 * - `<prefix>_excluded_geos` geo blocklist. Like the kill switch it is NOT in
 *   remote_config_defaults.xml: without the key, release builds use [AdsDefaults.EXCLUDED_GEOS]
 *   (consent geos) and debug builds exclude nothing, so placements are testable from Europe
 *   before the key exists in Firebase.
 * - `<prefix>_min_content_length` minimum length (characters) of the content the slot is attached
 *   to, below which no ad is requested (default 0 = always). Used by the bottom app detail slot.
 */
data class NativeAdConfig(
  val enabled: Boolean,
  val position: Int,
  val excludedGeos: Set<String>,
  val minContentLength: Int,
) {

  fun isGeoEligible(geo: String): Boolean = geo.uppercase(Locale.US) !in excludedGeos

  fun isContentLongEnough(contentLength: Int): Boolean = contentLength >= minContentLength

  companion object {
    const val DEFAULT_POSITION = 2
    const val DEFAULT_MIN_CONTENT_LENGTH = 0
    val DEFAULT_ENABLED: Boolean = BuildConfig.DEBUG
    val DEFAULT_EXCLUDED_GEOS: Set<String> =
      if (BuildConfig.DEBUG) emptySet() else AdsDefaults.EXCLUDED_GEOS

    fun enabledKey(placement: NativeAdPlacement) = "${placement.flagPrefix}_enabled"
    fun positionKey(placement: NativeAdPlacement) = "${placement.flagPrefix}_position"
    fun excludedGeosKey(placement: NativeAdPlacement) = "${placement.flagPrefix}_excluded_geos"
    fun minContentLengthKey(placement: NativeAdPlacement) =
      "${placement.flagPrefix}_min_content_length"

    suspend fun from(featureFlags: FeatureFlags, placement: NativeAdPlacement): NativeAdConfig {
      val excludedGeos = (featureFlags.getStringListOrNull(excludedGeosKey(placement))
        ?: DEFAULT_EXCLUDED_GEOS.toList())
        .map { it.uppercase(Locale.US) }
        .toSet()

      return NativeAdConfig(
        enabled = featureFlags.getFlag(enabledKey(placement), DEFAULT_ENABLED),
        position = featureFlags.getInt(positionKey(placement), DEFAULT_POSITION).coerceAtLeast(0),
        excludedGeos = excludedGeos,
        minContentLength = featureFlags.getInt(
          minContentLengthKey(placement), DEFAULT_MIN_CONTENT_LENGTH
        ).coerceAtLeast(0),
      )
    }
  }
}
