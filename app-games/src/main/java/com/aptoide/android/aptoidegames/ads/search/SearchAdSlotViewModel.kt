package com.aptoide.android.aptoidegames.ads.search

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.LocalIdsRepository
import com.aptoide.android.aptoidegames.ads.AdGeoProvider
import com.aptoide.android.aptoidegames.ads.AppLovinSdkInitializer
import com.aptoide.android.aptoidegames.ads.NATIVE_ADS_ENABLED
import com.aptoide.android.aptoidegames.ads.mrec.MrecAdLoaderFactory
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdAnalytics
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdController
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdPlacement
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.attribution.domain.AttributionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Decides once per Search landing instance which ad format the opportunity gets, before any ad
 * object is created, so only one format is ever requested. The Firebase installation id is the
 * bucketing key for fractional experiments; the guest uid is its fallback. The default 100%
 * MREC allocation needs no installation-id lookup. No id means native only for a partial split.
 */
@HiltViewModel
class SearchAdSlotViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val featureFlags: FeatureFlags,
  private val analyticsInfoProvider: AptoideAnalyticsInfoProvider,
  private val idsRepository: LocalIdsRepository,
  private val sdkInitializer: AppLovinSdkInitializer,
  private val geoProvider: AdGeoProvider,
  private val genericAnalytics: GenericAnalytics,
) : ViewModel() {

  private val _decision = MutableStateFlow<SearchAdDecision?>(null)
  val decision: StateFlow<SearchAdDecision?> = _decision.asStateFlow()

  /**
   * The MREC arm's loader and controller, created at most once per landing instance and only
   * when the decision is MREC. Owning them here (not in the lazy item) gives the MREC arm the
   * same lifetime as the native arm's ViewModel: typing, rotating or opening an app and coming
   * back re-shows the loaded MREC instead of destroying it and requesting again, so both arms
   * measure the same opportunity. Only the application context is retained.
   */
  private var mrec: MrecArm? = null

  init {
    viewModelScope.launch { _decision.value = decide() }
  }

  /** The MREC arm for [decision]; null unless the decision requests an MREC. */
  fun mrecArm(decision: SearchAdDecision): MrecArm? {
    if (!decision.requestsMrec) return null
    return mrec ?: MrecArm(decision).also { mrec = it }
  }

  override fun onCleared() {
    mrec?.controller?.release()
    mrec = null
  }

  inner class MrecArm(decision: SearchAdDecision) {
    val loaderFactory = MrecAdLoaderFactory(context, decision.mrecAutoRefresh)
    val controller = NativeAdController(
      placement = NativeAdPlacement.SEARCH_MREC,
      adUnitId = decision.mrecAdUnitId,
      distributionEnabled = NATIVE_ADS_ENABLED,
      featureFlags = featureFlags,
      geoProvider = geoProvider,
      sdkInitializer = sdkInitializer::ensureInitialized,
      hasSdkKey = { sdkInitializer.hasSdkKey },
      loaderFactory = loaderFactory,
      analytics = NativeAdAnalytics(
        genericAnalytics = genericAnalytics,
        prefix = NativeAdPlacement.SEARCH_MREC.flagPrefix,
        format = SearchAdFormat.MREC.analyticsFormat,
        variant = SearchAdFormat.MREC.variant,
      ),
      scope = viewModelScope,
      mainDispatcher = Dispatchers.Main.immediate,
    )
  }

  private suspend fun decide(): SearchAdDecision {
    // Same distribution gate as every ad: nothing (not even an id lookup) on builds without ads.
    if (!NATIVE_ADS_ENABLED) {
      return SearchAdDecision(SearchAdFormat.NATIVE, mrecAdUnitId = "", mrecAutoRefresh = false)
    }
    val config = runCatching {
      SearchAdExperimentConfig(
        enabled = featureFlags.getFlag(
          SearchAdExperimentConfig.ENABLED_KEY, SearchAdExperimentConfig.DEFAULT.enabled,
        ),
        mrecAllocationPercent = featureFlags.getInt(
          SearchAdExperimentConfig.ALLOCATION_KEY,
          SearchAdExperimentConfig.DEFAULT.mrecAllocationPercent,
        ),
        mrecAutoRefresh = featureFlags.getFlag(SearchAdExperimentConfig.AUTO_REFRESH_KEY, false),
      )
    }.getOrDefault(SearchAdExperimentConfig.DEFAULT)
    val mrecAdUnitId = NativeAdPlacement.SEARCH_MREC.adUnitId
    val allocation = config.mrecAllocationPercent.coerceIn(0, SearchAdFormatDecider.BUCKETS)
    val needsBucket = config.enabled && mrecAdUnitId.isNotBlank() &&
      allocation in 1 until SearchAdFormatDecider.BUCKETS
    val stableId = if (needsBucket) stableInstallationId() else null
    return SearchAdDecision(
      format = SearchAdFormatDecider.decide(config, mrecAdUnitId, stableId),
      mrecAdUnitId = mrecAdUnitId,
      mrecAutoRefresh = config.mrecAutoRefresh,
    )
  }

  private suspend fun stableInstallationId(): String? =
    runCatching { analyticsInfoProvider.getAnalyticsId() }.getOrNull()?.takeIf { it.isNotBlank() }
      ?: runCatching { idsRepository.getId(AttributionManager.GUEST_UID_KEY) }.getOrNull()
        ?.takeIf { it.isNotBlank() }
}

@Composable
fun rememberSearchAdSlot(): SearchAdSlotViewModel = hiltViewModel()
