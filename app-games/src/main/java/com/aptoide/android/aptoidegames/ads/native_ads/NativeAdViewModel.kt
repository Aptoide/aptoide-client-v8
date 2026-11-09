package com.aptoide.android.aptoidegames.ads.native_ads

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.aptoide.android.aptoidegames.ads.AdGeoProvider
import com.aptoide.android.aptoidegames.ads.AppLovinSdkInitializer
import com.aptoide.android.aptoidegames.ads.NATIVE_ADS_ENABLED
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

/**
 * Thin ViewModel over a [NativeAdController] for one [NativeAdPlacement] per screen instance.
 *
 * Each placement gets its own Hilt subclass so each screen has an independent ViewModel scope
 * and its own ad unit, flags and analytics prefix. The controller holds all lifecycle rules;
 * the ViewModel only ties it to the screen's lifetime and, for feed-style slots, starts it.
 *
 * Nothing is requested at construction: the screen calls [setSlotActive] and [startLoading] from
 * its own near-viewport + resumed signal, so pages that are left before the slot comes near never
 * request an ad.
 * @param variant experiment cohort label attached to analytics, when the slot is under test.
 */
abstract class NativeAdViewModel(
  val placement: NativeAdPlacement,
  featureFlags: FeatureFlags,
  sdkInitializer: AppLovinSdkInitializer,
  geoProvider: AdGeoProvider,
  loaderFactory: NativeAdLoaderFactory,
  genericAnalytics: GenericAnalytics,
  variant: String? = null,
) : ViewModel() {

  private val controller = NativeAdController(
    placement = placement,
    adUnitId = placement.adUnitId,
    distributionEnabled = NATIVE_ADS_ENABLED,
    featureFlags = featureFlags,
    geoProvider = geoProvider,
    sdkInitializer = sdkInitializer::ensureInitialized,
    hasSdkKey = { sdkInitializer.hasSdkKey },
    loaderFactory = loaderFactory,
    analytics = NativeAdAnalytics(genericAnalytics, placement, variant),
    scope = viewModelScope,
    mainDispatcher = Dispatchers.Main.immediate,
  )

  val uiState: StateFlow<NativeAdUiState> = controller.uiState

  /** The effective `<prefix>_position`, once the Remote Config gate has been evaluated. */
  val configuredPosition: StateFlow<Int?> = controller.configuredPosition

  /** The effective `<prefix>_min_gap`, once the Remote Config gate has been evaluated. */
  val configuredMinGap: StateFlow<Int?> = controller.configuredMinGap

  /**
   * Idempotent: starts (or, when allowed, re-evaluates) the load. See [NativeAdController].
   * [listSize] is the number of organic rows for list slots, checked against the configured position.
   */
  fun startLoading(
    contentLength: Int = Int.MAX_VALUE,
    listSize: Int? = null,
    locationCheck: ((position: Int, minGap: Int) -> Boolean)? = null,
  ) = controller.requestLoad(contentLength, listSize, locationCheck)

  /** The slot is attached and near the viewport (true) or offscreen / disposed (false). */
  fun setSlotActive(active: Boolean) = controller.setActive(active)

  /** Renders [ad] into [view]; ignored when [ad] is no longer the loaded ad. */
  fun render(view: MaxNativeAdView, ad: MaxAd) {
    controller.render(view, ad)
  }

  override fun onCleared() {
    controller.release()
    super.onCleared()
  }
}

@HiltViewModel
class HomeNativeAdViewModel @Inject constructor(
  featureFlags: FeatureFlags,
  sdkInitializer: AppLovinSdkInitializer,
  geoProvider: AdGeoProvider,
  loaderFactory: NativeAdLoaderFactory,
  genericAnalytics: GenericAnalytics,
) : NativeAdViewModel(
  NativeAdPlacement.HOME_BUNDLE, featureFlags, sdkInitializer, geoProvider, loaderFactory,
  genericAnalytics,
)

@HiltViewModel
class HomeDeepNativeAdViewModel @Inject constructor(
  featureFlags: FeatureFlags,
  sdkInitializer: AppLovinSdkInitializer,
  geoProvider: AdGeoProvider,
  loaderFactory: NativeAdLoaderFactory,
  genericAnalytics: GenericAnalytics,
) : NativeAdViewModel(
  NativeAdPlacement.HOME_DEEP, featureFlags, sdkInitializer, geoProvider, loaderFactory,
  genericAnalytics,
)

