package com.aptoide.android.aptoidegames.analytics

import android.content.Context
import android.content.res.Configuration
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.InstallManager
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.ProductInfoData
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsPayload
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
      .getApp("com.appcoins.wallet")
      .packageInfoFlow
      .map { (it != null).toString() }
      .onEach { analyticsSender.setUserProperties("wallet_installed" to it) }
      .launchIn(CoroutineScope(Dispatchers.IO))
  }

  private val Context.isNightMode
    get() =
      resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

  /**
   * Actual events sending in the same order as in the Google document
   */

  fun sendAppCoinsInstallStarted(
    packageName: String,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "appcoins_install_initiated",
    params = analyticsContext.toParameters(P_PACKAGE_NAME to packageName)
  )

  fun sendInstallClick(
    app: App,
    networkType: String,
    analyticsContext: AnalyticsUIContext,
  ) {
    analyticsSender.logEvent(
      name = "install_clicked",
      params = analyticsContext.toParameters(
        P_PACKAGE_NAME to app.packageName,
        P_APPC_BILLING to app.isAppCoins,
        P_SERVICE to networkType
      )
    )
  }

  fun sendOpenClick(
    packageName: String,
    hasAPPCBilling: Boolean? = null,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "open_clicked",
    params = analyticsContext.toParameters(
      P_PACKAGE_NAME to packageName,
      P_APPC_BILLING to hasAPPCBilling
    )
  )

  fun sendRetryClick(
    app: App,
    networkType: String,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "retry_app_clicked",
    params = analyticsContext.toParameters(
      P_PACKAGE_NAME to app.packageName,
      P_APPC_BILLING to app.isAppCoins,
      P_SERVICE to networkType
    )
  )

  fun sendNoNetworkRetry() = analyticsSender.logEvent(
    name = "retry_no_connection_clicked",
    params = emptyMap()
  )

  fun sendUpdateClick(
    app: App,
    networkType: String,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "update_clicked",
    params = analyticsContext.toParameters(
      P_PACKAGE_NAME to app.packageName,
      P_APPC_BILLING to app.isAppCoins,
      P_SERVICE to networkType
    )
  )

  fun sendResumeDownloadClick(
    packageName: String,
    downloadOnlyOverWifiSetting: Boolean,
    appSize: Long,
  ) {
    analyticsSender.logEvent(
      name = "resume_download_clicked",
      params = mapOf(
        P_PACKAGE_NAME to packageName,
        P_WIFI_SETTING to downloadOnlyOverWifiSetting,
        P_APP_SIZE to appSize
      )
    )
  }

  fun sendDownloadStartedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_download",
    params = analyticsPayload.toParameters(
      P_PACKAGE_NAME to packageName,
      P_STATUS to "started"
    )
  )

  fun sendDownloadRestartedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_download",
    params = analyticsPayload.toParameters(
      P_PACKAGE_NAME to packageName,
      P_STATUS to "restart"
    )
  )

  fun sendDownloadCompletedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_download",
    params = analyticsPayload.toParameters(
      P_PACKAGE_NAME to packageName,
      P_STATUS to "success"
    )
  )

  fun sendInstallStartedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_installed",
    params = analyticsPayload.toParameters(
      P_PACKAGE_NAME to packageName,
      P_STATUS to "started"
    )
  )

  fun sendInstallCompletedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_installed",
    params = analyticsPayload.toParameters(
      P_PACKAGE_NAME to packageName,
      P_STATUS to "success"
    )
  )

  fun sendDownloadCancelEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_download",
    params = analyticsPayload.toParameters(
      P_PACKAGE_NAME to packageName,
      P_STATUS to "cancel"
    )
  )

  fun sendInstallCancelEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_installed",
    params = analyticsPayload.toParameters(
      P_PACKAGE_NAME to packageName,
      P_STATUS to "cancel"
    )
  )

  fun sendDownloadErrorEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    errorMessage: String?,
  ) = analyticsSender.logEvent(
    name = "app_download",
    params = analyticsPayload.toParameters(
      P_PACKAGE_NAME to packageName,
      P_STATUS to "fail",
      P_ERROR_MESSAGE to (errorMessage ?: "failure")
    )
  )

  fun sendInstallErrorEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    errorMessage: String?,
  ) = analyticsSender.logEvent(
    name = "app_installed",
    params = analyticsPayload.toParameters(
      P_PACKAGE_NAME to packageName,
      P_STATUS to "fail",
      P_ERROR_MESSAGE to (errorMessage ?: "failure")
    )
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
    params = analyticsContext.toParameters(
      P_PACKAGE_NAME to app.packageName,
      P_APPC_BILLING to app.isAppCoins
    )
  )

  fun sendSeeAllClick(analyticsContext: AnalyticsUIContext) = analyticsSender.logEvent(
    name = "see_all_clicked",
    params = analyticsContext.bundleMeta.toParameters()
  )

  fun sendBackButtonClick(analyticsContext: AnalyticsUIContext) = analyticsSender.logEvent(
    name = "back_button_clicked",
    params = analyticsContext.toParameters()
  )

  fun sendCarouselSwipe(
    count: Int,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "carousel_swipe",
    params = analyticsContext.bundleMeta.toParameters(P_SCROLL_COUNT to count)
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

  fun sendBottomBarCategoriesClick() = analyticsSender.logEvent(
    name = "bn_categories_clicked",
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

  fun sendDownloadCancel(
    packageName: String,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "download_canceled",
    params = analyticsContext.toParameters(P_PACKAGE_NAME to packageName)
  )

  fun sendSearchMadeEvent(searchMeta: SearchMeta) = analyticsSender.logEvent(
    name = "search_made",
    params = searchMeta.toParameters()
  )

  fun sendNotEnoughSpaceDialogShow(
    packageName: String,
    appSize: Long,
  ) = analyticsSender.logEvent(
    name = "oos_not_enough_space",
    params = mapOf(
      P_PACKAGE_NAME to packageName,
      P_APP_SIZE to appSize
    )
  )

  fun sendUninstallClick(
    packageName: String,
    appSize: Long,
  ) = analyticsSender.logEvent(
    name = "oos_uninstall_clicked",
    params = mapOf(
      P_PACKAGE_NAME to packageName,
      P_APP_SIZE to appSize
    )
  )

  fun sendOOsGoBackButtonClick() = analyticsSender.logEvent(
    name = "oos_go_back_clicked",
    params = emptyMap()
  )

  fun sendDownloadOverWifiDisabled() = analyticsSender.logEvent(
    name = "download_over_wifi_disabled",
    params = emptyMap()
  )

  fun sendDownloadOverWifiEnabled() = analyticsSender.logEvent(
    name = "download_over_wifi_enabled",
    params = emptyMap()
  )

  fun sendWifiPromptShown(
    app: App,
    downloadOnlyOverWifiSetting: Boolean,
  ) = analyticsSender.logEvent(
    name = "wifi_prompt_shown",
    params = mapOf(
      P_WIFI_SETTING to downloadOnlyOverWifiSetting,
      P_PACKAGE_NAME to app.packageName,
      P_APP_SIZE to app.appSize
    )
  )

  fun sendWaitForWifiClicked(
    app: App,
    downloadOnlyOverWifi: Boolean,
  ) = analyticsSender.logEvent(
    name = "wait_for_wifi_clicked",
    params = mapOf(
      P_WIFI_SETTING to downloadOnlyOverWifi,
      P_PACKAGE_NAME to app.packageName,
      P_APP_SIZE to app.appSize
    )
  )

  fun sendDownloadNowClicked(
    downloadOnlyOverWifi: Boolean,
    promptType: String,
    packageName: String,
    appSize: Long,
  ) = analyticsSender.logEvent(
    name = "download_now_clicked",
    params = mapOf(
      P_WIFI_SETTING to downloadOnlyOverWifi,
      P_PROMPT_TYPE to promptType,
      P_PACKAGE_NAME to packageName,
      P_APP_SIZE to appSize
    )
  )

  fun sendPaymentStartEvent(
    packageName: String,
    productInfoData: ProductInfoData?,
  ) = analyticsSender.logEvent(
    name = "iap_payment_start",
    params = productInfoData.toParameters(P_PACKAGE_NAME to packageName)
  )

  fun sendPaymentMethodsDismissedEvent(
    packageName: String,
    productInfoData: ProductInfoData?,
  ) = analyticsSender.logEvent(
    name = "iap_payment_dismissed",
    params = productInfoData.toParameters(
      P_PACKAGE_NAME to packageName,
      P_PAYMENT_METHOD to "list",
      P_CONTEXT to "start"
    )
  )

  fun sendPaymentDismissedEvent(
    paymentMethod: PaymentMethod<*>,
    context: String?,
  ) = analyticsSender.logEvent(
    name = "iap_payment_dismissed",
    params = paymentMethod.toParameters(P_CONTEXT to context)
  )

  fun sendPaymentBackEvent(paymentMethod: PaymentMethod<*>) = analyticsSender.logEvent(
    name = "iap_payment_back",
    params = paymentMethod.toParameters()
  )

  fun sendPaymentBuyEvent(paymentMethod: PaymentMethod<*>) = analyticsSender.logEvent(
    name = "iap_payment_buy",
    params = paymentMethod.toParameters()
  )

  fun sendPaymentTryAgainEvent(paymentMethod: PaymentMethod<*>) = analyticsSender.logEvent(
    name = "iap_payment_try_again",
    params = paymentMethod.toParameters()
  )

  fun sendPaymentSuccessEvent(paymentMethod: PaymentMethod<*>) = analyticsSender.logEvent(
    name = "iap_payment_conclusion",
    params = paymentMethod.toParameters(P_STATUS to "success")
  )

  fun sendPaymentErrorEvent(
    paymentMethod: PaymentMethod<*>,
    errorCode: String? = null,
  ) = analyticsSender.logEvent(
    name = "iap_payment_conclusion",
    params = paymentMethod.toParameters(
      P_STATUS to "error",
      P_ERROR_CODE to errorCode
    )
  )

  fun sendPaymentMethodsEvent(paymentMethod: PaymentMethod<*>) = analyticsSender.logEvent(
    name = "iap_payment_methods",
    params = paymentMethod.productInfo.toParameters(
      P_PACKAGE_NAME to paymentMethod.purchaseRequest.domain,
      P_PAYMENT_METHOD to paymentMethod.id
    )
  )

  fun sendPaymentSupportClicked() = analyticsSender.logEvent(
    name = "payment_support_clicked",
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

  /**
   * Helper functions for better readability
   */

  private fun AnalyticsUIContext?.toParameters(vararg pairs: Pair<String, Any?>): Map<String, Any> =
    this?.run {
      searchMeta.toParameters() +
        bundleMeta.toParameters(
          *pairs,
          P_CONTEXT to currentScreen,
          P_ITEM_POSITION to itemPosition
        )
    } ?: emptyMap()

  private fun AnalyticsPayload?.toParameters(vararg pairs: Pair<String, Any?>): Map<String, Any> =
    this?.run {
      bundleMeta.toParameters(
        *pairs,
        P_CONTEXT to context,
        P_APPC_BILLING to isAppCoins,
        P_ITEM_POSITION to itemPosition
      )
    } ?: emptyMap()

  private fun BundleMeta?.toParameters(vararg pairs: Pair<String, Any?>) = this?.run {
    mapOfNonNull(
      *pairs,
      "section_id" to tag,
      "section_type" to bundleSource
    )
  } ?: emptyMap()

  private fun SearchMeta?.toParameters() = this?.run {
    mapOf(
      "inserted_keyword" to insertedKeyword,
      "search_keyword" to searchKeyword,
      "search_type" to searchType
    )
  } ?: emptyMap()

  private fun PaymentMethod<*>.toParameters(vararg pairs: Pair<String, Any?>) =
    productInfo.toParameters(
      *pairs,
      P_PACKAGE_NAME to purchaseRequest.domain,
      P_PAYMENT_METHOD to id
    )

  private fun ProductInfoData?.toParameters(vararg pairs: Pair<String, Any?>) = this?.run {
    mapOfNonNull(
      *pairs,
      "sku_id" to sku,
      "sku_name" to title,
      "price" to priceValue,
      "currency" to priceCurrency
    )
  } ?: emptyMap()

  companion object {
    private const val P_OPEN_TYPE = "open_type"
    private const val P_FIRST_LAUNCH = "first_launch"
    private const val P_PACKAGE_NAME = "package_name"
    private const val P_CONTEXT = "context"
    private const val P_ITEM_POSITION = "item_position"
    private const val P_SCROLL_COUNT = "scroll_count"
    private const val P_CATEGORY = "category"
    private const val P_APPC_BILLING = "appc_billing"
    private const val P_APP_SIZE = "app_size"
    private const val P_WIFI_SETTING = "wifi_setting"
    private const val P_PROMPT_TYPE = "prompt_type"
    private const val P_SERVICE = "service"
    private const val P_STATUS = "status"
    private const val P_ERROR_MESSAGE = "error_message"
    private const val P_PAYMENT_METHOD = "payment_method"
    private const val P_ERROR_CODE = "error_code"
  }
}

private fun <K, V : Any> mapOfNonNull(vararg pairs: Pair<K, V?>) = mapOf(*pairs)
  .filterValues { it != null }
  .mapValues { it.value as V }
