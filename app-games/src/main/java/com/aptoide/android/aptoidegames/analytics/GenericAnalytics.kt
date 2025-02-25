package com.aptoide.android.aptoidegames.analytics

import android.content.Context
import android.content.res.Configuration
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.walletApp
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_APPC_BILLING
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_APP_SIZE
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_CONTEXT
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_ITEM_POSITION
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics.Companion.P_PACKAGE_NAME
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.dto.SearchMeta
import com.aptoide.android.aptoidegames.home.repository.ThemePreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class GenericAnalytics(private val analyticsSender: AnalyticsSender) {

  fun setUserProperties(
    context: Context,
    themePreferencesManager: ThemePreferencesManager,
    installManager: InstallManager,
  ) {
    themePreferencesManager
      .isDarkTheme()
      .map { if (it ?: context.isNightMode) "system_dark" else "system_light" }
      .onEach { analyticsSender.setUserProperties("theme" to it) }
      .launchIn(CoroutineScope(Dispatchers.IO))
    installManager
      .getApp(walletApp.packageName)
      .packageInfoFlow
      .map { (it != null).toString() }
      .onEach { analyticsSender.setUserProperties("wallet_installed" to it) }
      .launchIn(CoroutineScope(Dispatchers.IO))
  }

  private val Context.isNightMode
    get() =
      resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

  fun logEvent(
    name: String,
    params: Map<String, Any>?,
  ) = analyticsSender.logEvent(name, params)

  /**
   * Actual events sending in the same order as in the Google document
   */

  fun sendNoNetworkRetry() = analyticsSender.logEvent(
    name = "retry_no_connection_clicked",
    params = emptyMap()
  )

  fun sendOpenAppEvent(
    appOpenSource: String,
    isFirstLaunch: Boolean,
    networkType: String,
  ) = analyticsSender.logEvent(
    name = "app_open",
    params = mapOf(
      P_OPEN_TYPE to appOpenSource,
      P_FIRST_LAUNCH to if (isFirstLaunch) "true" else "false",
      P_SERVICE to networkType
    )
  )

  fun sendEngagedUserEvent() = analyticsSender.logEvent(
    name = "engaged_user",
    params = emptyMap()
  )

  fun sendAppPromoClick(
    app: App,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "app_promo_clicked",
    params = analyticsContext.toGenericParameters(
      *app.toGenericParameters()
    )
  )

  fun sendSeeAllClick(analyticsContext: AnalyticsUIContext) = analyticsSender.logEvent(
    name = "see_all_clicked",
    params = analyticsContext.bundleMeta.toGenericParameters()
  )

  fun sendBackButtonClick(analyticsContext: AnalyticsUIContext) = analyticsSender.logEvent(
    name = "back_button_clicked",
    params = analyticsContext.toGenericParameters()
  )

  fun sendCarouselSwipe(
    count: Int,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "carousel_swipe",
    params = analyticsContext.bundleMeta.toGenericParameters(P_SCROLL_COUNT to count)
  )

  fun sendMenuClick(link: String) = analyticsSender.logEvent(
    name = "menu_clicked",
    params = mapOf("link" to link)
  )

  fun sendCategoryClick(
    categoryName: String,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "category_clicked",
    params = mapOfNonNull(
      P_CATEGORY to categoryName,
      P_ITEM_POSITION to analyticsContext.itemPosition
    )
  )

  fun sendBottomBarHomeClick() = analyticsSender.logEvent(
    name = "bn_home_clicked",
    params = emptyMap()
  )

  fun sendBottomBarSearchClick() = analyticsSender.logEvent(
    name = "bn_search_clicked",
    params = emptyMap()
  )

  fun sendBottomBarGameGenieClick() = analyticsSender.logEvent(
    name = "bn_gamegenie_clicked",
    params = emptyMap()
  )

  fun sendBottomBarCategoriesClick() = analyticsSender.logEvent(
    name = "bn_categories_clicked",
    params = emptyMap()
  )

  fun sendBottomBarUpdatesClick() = analyticsSender.logEvent(
    name = "bn_updates_clicked",
    params = emptyMap()
  )

  fun sendNotificationOptIn() = analyticsSender.logEvent(
    name = "notification_opt_in",
    params = emptyMap()
  )

  fun sendNotificationOptOut() = analyticsSender.logEvent(
    name = "notification_opt_out",
    params = emptyMap()
  )

  fun sendGetNotifiedContinueClick() = analyticsSender.logEvent(
    name = "get_notified_continue_clicked",
    params = emptyMap()
  )

  fun sendSearchMadeEvent(searchMeta: SearchMeta) = analyticsSender.logEvent(
    name = "search_made",
    params = searchMeta.toGenericParameters()
  )

  fun sendDownloadOverWifiDisabled() = analyticsSender.logEvent(
    name = "download_over_wifi_disabled",
    params = emptyMap()
  )

  fun sendDownloadOverWifiEnabled() = analyticsSender.logEvent(
    name = "download_over_wifi_enabled",
    params = emptyMap()
  )

  fun sendSendFeedbackClicked() = analyticsSender.logEvent(
    name = "send_feedback_clicked",
    params = emptyMap()
  )

  fun sendFeedbackSent(feedbackType: String) = analyticsSender.logEvent(
    name = "feedback_sent",
    params = mapOf("feedback_type" to feedbackType)
  )

  fun sendFeatureFlagsFetch(duration: Long) = analyticsSender.logEvent(
    name = "feature_flags_fetch",
    params = mapOf("duration" to duration)
  )

  fun sendApkfyShown() = analyticsSender.logEvent("apkfy_shown", params = emptyMap())

  fun sendApkfyTimeout() = analyticsSender.logEvent("apkfy_timeout", params = emptyMap())

  fun sendECVideosFlagReady() =
    analyticsSender.logEvent("ec_videos_flag_ready", params = emptyMap())

  fun sendHomeTabClick(tab: String) =
    analyticsSender.logEvent("home_tab_clicked", params = mapOf(P_NAME to tab))

  companion object {
    internal const val P_OPEN_TYPE = "open_type"
    internal const val P_FIRST_LAUNCH = "first_launch"
    internal const val P_PACKAGE_NAME = "package_name"
    internal const val P_APP_SIZE = "app_size"
    internal const val P_CONTEXT = "context"
    internal const val P_ITEM_POSITION = "item_position"
    internal const val P_SCROLL_COUNT = "scroll_count"
    internal const val P_CATEGORY = "category"
    internal const val P_APPC_BILLING = "appc_billing"
    internal const val P_SERVICE = "service"
    internal const val P_NAME = "name"
  }
}

