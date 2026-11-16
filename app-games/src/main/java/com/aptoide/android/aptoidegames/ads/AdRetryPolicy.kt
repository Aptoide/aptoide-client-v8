package com.aptoide.android.aptoidegames.ads

/**
 * Bounded exponential backoff for transient ad failures (SDK init not ready, no fill, network).
 *
 * Attempts are 1-based: the first retry after a failure is attempt 1. [delayForAttempt] returns
 * null once [maxAttempts] is exceeded, which ends the retry loop for that placement instance.
 * Deterministic on purpose so the schedule is unit-testable; callers add no jitter.
 */
data class AdRetryPolicy(
  val baseDelayMs: Long = 2_000L,
  val maxDelayMs: Long = 32_000L,
  val maxAttempts: Int = 4,
  /** Minimum time between re-evaluations of a non-permanent skip (flag off, geo excluded). */
  val reevaluateIntervalMs: Long = 60_000L,
) {

  init {
    require(baseDelayMs > 0) { "baseDelayMs must be positive" }
    require(maxDelayMs >= baseDelayMs) { "maxDelayMs must be >= baseDelayMs" }
    require(maxAttempts >= 0) { "maxAttempts must be >= 0" }
    require(reevaluateIntervalMs >= 0) { "reevaluateIntervalMs must be >= 0" }
  }

  /** Delay before retry number [attempt] (1-based), or null when retries are exhausted. */
  fun delayForAttempt(attempt: Int): Long? {
    if (attempt < 1 || attempt > maxAttempts) return null
    val shift = (attempt - 1).coerceAtMost(MAX_SHIFT)
    val raw = baseDelayMs shl shift
    return if (raw <= 0) maxDelayMs else raw.coerceAtMost(maxDelayMs)
  }

  fun canReevaluate(lastEvaluationAtMs: Long, nowMs: Long): Boolean =
    nowMs - lastEvaluationAtMs >= reevaluateIntervalMs

  companion object {
    private const val MAX_SHIFT = 20
    val DEFAULT = AdRetryPolicy()
  }
}
