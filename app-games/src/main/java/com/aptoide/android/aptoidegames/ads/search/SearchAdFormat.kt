package com.aptoide.android.aptoidegames.ads.search

import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdAnalytics

/**
 * The two formats under test on the Search landing. Exactly one is requested per screen
 * instance; [MREC] is the default when configured and [NATIVE] is the explicit control/fallback.
 */
enum class SearchAdFormat(val variant: String, val analyticsFormat: String) {
  NATIVE(
    variant = NativeAdAnalytics.VARIANT_NATIVE_CONTROL,
    analyticsFormat = NativeAdAnalytics.FORMAT_NATIVE,
  ),
  MREC(variant = "mrec", analyticsFormat = NativeAdAnalytics.FORMAT_MREC),
}

/** Remote Config inputs of the Search format experiment. */
data class SearchAdExperimentConfig(
  /** Kill switch. Off means every user gets the native control. */
  val enabled: Boolean,
  /** Share of installations, 0..100, assigned to the MREC treatment. */
  val mrecAllocationPercent: Int,
  /** Whether the MREC may auto-refresh while visible. Off by default. */
  val mrecAutoRefresh: Boolean,
) {
  companion object {
    const val ENABLED_KEY = "search_ad_format_experiment_enabled"
    const val ALLOCATION_KEY = "search_mrec_allocation"
    const val AUTO_REFRESH_KEY = "search_mrec_auto_refresh"

    /** Default Search format for both brands; a missing MREC unit still selects native. */
    val DEFAULT = SearchAdExperimentConfig(
      enabled = true,
      mrecAllocationPercent = 100,
      mrecAutoRefresh = false,
    )

    /** Explicit rollback to the native control. */
    val DISABLED = SearchAdExperimentConfig(
      enabled = false,
      mrecAllocationPercent = 0,
      mrecAutoRefresh = false,
    )
  }
}

/** What the Search landing renders in its ad opportunity. */
data class SearchAdDecision(
  val format: SearchAdFormat,
  val mrecAdUnitId: String,
  val mrecAutoRefresh: Boolean,
) {
  /** Only the native arm may create a native ad view model. */
  val requestsNative: Boolean get() = format == SearchAdFormat.NATIVE

  /** Only the MREC arm may create an MREC controller. It never falls back to native. */
  val requestsMrec: Boolean get() = format == SearchAdFormat.MREC
}

/**
 * Pure assignment rules: a stable bucket per installation, the configured allocation, and the
 * safeguards that fall back to the native control before any request is made.
 */
object SearchAdFormatDecider {

  private const val SALT = "search_ad_format:"
  const val BUCKETS = 100

  /** Deterministic 0..99 bucket for an installation id; the same id always lands in one bucket. */
  fun bucket(stableId: String): Int {
    var hash = 0
    for (char in SALT + stableId) hash = 31 * hash + char.code
    return (hash and Int.MAX_VALUE) % BUCKETS
  }

  /**
   * [SearchAdFormat.MREC] when enabled with a configured unit and full allocation. Fractional
   * allocations additionally require a stable installation id for bucketing. Otherwise native.
   */
  fun decide(
    config: SearchAdExperimentConfig,
    mrecAdUnitId: String,
    stableId: String?,
  ): SearchAdFormat {
    if (!config.enabled) return SearchAdFormat.NATIVE
    if (mrecAdUnitId.isBlank()) return SearchAdFormat.NATIVE
    val allocation = config.mrecAllocationPercent.coerceIn(0, BUCKETS)
    if (allocation == 0) return SearchAdFormat.NATIVE
    if (allocation == BUCKETS) return SearchAdFormat.MREC
    val id = stableId?.takeIf { it.isNotBlank() } ?: return SearchAdFormat.NATIVE
    return if (bucket(id) < allocation) SearchAdFormat.MREC else SearchAdFormat.NATIVE
  }
}