/**
 * Helper functions for better readability
 */

fun AnalyticsUIContext?.toGenericParameters(vararg pairs: Pair<String, Any?>): Map<String, Any> =
  this?.run {
    searchMeta.toGenericParameters() +
      bundleMeta.toGenericParameters(
        *pairs,
        P_CONTEXT to currentScreen,
        P_ITEM_POSITION to itemPosition
      )
  } ?: mapOfNonNull(*pairs)

fun BundleMeta?.toGenericParameters(vararg pairs: Pair<String, Any?>) =
  this?.run {
    mapOfNonNull(
      *pairs,
      "section_id" to tag,
      "section_type" to bundleSource
    )
  } ?: mapOfNonNull(*pairs)

fun SearchMeta?.toGenericParameters() = this?.run {
  mapOf(
    "inserted_keyword" to insertedKeyword,
    "search_keyword" to searchKeyword,
    "search_type" to searchType
  )
} ?: emptyMap()

fun App.toGenericParameters(): Array<Pair<String, Any>> = arrayOf(
  P_PACKAGE_NAME to packageName,
  P_APPC_BILLING to isAppCoins,
  P_APP_SIZE to appSize
)

fun <K, V : Any> mapOfNonNull(vararg pairs: Pair<K, V?>) = mapOf(*pairs)
  .filterValues { it != null }
  .mapValues { it.value as V }