@HiltViewModel
class SearchNativeAdViewModel @Inject constructor(
  featureFlags: FeatureFlags,
  sdkInitializer: AppLovinSdkInitializer,
  geoProvider: AdGeoProvider,
  loaderFactory: NativeAdLoaderFactory,
  genericAnalytics: GenericAnalytics,
) : NativeAdViewModel(
  NativeAdPlacement.SEARCH_LANDING, featureFlags, sdkInitializer, geoProvider, loaderFactory,
  genericAnalytics, variant = NativeAdAnalytics.VARIANT_NATIVE_CONTROL,
)

@HiltViewModel
class AppDetailNativeAdViewModel @Inject constructor(
  featureFlags: FeatureFlags,
  sdkInitializer: AppLovinSdkInitializer,
  geoProvider: AdGeoProvider,
  loaderFactory: NativeAdLoaderFactory,
  genericAnalytics: GenericAnalytics,
) : NativeAdViewModel(
  NativeAdPlacement.APP_DETAIL, featureFlags, sdkInitializer, geoProvider, loaderFactory,
  genericAnalytics,
)

@HiltViewModel
class AppDetailBottomNativeAdViewModel @Inject constructor(
  featureFlags: FeatureFlags,
  sdkInitializer: AppLovinSdkInitializer,
  geoProvider: AdGeoProvider,
  loaderFactory: NativeAdLoaderFactory,
  genericAnalytics: GenericAnalytics,
) : NativeAdViewModel(
  NativeAdPlacement.APP_DETAIL_BOTTOM, featureFlags, sdkInitializer, geoProvider, loaderFactory,
  genericAnalytics,
)

@HiltViewModel
class CategoryListNativeAdViewModel @Inject constructor(
  featureFlags: FeatureFlags,
  sdkInitializer: AppLovinSdkInitializer,
  geoProvider: AdGeoProvider,
  loaderFactory: NativeAdLoaderFactory,
  genericAnalytics: GenericAnalytics,
) : NativeAdViewModel(
  NativeAdPlacement.CATEGORY_LIST, featureFlags, sdkInitializer, geoProvider, loaderFactory,
  genericAnalytics,
)

@HiltViewModel
class TopChartsNativeAdViewModel @Inject constructor(
  featureFlags: FeatureFlags,
  sdkInitializer: AppLovinSdkInitializer,
  geoProvider: AdGeoProvider,
  loaderFactory: NativeAdLoaderFactory,
  genericAnalytics: GenericAnalytics,
) : NativeAdViewModel(
  NativeAdPlacement.TOP_CHARTS, featureFlags, sdkInitializer, geoProvider, loaderFactory,
  genericAnalytics,
)

@HiltViewModel
class CollectionListNativeAdViewModel @Inject constructor(
  featureFlags: FeatureFlags,
  sdkInitializer: AppLovinSdkInitializer,
  geoProvider: AdGeoProvider,
  loaderFactory: NativeAdLoaderFactory,
  genericAnalytics: GenericAnalytics,
) : NativeAdViewModel(
  NativeAdPlacement.COLLECTION_LIST, featureFlags, sdkInitializer, geoProvider, loaderFactory,
  genericAnalytics,
)

@Composable
fun rememberHomeNativeAd(): HomeNativeAdViewModel = hiltViewModel()

@Composable
fun rememberHomeDeepNativeAd(): HomeDeepNativeAdViewModel = hiltViewModel()

@Composable
fun rememberAppDetailNativeAd(): AppDetailNativeAdViewModel = hiltViewModel()

@Composable
fun rememberAppDetailBottomNativeAd(): AppDetailBottomNativeAdViewModel = hiltViewModel()

@Composable
fun rememberSearchNativeAd(): SearchNativeAdViewModel = hiltViewModel()

@Composable
fun rememberCategoryListNativeAd(): CategoryListNativeAdViewModel = hiltViewModel()

@Composable
fun rememberTopChartsNativeAd(): TopChartsNativeAdViewModel = hiltViewModel()

@Composable
fun rememberCollectionListNativeAd(): CollectionListNativeAdViewModel = hiltViewModel()
