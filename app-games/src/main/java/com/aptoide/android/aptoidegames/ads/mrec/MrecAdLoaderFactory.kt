package com.aptoide.android.aptoidegames.ads.mrec

import android.content.Context
import android.view.ViewGroup
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdLoaderCallbacks
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdLoaderFactory
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdLoaderPort

/**
 * Adapts one [MaxAdView] (format MREC, 300x250dp) to the shared [NativeAdLoaderPort] so the
 * Search MREC treatment runs through the same [com.aptoide.android.aptoidegames.ads.native_ads.NativeAdController]
 * as every native placement: same gates, retries, activity rules and disposal.
 *
 * The view is created only when the controller has passed its gates and decided to request, and
 * it stays one view per controller. Unity and every other network participate through their MAX
 * adapters; nothing here initializes a network SDK.
 */
class MrecAdLoaderFactory(
  context: Context,
  autoRefresh: Boolean,
) : NativeAdLoaderFactory {

  // The view outlives any one screen (it is owned by the Search landing ViewModel), so it must
  // never hold an Activity: only the application context is kept.
  private val appContext: Context = context.applicationContext

  private val refreshPolicy = MrecRefreshPolicy(autoRefresh)

  /** The hosted view once the controller created it; null before the first request. */
  var view: MaxAdView? = null
    private set

  /**
   * The view ready to be added to a new host. A Compose AndroidView that left composition keeps
   * its child attached, so the view is detached from any previous parent first; adding it while
   * it still has one would throw.
   */
  fun detachedView(): MaxAdView? = view?.also { (it.parent as? ViewGroup)?.removeView(it) }

  override fun create(
    adUnitId: String,
    placementName: String,
    callbacks: NativeAdLoaderCallbacks,
  ): NativeAdLoaderPort {
    val adView = MaxAdView(adUnitId, MaxAdFormat.MREC, appContext)
    adView.setPlacement(placementName)
    // Without this the SDK may ignore a stopAutoRefresh() issued before the first load.
    adView.setExtraParameter(MrecRefreshPolicy.ALLOW_PAUSE_PARAMETER, "true")
    adView.stopAutoRefresh()
    view = adView

    var loaded = false
    var activeNow = false
    fun apply(action: MrecRefreshAction) = when (action) {
      MrecRefreshAction.START -> adView.startAutoRefresh()
      MrecRefreshAction.STOP -> adView.stopAutoRefresh()
      MrecRefreshAction.NONE -> Unit
    }

    adView.setRevenueListener { ad -> callbacks.onRevenuePaid(ad) }
    adView.setListener(object : MaxAdViewAdListener {
      override fun onAdLoaded(ad: MaxAd) {
        loaded = true
        apply(refreshPolicy.onLoaded(activeNow))
        callbacks.onLoaded(ad)
      }

      override fun onAdLoadFailed(adUnitId: String, error: MaxError) =
        callbacks.onLoadFailed(error.code, error.message)

      override fun onAdClicked(ad: MaxAd) = callbacks.onClicked(ad)

      // Fullscreen-only callbacks for this format: intentionally ignored (no metrics from them).
      override fun onAdDisplayed(ad: MaxAd) = Unit
      override fun onAdHidden(ad: MaxAd) = Unit
      override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) = Unit
      override fun onAdExpanded(ad: MaxAd) = Unit
      override fun onAdCollapsed(ad: MaxAd) = Unit
    })

    return object : NativeAdLoaderPort {
      override fun load() = adView.loadAd()

      // The ad view is hosted directly; there is nothing to bind into a native template.
      override fun render(view: MaxNativeAdView, ad: MaxAd): Boolean = false

      override fun destroy(ad: MaxAd) = Unit

      override fun destroy() {
        adView.setListener(null)
        adView.setRevenueListener(null)
        adView.destroy()
        loaded = false
        if (view === adView) view = null
      }

      override fun onActiveChanged(active: Boolean) {
        activeNow = active
        apply(refreshPolicy.onActiveChanged(active, loaded))
      }
    }
  }
}
