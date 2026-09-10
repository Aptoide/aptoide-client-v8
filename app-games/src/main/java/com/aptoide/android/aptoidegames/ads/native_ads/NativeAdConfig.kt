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
 * - `<prefix>_excluded_geos` falls back to `appopen_excluded_geos`, then to [AdsDefaults].
 */
data class NativeAdConfig(
  val enabled: Boolean,
  val position: Int,
  val excludedGeos: Set<String>,
) {

  fun isGeoEligible(geo: String): Boolean = geo.uppercase(Locale.US) !in excludedGeos

  companion object {
    private const val APPOPEN_EXCLUDED_GEOS_KEY = "appopen_excluded_geos"

    const val DEFAULT_POSITION = 2
    val DEFAULT_ENABLED: Boolean = BuildConfig.DEBUG

    fun enabledKey(placement: NativeAdPlacement) = "${placement.flagPrefix}_enabled"
    fun positionKey(placement: NativeAdPlacement) = "${placement.flagPrefix}_position"
    fun excludedGeosKey(placement: NativeAdPlacement) = "${placement.flagPrefix}_excluded_geos"

    suspend fun from(featureFlags: FeatureFlags, placement: NativeAdPlacement): NativeAdConfig {
      val excludedGeos = (featureFlags.getStringListOrNull(excludedGeosKey(placement))
        ?: featureFlags.getStringListOrNull(APPOPEN_EXCLUDED_GEOS_KEY)
        ?: AdsDefaults.EXCLUDED_GEOS.toList())
        .map { it.uppercase(Locale.US) }
        .toSet()

      return NativeAdConfig(
        enabled = featureFlags.getFlag(enabledKey(placement), DEFAULT_ENABLED),
        position = featureFlags.getInt(positionKey(placement), DEFAULT_POSITION).coerceAtLeast(0),
        excludedGeos = excludedGeos,
      )
    }
  }
}
