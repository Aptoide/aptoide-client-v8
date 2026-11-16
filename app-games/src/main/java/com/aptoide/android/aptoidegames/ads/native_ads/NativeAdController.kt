package com.aptoide.android.aptoidegames.ads.native_ads

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.aptoide.android.aptoidegames.ads.AdGeoProvider
import com.aptoide.android.aptoidegames.ads.AdRetryPolicy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

sealed interface NativeAdUiState {
  /** Nothing to show: disabled, ineligible geo, no fill, load error. The screen collapses the slot. */
  data object Hidden : NativeAdUiState

  /**
   * An ad is loaded. [ad] is the identity the view must render; a new ad means a new view.
   * [position] only matters for list slots (see [NativeAdConfig]).
   */
  data class Loaded(
    val ad: MaxAd,
    val position: Int,
    val minGap: Int = NativeAdConfig.DEFAULT_MIN_GAP,
  ) : NativeAdUiState
}

/**
 * Lifecycle of one native ad placement instance, independent of Android framework classes so it
 * can be unit tested with a fake [NativeAdLoaderFactory].
 *
 * Phases: idle → evaluating gates → loading → loaded, with these rules:
 * - Gates (distribution, unit id, Remote Config, content length, geo, SDK init) are re-evaluated
 *   on every attempt. Nothing is latched before a gate: a flag that was off, a geo lookup that was
 *   unknown or an SDK that was not ready yet can be re-checked by a later [requestLoad].
 * - Permanent skips (distribution off, blank unit id, content too short) end the instance.
 *   Non-permanent skips (flag off, geo excluded) can be re-evaluated after
 *   [AdRetryPolicy.reevaluateIntervalMs].
 * - Transient failures (SDK init, load failure) retry with [AdRetryPolicy]'s bounded backoff.
 *   Retries only run while the slot is [active]; while inactive they are deferred and resume on
 *   the next [setActive] (true), so an offscreen or backgrounded slot never spams requests.
 * - An expired ad is destroyed and, if the slot is active, replaced by a fresh request; if not,
 *   the reload waits for the next activation.
 * - Each loaded ad is destroyed before the next one replaces it and on [release].
 */
