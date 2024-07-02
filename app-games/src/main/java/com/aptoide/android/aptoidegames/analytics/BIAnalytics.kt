package com.aptoide.android.aptoidegames.analytics

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
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
import kotlinx.coroutines.withContext

class BIAnalytics {

  fun setup(
    context: Context,
    installManager: InstallManager,
    analyticsInfoProvider: AptoideAnalyticsInfoProvider,
    appLaunchPreferencesManager: AppLaunchPreferencesManager,
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      withContext(Dispatchers.Main) {
        Indicative.launch(context, BuildConfig.INDICATIVE_KEY)
        Indicative.setUniqueID(analyticsInfoProvider.getAnalyticsId())
      }
      Indicative.addProperties(
        mapOf(
          "android_api_level" to VERSION.SDK_INT,
          "aptoide_version_code" to BuildConfig.VERSION_CODE,
          "android_brand" to Build.MANUFACTURER,
          "android_model" to Build.MODEL,
          "aptoide_package" to BuildConfig.APPLICATION_ID,
          "theme" to "dark",
          "logged_in" to "NA",
          "gms" to context.getGMSValue()
        )
      )
      installManager
        .getApp("com.dti.folderlauncher")
        .packageInfoFlow
        .map { it != null }
        .onEach { Indicative.addProperty("is_gh_installed", it) }
        .launchIn(this)
      installManager
        .getApp("cm.aptoide.pt")
        .packageInfoFlow
        .map { it != null }
        .onEach { Indicative.addProperty("is_vanilla_installed", it) }
        .launchIn(this)
      installManager
        .getApp("com.appcoins.wallet")
        .packageInfoFlow
        .map { it != null }
        .onEach { Indicative.addProperty("is_wallet_app_installed", it) }
        .launchIn(this)
      val isFirstLaunch = appLaunchPreferencesManager.isFirstLaunch()
      if (isFirstLaunch) {
        appLaunchPreferencesManager.setIsNotFirstLaunch()
      }
      Indicative.addProperty("first_session", isFirstLaunch)
    }
  }
}
