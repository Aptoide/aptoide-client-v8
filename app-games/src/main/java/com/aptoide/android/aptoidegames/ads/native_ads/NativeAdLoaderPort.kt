package com.aptoide.android.aptoidegames.ads.native_ads

import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import javax.inject.Inject

/** Callbacks a [NativeAdLoaderPort] delivers to the controller, mirroring MAX's listener. */
interface NativeAdLoaderCallbacks {
  fun onLoaded(ad: MaxAd)
  fun onLoadFailed(errorCode: Int, message: String?)
  fun onClicked(ad: MaxAd)
  fun onExpired(ad: MaxAd)
  fun onRevenuePaid(ad: MaxAd)
}

/**
 * The slice of [MaxNativeAdLoader] the controller needs. Keeps the Android SDK out of the
 * lifecycle logic so it can run under plain JVM tests with a fake loader.
 */
interface NativeAdLoaderPort {
  fun load()
  fun render(view: MaxNativeAdView, ad: MaxAd): Boolean
  fun destroy(ad: MaxAd)
  fun destroy()

  /** The slot became active (visible, host resumed) or inactive. Used by auto-refreshing formats. */
  fun onActiveChanged(active: Boolean) {}
}

fun interface NativeAdLoaderFactory {
  fun create(adUnitId: String, placementName: String, callbacks: NativeAdLoaderCallbacks): NativeAdLoaderPort
}

/** Production factory: one [MaxNativeAdLoader] per controller attempt cycle. */
class MaxNativeAdLoaderFactory @Inject constructor() : NativeAdLoaderFactory {

  override fun create(
    adUnitId: String,
    placementName: String,
    callbacks: NativeAdLoaderCallbacks,
  ): NativeAdLoaderPort {
    val loader = MaxNativeAdLoader(adUnitId)
    loader.setPlacement(placementName)
    loader.setRevenueListener { ad -> callbacks.onRevenuePaid(ad) }
    loader.setNativeAdListener(object : MaxNativeAdListener() {
      override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) =
        callbacks.onLoaded(ad)

      override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) =
        callbacks.onLoadFailed(error.code, error.message)

      override fun onNativeAdClicked(ad: MaxAd) = callbacks.onClicked(ad)

      override fun onNativeAdExpired(ad: MaxAd) = callbacks.onExpired(ad)
    })
    return object : NativeAdLoaderPort {
      override fun load() = loader.loadAd()
      override fun render(view: MaxNativeAdView, ad: MaxAd): Boolean = loader.render(view, ad)
      override fun destroy(ad: MaxAd) = loader.destroy(ad)
      override fun destroy() = loader.destroy()
    }
  }
}
