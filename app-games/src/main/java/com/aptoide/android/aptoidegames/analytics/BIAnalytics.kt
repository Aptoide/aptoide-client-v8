package com.aptoide.android.aptoidegames.analytics

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import androidx.annotation.Size
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.hasObb
import cm.aptoide.pt.feature_apps.data.isAab
import cm.aptoide.pt.feature_apps.data.isInCatappult
import cm.aptoide.pt.feature_apps.data.walletApp
import cm.aptoide.pt.feature_categories.analytics.AptoideAnalyticsInfoProvider
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_ACTION
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APKFY_APP_INSTALL
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_AAB
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_AAB_INSTALL_TIME
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_APPC
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_IN_CATAPPULT
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_OBB
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_APP_VERSION_CODE
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_CONTEXT
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_INSERTED_KEYWORD
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_PACKAGE_NAME
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_PREVIOUS_CONTEXT
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_SEARCH_TERM
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_SEARCH_TERM_POSITION
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_SEARCH_TERM_SOURCE
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_TAB
import com.aptoide.android.aptoidegames.analytics.BIAnalytics.Companion.P_TAG
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
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
    featureFlags: FeatureFlags,
  ) {
    Indicative.launch(context, BuildConfig.INDICATIVE_KEY)

    CoroutineScope(Dispatchers.Main).launch {
      val isFirstLaunch = appLaunchPreferencesManager.isFirstLaunch()
      val locale = context.resources.configuration.locales[0]

      Indicative.setUniqueID(analyticsInfoProvider.getAnalyticsId())

      analyticsSender.setUserProperties(
        UserProperty("android_api_level", VERSION.SDK_INT),
        UserProperty("aptoide_version_code", BuildConfig.VERSION_CODE),
        UserProperty("android_brand", Build.MANUFACTURER),
        UserProperty("android_model", Build.MODEL),
        UserProperty("aptoide_package", BuildConfig.APPLICATION_ID),
        UserProperty("aptoide_store", BuildConfig.MARKET_NAME),
        UserProperty("android_language", "${locale.language}-${locale.country}"),
        UserProperty("theme", "dark"),
        UserProperty("logged_in", "NA"),
        UserProperty("gms", context.getGMSValue())
      )
      installManager
        .getApp("com.dti.folderlauncher")
        .packageInfoFlow
        .map { it != null }
        .onEach { setUserProperties(UserProperty("is_gh_installed", it)) }
        .launchIn(this)
      installManager
        .getApp("cm.aptoide.pt")
        .packageInfoFlow
        .map { it != null }
        .onEach { setUserProperties(UserProperty("is_vanilla_installed", it)) }
        .launchIn(this)
      installManager
        .getApp(walletApp.packageName)
        .packageInfoFlow
        .map { it != null }
        .onEach { setUserProperties(UserProperty("is_wallet_app_installed", it)) }
        .launchIn(this)

      setUserProperties(UserProperty("first_session", isFirstLaunch))
      setFeatureFlagsProperties(featureFlags)
    }
  }

  fun setUserProperties(vararg props: UserProperty) =
    analyticsSender.setUserProperties(*props)

  private suspend fun setFeatureFlagsProperties(featureFlags: FeatureFlags) {
    val apkfyVariant = featureFlags.getFlagAsString("apkfy_test_variant")
    val testGroup =
      when (apkfyVariant) {
        "a" -> "group_a"
        "b" -> "group_b"
        "c" -> "group_c"
        "d" -> "group_d"
        else -> "NA"
      }

    analyticsSender.setUserProperties(UserProperty("ab_test_apkfy_may_21", testGroup))
  }

  fun setUTMProperties(
    utmSource: String?,
    utmMedium: String?,
    utmCampaign: String?,
    utmTerm: String?,
    utmContent: String?,
    utmOemId: String?,
    utmPackageName: String?,
  ) = analyticsSender.setUserProperties(
    UserProperty("utm_source", utmSource),
    UserProperty("utm_medium", utmMedium),
    UserProperty("utm_campaign", utmCampaign),
    UserProperty("utm_term", utmTerm),
    UserProperty("utm_content", utmContent),
    UserProperty("utm_oem_id", utmOemId),
    UserProperty("utm_package_name", utmPackageName),
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
    internal const val P_ACTION = "action"
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
    internal const val P_APKFY_APP_INSTALL = "apkfy_app_install"
    internal const val P_CONTEXT = "context"
    internal const val P_PREVIOUS_CONTEXT = "previous_context"
    internal const val P_TAG = "tag"
    internal const val P_TAB = "tab"
  }
}

fun AnalyticsUIContext?.toBiParameters(vararg pairs: Pair<String, Any?>): Map<String, Any> =
  this?.run {
    mapOfNonNull(
      *pairs,
      P_ACTION to installAction?.name?.lowercase(),
      P_APKFY_APP_INSTALL to isApkfy,
      P_CONTEXT to currentScreen,
      P_PREVIOUS_CONTEXT to previousScreen,
      P_TAG to bundleMeta?.tag,
      P_TAB to homeTab
    )
  } ?: mapOfNonNull(*pairs)

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
  } ?: mapOfNonNull(*pairs)

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
  } ?: mapOfNonNull(*pairs)

fun Any?.asNullableParameter() = this?.toString() ?: "no_info"
