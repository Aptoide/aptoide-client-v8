package com.aptoide.android.aptoidegames.ads.native_ads

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.aptoide.android.aptoidegames.ads.AppLovinSdkInitializer
import com.aptoide.android.aptoidegames.ads.NATIVE_ADS_ENABLED
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.app_open_ads.AppOpenGeoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

sealed interface NativeAdUiState {
  /** Nothing to show: disabled, ineligible geo, no fill, load error. The screen collapses the slot. */
  data object Hidden : NativeAdUiState

  /** An ad is loaded. [position] only matters for list slots (see [NativeAdConfig]). */
  data class Loaded(val ad: MaxAd, val position: Int) : NativeAdUiState
}

/**
 * Loads one MAX native ad for a [NativeAdPlacement] per screen instance and exposes it as UI state.
 *
 * The ad is loaded without a view and rendered later into whatever [MaxNativeAdView] the
 * composable creates (see [render]); this is the pattern MAX recommends for lists.
 * Any failure resolves to [NativeAdUiState.Hidden] so the screen never shows an empty slot,
 * which is what makes disabling the ad unit on the MAX dashboard safe.
 *
 * Each placement gets its own Hilt subclass so each screen has an independent ViewModel scope
 * and its own ad unit, flags and analytics prefix.
 */
abstract class NativeAdViewModel(
  private val placement: NativeAdPlacement,
  private val context: Context,
  private val featureFlags: FeatureFlags,
  private val sdkInitializer: AppLovinSdkInitializer,
  genericAnalytics: GenericAnalytics,
) : ViewModel() {

  private val analytics = NativeAdAnalytics(genericAnalytics, placement)

  private val _uiState = MutableStateFlow<NativeAdUiState>(NativeAdUiState.Hidden)
  val uiState: StateFlow<NativeAdUiState> = _uiState.asStateFlow()

  private var loader: MaxNativeAdLoader? = null
  private var loadedAd: MaxAd? = null
  private var geo: String = AppOpenGeoProvider.UNKNOWN_GEO

  init {
    viewModelScope.launch { start() }
  }

  private suspend fun start() {
    if (!NATIVE_ADS_ENABLED) return skip("disabled for this distribution")

    val adUnitId = placement.adUnitId
    if (adUnitId.isBlank()) return skip("no ad unit id")

    val config = NativeAdConfig.from(featureFlags, placement)
    if (!config.enabled) return skip("${NativeAdConfig.enabledKey(placement)} is false")

    geo = AppOpenGeoProvider(context).getGeo()
    if (!config.isGeoEligible(geo)) return skip("geo $geo is excluded")

    if (!sdkInitializer.ensureInitialized()) return skip("MAX SDK not initialized")

    withContext(Dispatchers.Main) { load(adUnitId, config.position) }
  }

  private fun skip(reason: String) =
    Timber.d("Native ad %s not requested: %s", placement.name, reason)

  private fun load(adUnitId: String, position: Int) {
    val loader = MaxNativeAdLoader(adUnitId).also { loader = it }
    loader.setPlacement(placement.maxPlacementName)
    loader.setRevenueListener { ad ->
      analytics.sendImpression(geo, ad.networkName, ad.revenue * ECPM_MULTIPLIER)
    }
    loader.setNativeAdListener(object : MaxNativeAdListener() {
      override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {
        loadedAd?.let(loader::destroy)
        loadedAd = ad
        analytics.sendLoaded(geo, ad.networkName)
        _uiState.value = NativeAdUiState.Loaded(ad, position)
      }

      override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
        analytics.sendFailed(geo, error.code.toString())
        Timber.w(
          "%s native ad load failed for %s: %s (%s)",
          placement.name, adUnitId, error.message, error.code,
        )
        _uiState.value = NativeAdUiState.Hidden
      }

      override fun onNativeAdClicked(ad: MaxAd) {
        analytics.sendClicked(geo, ad.networkName)
      }

      override fun onNativeAdExpired(ad: MaxAd) {
        _uiState.value = NativeAdUiState.Hidden
        loader.loadAd()
      }
    })
    loader.loadAd()
  }

  /** Renders the currently loaded ad into [view]. Called from the composable's view factory. */
  fun render(view: MaxNativeAdView) {
    val ad = loadedAd ?: return
    loader?.render(view, ad)
  }

  override fun onCleared() {
    loadedAd?.let { ad -> loader?.destroy(ad) }
    loadedAd = null
    loader?.destroy()
    loader = null
    super.onCleared()
  }

  private companion object {
    const val ECPM_MULTIPLIER = 1_000
  }
}

@HiltViewModel
class HomeNativeAdViewModel @Inject constructor(
  @ApplicationContext context: Context,
  featureFlags: FeatureFlags,
  sdkInitializer: AppLovinSdkInitializer,
  genericAnalytics: GenericAnalytics,
) : NativeAdViewModel(
  NativeAdPlacement.HOME_BUNDLE, context, featureFlags, sdkInitializer, genericAnalytics,
)

@HiltViewModel
class SearchNativeAdViewModel @Inject constructor(
  @ApplicationContext context: Context,
  featureFlags: FeatureFlags,
  sdkInitializer: AppLovinSdkInitializer,
  genericAnalytics: GenericAnalytics,
) : NativeAdViewModel(
  NativeAdPlacement.SEARCH_LANDING, context, featureFlags, sdkInitializer, genericAnalytics,
)

@Composable
fun rememberHomeNativeAd(): HomeNativeAdViewModel = hiltViewModel()

@Composable
fun rememberSearchNativeAd(): SearchNativeAdViewModel = hiltViewModel()