class NativeAdController(
  private val placement: NativeAdPlacement,
  private val adUnitId: String,
  private val distributionEnabled: Boolean,
  private val featureFlags: FeatureFlags,
  private val geoProvider: AdGeoProvider,
  private val sdkInitializer: suspend () -> Boolean,
  private val loaderFactory: NativeAdLoaderFactory,
  private val analytics: NativeAdAnalytics,
  private val scope: CoroutineScope,
  private val mainDispatcher: CoroutineDispatcher,
  private val retryPolicy: AdRetryPolicy = AdRetryPolicy.DEFAULT,
  private val now: () -> Long = System::currentTimeMillis,
  /** False when the build has no MAX SDK key: a permanent skip, not a transient init failure. */
  private val hasSdkKey: () -> Boolean = { true },
  /** An SDK initialization that never completes is treated as a transient failure after this. */
  private val sdkInitTimeoutMs: Long = DEFAULT_SDK_INIT_TIMEOUT_MS,
) : NativeAdLoaderCallbacks {

  private val _uiState = MutableStateFlow<NativeAdUiState>(NativeAdUiState.Hidden)
  val uiState: StateFlow<NativeAdUiState> = _uiState.asStateFlow()

  private val _configuredPosition = MutableStateFlow<Int?>(null)

  /** The `<prefix>_position` the last gate evaluation read; null before the first evaluation. */
  val configuredPosition: StateFlow<Int?> = _configuredPosition.asStateFlow()

  private val _configuredMinGap = MutableStateFlow<Int?>(null)

  /** The `<prefix>_min_gap` the last gate evaluation read; null before the first evaluation. */
  val configuredMinGap: StateFlow<Int?> = _configuredMinGap.asStateFlow()

  private var phase: Phase = Phase.Idle
  private var active = false
  private var contentLength = Int.MAX_VALUE
  private var listSize: Int? = null
  private var requested = false
  private var locationCheck: ((position: Int, minGap: Int) -> Boolean)? = null
  private var retryNotBefore = 0L
  private var attempt = 0
  private var lastEvaluationAt = Long.MIN_VALUE
  private var retryJob: Job? = null
  private var released = false

  private var loader: NativeAdLoaderPort? = null
  private var loadedAd: MaxAd? = null
  private var renderedAd: MaxAd? = null
  private var position = placement.defaultPosition
  private var minGap = NativeAdConfig.DEFAULT_MIN_GAP
  private var geo: String = AdGeoProvider.UNKNOWN_GEO

  /** Last reason this instance did not request an ad, for the debug log and tests. */
  var lastSkipReason: String? = null
    private set

  /**
   * Marks the slot as attached and visible enough to show an ad (true) or offscreen / disposed
   * (false). Deferred retries and expiry reloads run only while active.
   */
  fun setActive(active: Boolean) {
    if (released || this.active == active) return
    this.active = active
    loader?.onActiveChanged(active)
    if (active) {
      // Activation re-requests whenever the slot was ever asked to load. requestLoad is idempotent
      // while a request is in flight or an ad is loaded, and rate-limits a re-evaluation after a
      // flag / geo skip or exhausted retries, so a screen that was skipped before it became active
      // gets another chance on activation without a new request from the UI.
      if (requested) requestLoad(contentLength, listSize, locationCheck)
    } else {
      retryJob?.cancel()
      retryJob = null
      // A retry that was waiting for its backoff is deferred: the next activation re-requests.
      if (phase is Phase.WaitingRetry) phase = Phase.Idle
    }
  }

  /**
   * Asks for an ad. Idempotent while a request is in flight or an ad is loaded. After a skip or
   * exhausted retries, a later call re-evaluates the gates when the policy allows it.
   */
  fun requestLoad(
    contentLength: Int = Int.MAX_VALUE,
    listSize: Int? = null,
    locationCheck: ((position: Int, minGap: Int) -> Boolean)? = null,
  ) {
    if (released) return
    requested = true
    this.contentLength = contentLength
    this.listSize = listSize
    this.locationCheck = locationCheck ?: this.locationCheck
    when (val current = phase) {
      Phase.Evaluating, Phase.Loading, Phase.Loaded, is Phase.WaitingRetry -> return
      is Phase.Skipped -> {
        if (current.permanent) return
        if (!retryPolicy.canReevaluate(lastEvaluationAt, now())) return
      }
      Phase.Exhausted -> {
        if (!retryPolicy.canReevaluate(lastEvaluationAt, now())) return
        attempt = 0
      }
      Phase.Idle -> Unit
    }
    // A retry that was deferred (slot inactive) still honours its backoff: toggling activity
    // must not turn the exponential schedule into back-to-back requests.
    val wait = retryNotBefore - now()
    if (wait > 0) {
      if (active) scheduleAttemptAfter(wait)
      return
    }
    launchAttempt()
  }

  private fun scheduleAttemptAfter(delayMs: Long) {
    phase = Phase.WaitingRetry(delayMs)
    retryJob = scope.launch {
      delay(delayMs)
      if (released || !active) {
        phase = Phase.Idle
        return@launch
      }
      launchAttempt()
    }
  }

  /** Renders [ad] into [view] if it is still the loaded ad; stale ads are ignored. */
  fun render(view: MaxNativeAdView, ad: MaxAd): Boolean =
    bind(ad) { loader -> loader.render(view, ad) }

  /**
   * Binds [ad] through [bindWith] when it is still the loaded ad. A successful bind is reported
   * once per ad as `<prefix>_rendered`; it means the assets are in a view, not that the user saw
   * it. Paid impressions keep their own event from the revenue callback.
   */
  internal fun bind(ad: MaxAd, bindWith: (NativeAdLoaderPort) -> Boolean): Boolean {
    if (released) return false
    val current = loadedAd ?: return false
    if (current !== ad) return false
    val loader = loader ?: return false
    val bound = bindWith(loader)
    if (bound && renderedAd !== ad) {
      renderedAd = ad
      analytics.sendRendered(geo, ad.networkName)
    }
    return bound
  }

  /** Destroys the loaded ad and loader. Safe to call more than once. */
  fun release() {
    if (released) return
    released = true
    retryJob?.cancel()
    retryJob = null
    destroyLoadedAd()
    // The loader reference is kept so a response that arrives after release can still be
    // disposed through it (see onLoaded); MAX tolerates destroy(ad) after destroy().
    loader?.destroy()
    phase = Phase.Idle
    _uiState.value = NativeAdUiState.Hidden
  }

  private fun launchAttempt() {
    phase = Phase.Evaluating
    lastEvaluationAt = now()
    scope.launch { evaluateAndLoad() }
  }

  private suspend fun evaluateAndLoad() {
    try {
      evaluate()
    } catch (cancelled: CancellationException) {
      // Real cancellation (the scope is going away) propagates. A CancellationException while
      // this coroutine is still active is a nested timeout inside a gate: a transient failure.
      if (!currentCoroutineContext().isActive) throw cancelled
      onGateFailure(cancelled)
    } catch (error: Throwable) {
      onGateFailure(error)
    }
  }

  /**
   * Nothing in the gate path may crash the app or leave the slot stuck in Evaluating: a failure
   * is reported and retried with the normal bounded backoff.
   */
  private fun onGateFailure(error: Throwable) {
    if (released) return
    Timber.e(error, "Native ad %s: gate evaluation failed", placement.name)
    analytics.sendFailed(geo, EXCEPTION_ERROR, attempt)
    scheduleRetry()
  }

  private suspend fun evaluate() {
    if (!distributionEnabled) return skip(SKIP_DISTRIBUTION_DISABLED, permanent = true)
    if (adUnitId.isBlank()) return skip("no_ad_unit_id", permanent = true)

    val config = NativeAdConfig.from(featureFlags, placement)
    position = config.position
    minGap = config.minGap
    _configuredPosition.value = config.position
    _configuredMinGap.value = config.minGap
    if (!config.enabled) return skip("flag_disabled", permanent = false)
    if (!config.isContentLongEnough(contentLength)) {
      return skip("content_too_short", permanent = true)
    }

    // List slots: the effective configured position must fit the list that is actually shown, or
    // the ad would be requested and then never inserted. The list may grow later (paging), so
    // this skip is not rate limited like the flag and geo skips.
    listSize?.let { size ->
      if (!NativeAdListPlan.shouldRequest(size, config.position)) {
        return skip("list_too_short", permanent = false, reevaluateNow = true)
      }
    }

    // Slots with a computed insertion spot (deep home slot): no request when no valid spot exists
    // for the effective remote position and gap. The feed may change, so re-evaluate immediately.
    locationCheck?.let { check ->
      if (!check(config.position, config.minGap)) {
        return skip("no_valid_location", permanent = false, reevaluateNow = true)
      }
    }

    if (geo == AdGeoProvider.UNKNOWN_GEO) geo = geoProvider.geo()
    if (!config.isGeoEligible(geo)) return skip("geo_excluded", permanent = false)

    if (!hasSdkKey()) return skip("no_sdk_key", permanent = true)

    val sdkReady = withTimeoutOrNull(sdkInitTimeoutMs) { sdkInitializer() } == true
    if (!sdkReady) {
      Timber.w("Native ad %s: MAX SDK not initialized (attempt %d)", placement.name, attempt)
      analytics.sendFailed(geo, SDK_INIT_ERROR, attempt)
      scheduleRetry()
      return
    }

    if (released) return
    withContext(mainDispatcher) {
      if (released) return@withContext
      if (!active) {
        // The slot went offscreen while the gates were being evaluated: hold the request until
        // the slot is active again instead of loading for a screen the user has left.
        phase = Phase.Idle
        return@withContext
      }
      val loader = loader
        ?: loaderFactory.create(adUnitId, placement.maxPlacementName, this@NativeAdController)
          .also {
            loader = it
            it.onActiveChanged(active)
          }
      phase = Phase.Loading
      lastSkipReason = null
      analytics.sendRequested(geo, attempt)
      loader.load()
    }
  }

  private fun skip(reason: String, permanent: Boolean, reevaluateNow: Boolean = false) {
    val changed = lastSkipReason != reason
    lastSkipReason = reason
    phase = if (reevaluateNow) Phase.Idle else Phase.Skipped(permanent)
    // A skip that repeats with the same reason is not reported again.
    // Builds that ship no ads (distribution_disabled) stay silent: no events for ads never served.
    if (changed && reason != SKIP_DISTRIBUTION_DISABLED) {
      analytics.sendSkipped(reason, geo.takeIf { it != AdGeoProvider.UNKNOWN_GEO })
    }
    Timber.d("Native ad %s not requested: %s", placement.name, reason)
  }

  private fun scheduleRetry() {
    if (released) return
    val nextAttempt = attempt + 1
    val delayMs = retryPolicy.delayForAttempt(nextAttempt)
    if (delayMs == null) {
      phase = Phase.Exhausted
      Timber.d("Native ad %s: retries exhausted after %d attempts", placement.name, attempt)
      return
    }
    attempt = nextAttempt
    retryNotBefore = now() + delayMs
    if (!active) {
      phase = Phase.Idle
      return
    }
    scheduleAttemptAfter(delayMs)
  }

  private fun destroyLoadedAd() {
    val ad = loadedAd ?: return
    loadedAd = null
    if (renderedAd === ad) renderedAd = null
    loader?.destroy(ad)
  }

  // --- loader callbacks (MAX delivers them on the main thread) ---

  override fun onLoaded(ad: MaxAd) {
    if (released) {
      loader?.destroy(ad)
      return
    }
    destroyLoadedAd()
    loadedAd = ad
    attempt = 0
    retryNotBefore = 0L
    phase = Phase.Loaded
    analytics.sendLoaded(geo, ad.networkName)
    _uiState.value = NativeAdUiState.Loaded(ad, position, minGap)
  }

  override fun onLoadFailed(errorCode: Int, message: String?) {
    if (released) return
    analytics.sendFailed(geo, errorCode.toString(), attempt)
    if (loadedAd != null) {
      // A failed refresh (MREC auto-refresh) while an ad is on screen: the loaded ad stays
      // visible and the SDK owns its refresh cycle, so no hide and no extra request from here.
      Timber.d("%s: load failed while an ad is showing (%d), keeping it", placement.name, errorCode)
      return
    }
    Timber.w(
      "%s native ad load failed for %s: %s (%d)", placement.name, adUnitId, message, errorCode,
    )
    _uiState.value = NativeAdUiState.Hidden
    scheduleRetry()
  }

  override fun onClicked(ad: MaxAd) {
    analytics.sendClicked(geo, ad.networkName)
  }

  override fun onExpired(ad: MaxAd) {
    if (released) {
      loader?.destroy(ad)
      return
    }
    if (loadedAd !== ad) {
      // Expiry of an ad that was already replaced: dispose it, leave the current ad alone.
      loader?.destroy(ad)
      return
    }
    analytics.sendExpired(geo, ad.networkName)
    destroyLoadedAd()
    _uiState.value = NativeAdUiState.Hidden
    phase = Phase.Idle
    attempt = 0
    retryNotBefore = 0L
    // Inactive: the reload waits, the next activation re-requests.
    if (active) requestLoad(contentLength, listSize, locationCheck)
  }

  override fun onRevenuePaid(ad: MaxAd) {
    analytics.sendImpression(geo, ad.networkName, ad.revenue * ECPM_MULTIPLIER)
  }

  private sealed interface Phase {
    data object Idle : Phase
    data object Evaluating : Phase
    data object Loading : Phase
    data object Loaded : Phase
    data class WaitingRetry(val delayMs: Long) : Phase
    data class Skipped(val permanent: Boolean) : Phase
    data object Exhausted : Phase
  }

  companion object {
    const val SDK_INIT_ERROR = "sdk_init"
    const val EXCEPTION_ERROR = "exception"
    const val SKIP_DISTRIBUTION_DISABLED = "distribution_disabled"
    const val DEFAULT_SDK_INIT_TIMEOUT_MS = 15_000L
    private const val ECPM_MULTIPLIER = 1_000
  }
}
