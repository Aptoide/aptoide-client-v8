package com.aptoide.android.aptoidegames.analytics

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import androidx.annotation.Size
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.hasObb
import cm.aptoide.pt.feature_apps.data.isAab
import cm.aptoide.pt.feature_apps.data.isInCatappult
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_AAB
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_AAB_INSTALL_TIME
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_APPC
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_IN_CATAPPULT
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_OBB
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_VERSION_CODE
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_INSERTED_KEYWORD
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_PACKAGE_NAME
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_SEARCH_TERM
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_SEARCH_TERM_POSITION
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_SEARCH_TERM_SOURCE
import com.aptoide.android.aptoidegames.analytics.dto.SearchMeta
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

  fun setUTMProperties(
    utmSource: String?,
    utmMedium: String?,
    utmCampaign: String?,
    utmTerm: String?,
    utmContent: String?,
  ) = analyticsSender.setUserProperties(
    "utm_source" to utmSource,
    "utm_medium" to utmMedium,
    "utm_campaign" to utmCampaign,
    "utm_term" to utmTerm,
    "utm_content" to utmContent
  )

  fun sendFirstLaunchEvent() = analyticsSender.logEvent(
    name = "aptoide_first_launch",
    params = null
  )

  fun logEvent(
    @Size(min = 1L, max = 40L) name: String,
    params: Map<String, Any>?,
  ) = analyticsSender.logEvent(name, params)

  companion object {
    internal const val P_APP_AAB = "app_aab"
    internal const val P_APP_AAB_INSTALL_TIME = "app_aab_install_time"
    internal const val P_APP_APPC = "app_appc"
    internal const val P_APP_OBB = "app_obb"
    internal const val P_APP_IN_CATAPPULT = "app_in_catappult"
    internal const val P_PACKAGE_NAME = "package_name"
    internal const val P_APP_VERSION_CODE = "app_version_code"
    internal const val P_SEARCH_TERM = "search_term"
    internal const val P_SEARCH_TERM_POSITION = "search_term_position"
    internal const val P_SEARCH_TERM_SOURCE = "search_term_source"
    internal const val P_INSERTED_KEYWORD = "inserted_keyword"
  }
}

fun App?.toBIParameters(
  aabTypes: String?,
  vararg pairs: Pair<String, Any?>,
): Map<String, Any> =
  this?.run {
    mapOfNonNull(
      *pairs,
      P_PACKAGE_NAME to packageName,
      P_APP_AAB to isAab(),
      P_APP_AAB_INSTALL_TIME to aabTypes,
      P_APP_APPC to isAppCoins,
      P_APP_VERSION_CODE to versionCode,
      P_APP_OBB to hasObb(),
      P_APP_IN_CATAPPULT to isInCatappult().asNullableParameter(),
    )
  } ?: emptyMap()

fun SearchMeta?.toBIParameters(
  searchTermPosition: Int?,
  vararg pairs: Pair<String, Any?>,
): Map<String, Any> =
  this?.run {
    mapOfNonNull(
      *pairs,
      P_SEARCH_TERM to searchTerm,
      P_INSERTED_KEYWORD to insertedKeyword,
      P_SEARCH_TERM_SOURCE to searchTermSource,
      P_SEARCH_TERM_POSITION to searchTermPosition,
    )
  } ?: emptyMap()

fun Any?.asNullableParameter() = this?.toString() ?: "no_info"
