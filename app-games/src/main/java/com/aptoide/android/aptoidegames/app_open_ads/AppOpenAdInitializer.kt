package com.aptoide.android.aptoidegames.app_open_ads

import android.content.Context
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.apkfy.ApkfySessionPreferences
import com.aptoide.android.aptoidegames.network.repository.AdvertisingIdsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppOpenAdInitializer @Inject constructor(
  @ApplicationContext private val context: Context,
  private val featureFlags: FeatureFlags,
  private val apkfySessionPreferences: ApkfySessionPreferences,
  private val genericAnalytics: GenericAnalytics,
  private val advertisingIdsRepository: AdvertisingIdsRepository,
) {

  private var appOpenAdManager: AppOpenAdManager? = null

  suspend fun initialize() {
    val config = AppOpenConfig.from(featureFlags)
    if (!config.enabled) return

    val geo = AppOpenGeoProvider(context).getGeo()
    if (!config.isGeoEligible(geo)) return

    if (BuildConfig.APPLOVIN_SDK_KEY.isBlank()) {
      Timber.w("AppLovin SDK key is missing. App open ads are disabled.")
      return
    }

    if (!apkfySessionPreferences.wasApkfyResolvedAtStartup) {
      return
    }

    val testDeviceAdIds = if (BuildConfig.DEBUG) {
      listOfNotNull(advertisingIdsRepository.advertisingId)
    } else {
      emptyList()
    }

    withContext(Dispatchers.Main) {
      val appLovinSdk = AppLovinSdk.getInstance(context)

      if (appLovinSdk.isInitialized) {
        createAdManager(config, geo)
        return@withContext
      }

      val initConfig = AppLovinSdkInitializationConfiguration
        .builder(BuildConfig.APPLOVIN_SDK_KEY)
        .setMediationProvider(AppLovinMediationProvider.MAX)
        .setAdUnitIds(listOf(BuildConfig.APP_OPEN_AD_UNIT_ID))
        .setTestDeviceAdvertisingIds(testDeviceAdIds)
        .build()

      runCatching {
        appLovinSdk.initialize(initConfig) {
          createAdManager(config, geo)
        }
      }.onFailure { throwable ->
        Timber.e(throwable, "Failed to initialize AppLovin app open ads.")
        AppOpenAnalytics(genericAnalytics).sendFailed(geo, "sdk_init_exception")
      }
    }
  }

  private fun createAdManager(
    config: AppOpenConfig,
    geo: String,
  ) {
    if (appOpenAdManager != null) return

    appOpenAdManager = AppOpenAdManager(
      appOpenAdUnitId = BuildConfig.APP_OPEN_AD_UNIT_ID,
      config = config,
      geo = geo,
      frequencyCap = AppOpenFrequencyCap(context),
      analytics = AppOpenAnalytics(genericAnalytics),
    )
  }
}
