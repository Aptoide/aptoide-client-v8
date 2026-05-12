package com.aptoide.android.aptoidegames.app_open_ads

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

data class AppOpenConfig(
  val enabled: Boolean,
  val excludedGeos: Set<String>,
  val capWindow: Duration,
  val loadTimeout: Duration,
) {

  fun isGeoEligible(geo: String): Boolean = geo.uppercase(Locale.US) !in excludedGeos

  companion object {
    private const val ENABLED_KEY = "appopen_enabled"
    private const val EXCLUDED_GEOS_KEY = "appopen_excluded_geos"
    private const val CAP_WINDOW_MINUTES_KEY = "appopen_cap_window_minutes"
    private const val LOAD_TIMEOUT_MS_KEY = "appopen_load_timeout_ms"

    private const val DEFAULT_ENABLED = false
    private const val DEFAULT_CAP_WINDOW_MINUTES = 30
    private const val DEFAULT_LOAD_TIMEOUT_MS = 3_000

    private val DEFAULT_EXCLUDED_GEOS = setOf(
      "US", "CA", "GB", "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR",
      "DE", "GR", "HU", "IE", "IT", "LV", "LT", "LU", "MT", "NL", "PL", "PT","RO",
      "SK", "SI", "ES", "SE"
    )

    suspend fun from(featureFlags: FeatureFlags): AppOpenConfig {
      val excludedGeos = (featureFlags.getStringListOrNull(EXCLUDED_GEOS_KEY)
        ?: DEFAULT_EXCLUDED_GEOS.toList())
        .map { it.uppercase(Locale.US) }
        .toSet()

      return AppOpenConfig(
        enabled = featureFlags.getFlag(ENABLED_KEY, DEFAULT_ENABLED),
        excludedGeos = excludedGeos,
        capWindow = featureFlags.getInt(
          CAP_WINDOW_MINUTES_KEY, DEFAULT_CAP_WINDOW_MINUTES,
        ).coerceAtLeast(0).minutes,
        loadTimeout = featureFlags.getInt(
          LOAD_TIMEOUT_MS_KEY, DEFAULT_LOAD_TIMEOUT_MS,
        ).coerceAtLeast(0).milliseconds,
      )
    }
  }
}
