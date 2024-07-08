package com.aptoide.android.aptoidegames.analytics

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import androidx.annotation.Size
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.launch.AppLaunchPreferencesManager
import com.indicative.client.android.Indicative
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class BIAnalytics(private val analyticsSender: AnalyticsSender) {

  fun setup(
    context: Context,
    installManager: InstallManager,
    analyticsInfoProvider: AptoideAnalyticsInfoProvider,
    appLaunchPreferencesManager: AppLaunchPreferencesManager,
  ) {
    CoroutineScope(Dispatchers.Main).launch {
      val isFirstLaunch = appLaunchPreferencesManager.isFirstLaunch()

      Indicative.launch(context, BuildConfig.INDICATIVE_KEY)
      Indicative.setUniqueID(analyticsInfoProvider.getAnalyticsId())

      analyticsSender.setUserProperties(
        "android_api_level" to VERSION.SDK_INT,
        "aptoide_version_code" to BuildConfig.VERSION_CODE,
        "android_brand" to Build.MANUFACTURER,
        "android_model" to Build.MODEL,
        "aptoide_package" to BuildConfig.APPLICATION_ID,
        "theme" to "dark",
        "logged_in" to "NA",
        "gms" to context.getGMSValue()
      )
      installManager
        .getApp("com.dti.folderlauncher")
        .packageInfoFlow
        .map { it != null }
        .onEach { analyticsSender.setUserProperties("is_gh_installed" to it) }
        .launchIn(this)
      installManager
        .getApp("cm.aptoide.pt")
        .packageInfoFlow
        .map { it != null }
        .onEach { analyticsSender.setUserProperties("is_vanilla_installed" to it) }
        .launchIn(this)
      installManager
        .getApp("com.appcoins.wallet")
        .packageInfoFlow
        .map { it != null }
        .onEach { analyticsSender.setUserProperties("is_wallet_app_installed" to it) }
        .launchIn(this)

      analyticsSender.setUserProperties("first_session" to isFirstLaunch)
    }
  }

  fun sendFirstLaunchEvent() = analyticsSender.logEvent(
    name = "aptoide_first_launch",
    params = null
  )

  fun logEvent(
    @Size(min = 1L, max = 40L) name: String,
    params: Map<String, Any>?,
  ) = analyticsSender.logEvent(name, params)
}
