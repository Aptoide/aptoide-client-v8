package com.aptoide.android.aptoidegames.ads.native_ads

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import java.lang.reflect.Proxy

/** A [MaxAd] stand-in: only the members the controller and analytics read are answered. */
fun fakeMaxAd(network: String = "FakeNetwork", revenue: Double = 0.001): MaxAd =
  Proxy.newProxyInstance(
    MaxAd::class.java.classLoader,
    arrayOf(MaxAd::class.java),
  ) { proxy, method, args ->
    when (method.name) {
      "getNetworkName" -> network
      "getRevenue" -> revenue
      "getAdUnitId" -> "unit"
      "hashCode" -> System.identityHashCode(proxy)
      "equals" -> proxy === args?.firstOrNull()
      "toString" -> "FakeMaxAd($network)"
      else -> null
    }
  } as MaxAd

class FakeFeatureFlags(
  val booleans: MutableMap<String, Boolean> = mutableMapOf(),
  val ints: MutableMap<String, Int> = mutableMapOf(),
  val lists: MutableMap<String, List<String>> = mutableMapOf(),
) : FeatureFlags {
  override suspend fun getFlag(key: String, default: Boolean): Boolean = booleans[key] ?: default
  override suspend fun getInt(key: String, default: Int): Int = ints[key] ?: default
  override suspend fun getStringListOrNull(key: String): List<String>? = lists[key]
}

class RecordingAnalytics : AnalyticsSender {
  val events = mutableListOf<Pair<String, Map<String, Any?>?>>()

  override fun logEvent(name: String, params: Map<String, Any?>?) {
    events += name to params
  }

  fun names(): List<String> = events.map { it.first }

  fun generic(): GenericAnalytics = GenericAnalytics(this)
}

class FakeLoader(val adUnitId: String, val placementName: String, val callbacks: NativeAdLoaderCallbacks) :
  NativeAdLoaderPort {
  var loadCalls = 0
  var destroyed = false
  val destroyedAds = mutableListOf<MaxAd>()
  var renderResult = true
  val onActiveChangedCalls = mutableListOf<Boolean>()

  override fun onActiveChanged(active: Boolean) {
    onActiveChangedCalls += active
  }

  override fun load() {
    loadCalls++
  }

  override fun render(view: MaxNativeAdView, ad: MaxAd): Boolean = renderResult
  override fun destroy(ad: MaxAd) {
    destroyedAds += ad
  }

  override fun destroy() {
    destroyed = true
  }
}

class FakeLoaderFactory : NativeAdLoaderFactory {
  val loaders = mutableListOf<FakeLoader>()
  val last: FakeLoader? get() = loaders.lastOrNull()
  val totalLoads: Int get() = loaders.sumOf { it.loadCalls }

  override fun create(
    adUnitId: String,
    placementName: String,
    callbacks: NativeAdLoaderCallbacks,
  ): NativeAdLoaderPort = FakeLoader(adUnitId, placementName, callbacks).also { loaders += it }
}
