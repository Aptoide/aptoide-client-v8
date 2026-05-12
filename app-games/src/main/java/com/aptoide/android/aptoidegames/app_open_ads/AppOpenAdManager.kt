package com.aptoide.android.aptoidegames.app_open_ads

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class AppOpenAdManager(
  appOpenAdUnitId: String,
  private val config: AppOpenConfig,
  private val geo: String,
  private val frequencyCap: AppOpenFrequencyCap,
  private val analytics: AppOpenAnalytics,
) : DefaultLifecycleObserver, MaxAdListener {

  private val appOpenAd = MaxAppOpenAd(appOpenAdUnitId)
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
  private var loadTimeoutJob: Job? = null
  private var hasAttempted = false
  private var isWaitingForAd = false
  private var displayedAtMs = 0L

  init {
    appOpenAd.setListener(this)
    loadAdIfNeeded()
    ProcessLifecycleOwner.get().lifecycle.addObserver(this)
  }

  override fun onStart(owner: LifecycleOwner) {
    if (hasAttempted) return
    hasAttempted = true
    attemptShow()
  }

  override fun onDestroy(owner: LifecycleOwner) {
    scope.cancel()
  }

  override fun onAdLoaded(ad: MaxAd) {
    if (isWaitingForAd) showAd()
  }

  override fun onAdDisplayed(ad: MaxAd) {
    val now = System.currentTimeMillis()
    displayedAtMs = now
    frequencyCap.recordImpression(now)
    analytics.sendShown(
      geo = geo,
      network = ad.networkName,
      ecpm = ad.revenue * ECPM_MULTIPLIER,
    )
  }

  override fun onAdHidden(ad: MaxAd) {
    if (displayedAtMs > 0L) {
      analytics.sendDismissed(
        geo = geo,
        dwellTimeMs = System.currentTimeMillis() - displayedAtMs,
      )
      displayedAtMs = 0L
    }
  }

  override fun onAdClicked(ad: MaxAd) = Unit

  override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
    if (isWaitingForAd) {
      analytics.sendFailed(geo = geo, errorCode = error.code.toString())
      clearAttempt()
    }
    Timber.w(
      "App open ad load failed for %s: %s (%s)",
      adUnitId,
      error.message,
      error.code,
    )
  }

  override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
    analytics.sendFailed(geo = geo, errorCode = error.code.toString())
    clearAttempt()
    Timber.w(
      "App open ad display failed: %s (%s)",
      error.message,
      error.code,
    )
  }

  private fun attemptShow() {
    if (!canShow()) return

    if (appOpenAd.isReady) {
      showAd()
      return
    }

    isWaitingForAd = true
    loadAdIfNeeded()
    loadTimeoutJob = scope.launch {
      delay(config.loadTimeout.inWholeMilliseconds)
      if (isWaitingForAd) {
        analytics.sendFailed(geo = geo, errorCode = ERROR_TIMEOUT)
        // Don't clear the attempt — keep waiting for the ad to load.
        // The ad may arrive a few seconds late and we still want to show it.
        // Only onAdLoadFailed truly gives up.
        loadTimeoutJob = null
      }
    }
  }

  private fun showAd() {
    if (!appOpenAd.isReady || appOpenAd.isShowing) {
      clearAttempt()
      return
    }

    clearAttempt()

    runCatching {
      appOpenAd.showAd(APP_OPEN_PLACEMENT)
    }.onFailure { throwable ->
      analytics.sendFailed(geo = geo, errorCode = ERROR_SHOW_EXCEPTION)
      Timber.e(throwable, "Failed to show app open ad.")
    }
  }

  private fun canShow(): Boolean {
    return !appOpenAd.isShowing &&
      frequencyCap.canShow(
        now = System.currentTimeMillis(),
        window = config.capWindow,
      )
  }

  private fun clearAttempt() {
    isWaitingForAd = false
    loadTimeoutJob?.cancel()
    loadTimeoutJob = null
  }

  private fun loadAdIfNeeded() {
    if (appOpenAd.isReady || appOpenAd.isLoading) return
    appOpenAd.loadAd()
  }

  companion object {
    private const val APP_OPEN_PLACEMENT = "app_open"
    private const val ECPM_MULTIPLIER = 1_000
    private const val ERROR_TIMEOUT = "timeout"
    private const val ERROR_SHOW_EXCEPTION = "show_exception"
  }
}
