package com.aptoide.android.aptoidegames.ads.home_native

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
import com.aptoide.android.aptoidegames.BuildConfig
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

sealed interface HomeNativeAdUiState {
  /** Nothing to show: disabled, ineligible geo, no fill, load error. The feed collapses the slot. */
  data object Hidden : HomeNativeAdUiState

  /** An ad is loaded and should be rendered before the bundle at [position]. */
  data class Loaded(val ad: MaxAd, val position: Int) : HomeNativeAdUiState
}

/**
 * Loads one MAX native ad for the Games home feed per screen instance and exposes it as UI state.
 *
 * The ad is loaded without a view and rendered later into whatever [MaxNativeAdView] the
 * composable creates (see [render]); this is the pattern MAX recommends for lists.
 * Any failure resolves to [HomeNativeAdUiState.Hidden] so the feed never shows an empty slot,
 * which is what makes disabling the ad unit on the MAX dashboard safe.
 */
@HiltViewModel
class HomeNativeAdViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val featureFlags: FeatureFlags,
  private val sdkInitializer: AppLovinSdkInitializer,
  genericAnalytics: GenericAnalytics,
) : ViewModel() {

  private val analytics = HomeNativeAdAnalytics(genericAnalytics)

  private val _uiState = MutableStateFlow<HomeNativeAdUiState>(HomeNativeAdUiState.Hidden)
  val uiState: StateFlow<HomeNativeAdUiState> = _uiState.asStateFlow()

  private var loader: MaxNativeAdLoader? = null
  private var loadedAd: MaxAd? = null
  private var geo: String = AppOpenGeoProvider.UNKNOWN_GEO

  init {
    viewModelScope.launch { start() }
  }

  private suspend fun start() {
    if (!NATIVE_ADS_ENABLED) return

    val adUnitId = BuildConfig.HOME_NATIVE_AD_UNIT_ID
    if (adUnitId.isBlank()) return

    val config = HomeNativeAdConfig.from(featureFlags)
    if (!config.enabled) return

    geo = AppOpenGeoProvider(context).getGeo()
    if (!config.isGeoEligible(geo)) return

    if (!sdkInitializer.ensureInitialized()) return

    withContext(Dispatchers.Main) { load(adUnitId, config.position) }
  }

  private fun load(adUnitId: String, position: Int) {
    val loader = MaxNativeAdLoader(adUnitId).also { loader = it }
    loader.setPlacement(PLACEMENT)
    loader.setRevenueListener { ad ->
      analytics.sendImpression(geo, ad.networkName, ad.revenue * ECPM_MULTIPLIER)
    }
    loader.setNativeAdListener(object : MaxNativeAdListener() {
      override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {
        loadedAd?.let(loader::destroy)
        loadedAd = ad
        analytics.sendLoaded(geo, ad.networkName)
        _uiState.value = HomeNativeAdUiState.Loaded(ad, position)
      }

      override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
        analytics.sendFailed(geo, error.code.toString())
        Timber.w("Home native ad load failed for %s: %s (%s)", adUnitId, error.message, error.code)
        _uiState.value = HomeNativeAdUiState.Hidden
      }

      override fun onNativeAdClicked(ad: MaxAd) {
        analytics.sendClicked(geo, ad.networkName)
      }

      override fun onNativeAdExpired(ad: MaxAd) {
        _uiState.value = HomeNativeAdUiState.Hidden
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
    const val PLACEMENT = "home_bundle"
    const val ECPM_MULTIPLIER = 1_000
  }
}

@Composable
fun rememberHomeNativeAd(): HomeNativeAdViewModel = hiltViewModel()
