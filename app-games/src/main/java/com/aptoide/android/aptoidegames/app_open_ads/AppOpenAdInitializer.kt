package com.aptoide.android.aptoidegames.app_open_ads

import android.content.Context
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.ads.AppLovinSdkInitializer
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.apkfy.ApkfySessionPreferences
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
  private val sdkInitializer: AppLovinSdkInitializer,
) {

  private var appOpenAdManager: AppOpenAdManager? = null

  suspend fun initialize() {
    val config = AppOpenConfig.from(featureFlags)
    if (!config.enabled) return

    val geo = AppOpenGeoProvider(context).getGeo()
    if (!config.isGeoEligible(geo)) return

    if (!sdkInitializer.hasSdkKey) {
      Timber.w("AppLovin SDK key is missing. App open ads are disabled.")
      return
    }

    if (!apkfySessionPreferences.wasApkfyResolvedAtStartup) {
      return
    }

    if (!sdkInitializer.ensureInitialized()) {
      AppOpenAnalytics(genericAnalytics).sendFailed(geo, "sdk_init_exception")
      return
    }

    withContext(Dispatchers.Main) { createAdManager(config, geo) }
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
