package com.aptoide.android.aptoidegames.analytics

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
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

class BIAnalytics(
  private val installManager: InstallManager,
  private val appLaunchPreferencesManager: AppLaunchPreferencesManager,
  private val context: Context
) {

  fun init() {
    Indicative.addProperties(getBaseIndicativeProperties())
    updateGamesHubInstalled()
    updateAptoideVanillaInstalled()
    updateWalletInstalled()
    handleFirstLaunch()
    handleGMS()
    handleLoggedIn()
    handleTheme()
  }

  private fun getBaseIndicativeProperties(): Map<String, Any> {
    return mapOf(
      "android_api_level" to VERSION.SDK_INT,
      "aptoide_version_code" to BuildConfig.VERSION_CODE,
      "android_brand" to Build.MANUFACTURER,
      "android_model" to Build.MODEL,
      "aptoide_package" to BuildConfig.APPLICATION_ID
    )
  }

  private fun updateGamesHubInstalled() {
    installManager
      .getApp("com.dti.folderlauncher")
      .packageInfoFlow
      .map { it != null }
      .onEach { Indicative.addProperty("is_gh_installed", it) }
      .launchIn(CoroutineScope(Dispatchers.IO))
  }

  private fun updateAptoideVanillaInstalled() {
    installManager
      .getApp("cm.aptoide.pt")
      .packageInfoFlow
      .map { it != null }
      .onEach { Indicative.addProperty("is_vanilla_installed", it) }
      .launchIn(CoroutineScope(Dispatchers.IO))
  }

  private fun updateWalletInstalled() {
    installManager
      .getApp("com.appcoins.wallet")
      .packageInfoFlow
      .map { it != null }
      .onEach {
        Indicative.addProperty("is_wallet_app_installed", it)
      }
      .launchIn(CoroutineScope(Dispatchers.IO))
  }

  private fun handleFirstLaunch() {
    CoroutineScope(Dispatchers.Main).launch {
      val isFirstLaunch = appLaunchPreferencesManager.isFirstLaunch()
      if (isFirstLaunch) {
        appLaunchPreferencesManager.setIsNotFirstLaunch()
      }
      Indicative.addProperty("first_session", isFirstLaunch)
    }
  }

  private fun handleTheme() {
    Indicative.addProperty("theme", "dark")
  }

  private fun handleLoggedIn() {
    Indicative.addProperty("logged_in", "NA")
  }

  private fun handleGMS() {
    Indicative.addProperty("gms", context.getGMSValue())
  }
}
