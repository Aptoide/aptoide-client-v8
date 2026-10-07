package com.aptoide.android.aptoidegames.ads.home_native

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.ads.AdsDefaults
import java.util.Locale

/**
 * Remote Config driven settings for the native ad bundle on the Games home feed.
 *
 * - `home_native_enabled`  kill switch. Intentionally NOT present in remote_config_defaults.xml so
 *                          that debug builds default to enabled (testable before the key exists in
 *                          the Firebase console) while release builds default to disabled.
 * - `home_native_position` zero-based bundle index the ad is inserted before (default 2 = third slot).
 * - `home_native_excluded_geos` geo blocklist. Like the kill switch it is NOT in
 *                          remote_config_defaults.xml: without the key, release builds use
 *                          [AdsDefaults.EXCLUDED_GEOS] (consent geos) and debug builds exclude
 *                          nothing, so the placement is testable from Europe before the key exists.
 */
data class HomeNativeAdConfig(
  val enabled: Boolean,
  val position: Int,
  val excludedGeos: Set<String>,
) {

  fun isGeoEligible(geo: String): Boolean = geo.uppercase(Locale.US) !in excludedGeos

  companion object {
    const val ENABLED_KEY = "home_native_enabled"
    const val POSITION_KEY = "home_native_position"
    const val EXCLUDED_GEOS_KEY = "home_native_excluded_geos"

    const val DEFAULT_POSITION = 2
    val DEFAULT_ENABLED: Boolean = BuildConfig.DEBUG
    val DEFAULT_EXCLUDED_GEOS: Set<String> =
      if (BuildConfig.DEBUG) emptySet() else AdsDefaults.EXCLUDED_GEOS

    suspend fun from(featureFlags: FeatureFlags): HomeNativeAdConfig {
      val excludedGeos = (featureFlags.getStringListOrNull(EXCLUDED_GEOS_KEY)
        ?: DEFAULT_EXCLUDED_GEOS.toList())
        .map { it.uppercase(Locale.US) }
        .toSet()

      return HomeNativeAdConfig(
        enabled = featureFlags.getFlag(ENABLED_KEY, DEFAULT_ENABLED),
        position = featureFlags.getInt(POSITION_KEY, DEFAULT_POSITION).coerceAtLeast(0),
        excludedGeos = excludedGeos,
      )
    }
  }
}
