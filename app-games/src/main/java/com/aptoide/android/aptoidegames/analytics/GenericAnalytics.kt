package com.aptoide.android.aptoidegames.analytics

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_home.domain.BundleSource
import cm.aptoide.pt.install_manager.InstallManager
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.ProductInfoData
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsPayload
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
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
      .onEach { analyticsSender.setUserProperty("theme", it) }
      .launchIn(CoroutineScope(Dispatchers.IO))
    installManager
      .getApp("com.appcoins.wallet")
      .packageInfoFlow
      .map { (it != null).toString() }
      .onEach { analyticsSender.setUserProperty("wallet_installed", it) }
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
    params = getInstallData(
      packageName = packageName,
      analyticsContext = analyticsContext
    )
  )

  fun sendInstallClick(
    app: App,
    networkType: String,
    analyticsContext: AnalyticsUIContext,
  ) {
    analyticsSender.logEvent(
      name = "install_clicked",
      params = getInstallData(
        packageName = app.packageName,
        hasAPPCBilling = app.isAppCoins,
        analyticsContext = analyticsContext
      )
        .addService(networkType)
    )
  }

  fun sendOpenClick(
    packageName: String,
    hasAPPCBilling: Boolean? = null,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "open_clicked",
    params = getInstallData(
      packageName = packageName,
      hasAPPCBilling = hasAPPCBilling,
      analyticsContext = analyticsContext
    )
  )

  fun sendRetryClick(
    app: App,
    networkType: String,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "retry_app_clicked",
    params = getInstallData(
      packageName = app.packageName,
      hasAPPCBilling = app.isAppCoins,
      analyticsContext = analyticsContext
    )
      .addService(networkType)
  )

  fun sendNoNetworkRetry() = analyticsSender.logEvent(
    name = "retry_no_connection_clicked",
    params = Bundle()
  )

  fun sendUpdateClick(
    app: App,
    networkType: String,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "update_clicked",
    params = getInstallData(
      packageName = app.packageName,
      hasAPPCBilling = app.isAppCoins,
      analyticsContext = analyticsContext
    )
      .addService(networkType)
  )

  fun sendResumeDownloadClick(
    packageName: String,
    downloadOnlyOverWifiSetting: Boolean,
    appSize: Long,
  ) {
    analyticsSender.logEvent(
      name = "resume_download_clicked",
      params = Bundle()
        .addPackageName(packageName)
        .addDownloadOnlyOverWifiSetting(downloadOnlyOverWifiSetting)
        .addAppSize(appSize)
    )
  }

  fun sendDownloadStartedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_download",
    params = getInstallData(packageName, analyticsPayload)
      .addStatusSuccess("started")
  )

  fun sendDownloadRestartedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_download",
    params = getInstallData(packageName, analyticsPayload)
      .addStatusSuccess("restart")
  )

  fun sendDownloadCompletedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_download",
    params = getInstallData(packageName, analyticsPayload)
      .addStatusSuccess()
  )

  fun sendInstallStartedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_installed",
    params = getInstallData(packageName, analyticsPayload)
      .addStatusSuccess("started")
  )

  fun sendInstallCompletedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_installed",
    params = getInstallData(packageName, analyticsPayload)
      .addStatusSuccess()
  )

  fun sendDownloadCancelEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_download",
    params = getInstallData(packageName, analyticsPayload)
      .addStatusCancel()
  )

  fun sendInstallCancelEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = analyticsSender.logEvent(
    name = "app_installed",
    params = getInstallData(packageName, analyticsPayload)
      .addStatusCancel()
  )

  fun sendDownloadErrorEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    errorMessage: String?,
  ) = analyticsSender.logEvent(
    name = "app_download",
    params = getInstallData(packageName, analyticsPayload)
      .addStatusFail()
      .addErrorMessage(errorMessage)
  )

  fun sendInstallErrorEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    errorMessage: String?,
  ) = analyticsSender.logEvent(
    name = "app_installed",
    params = getInstallData(packageName, analyticsPayload)
      .addStatusFail()
      .addErrorMessage(errorMessage)
  )

  fun sendOpenAppEvent(
    appOpenSource: String,
    isFirstLaunch: Boolean,
    networkType: String,
  ) = analyticsSender.logEvent(
    name = "app_open",
    params = Bundle()
      .addString(
        key = "open_type",
        value = appOpenSource
      )
      .addString(
        key = "first_launch",
        value = if (isFirstLaunch) "true" else "false"
      )
      .addService(networkType)
  )

  fun sendEngagedUserEvent() = analyticsSender.logEvent(
    name = "engaged_user",
    params = Bundle()
  )

  fun sendAppPromoClick(
    app: App,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "app_promo_clicked",
    params = getInstallData(
      packageName = app.packageName,
      hasAPPCBilling = app.isAppCoins,
      analyticsContext = analyticsContext
    )
  )

  fun sendSeeAllClick(analyticsContext: AnalyticsUIContext) = analyticsSender.logEvent(
    name = "see_all_clicked",
    params = Bundle()
      .addSectionId(analyticsContext.bundleMeta?.tag)
      .addSectionType(analyticsContext.bundleMeta?.bundleSource)
  )

  fun sendBackButtonClick(analyticsContext: AnalyticsUIContext) = analyticsSender.logEvent(
    name = "back_button_clicked",
    params = Bundle()
      .addContext(analyticsContext.currentScreen)
      .addSectionId(analyticsContext.bundleMeta?.tag)
      .addSectionType(analyticsContext.bundleMeta?.bundleSource)
      .addItemPosition(analyticsContext.itemPosition)
  )

  fun sendCarouselSwipe(
    count: Int,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "carousel_swipe",
    params = Bundle()
      .addInt("scroll_count", count)
      .addSectionId(analyticsContext.bundleMeta?.tag)
      .addSectionType(analyticsContext.bundleMeta?.bundleSource)
  )

  fun sendMenuClick(link: String) = analyticsSender.logEvent(
    name = "menu_clicked",
    params = Bundle().addString("link", link)
  )

  fun sendCategoryClick(
    categoryName: String,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "category_clicked",
    params = Bundle()
      .addCategory(categoryName)
      .addItemPosition(analyticsContext.itemPosition)
  )

  fun sendBottomBarHomeClick() = analyticsSender.logEvent(
    name = "bn_home_clicked",
    params = Bundle()
  )

  fun sendBottomBarSearchClick() = analyticsSender.logEvent(
    name = "bn_search_clicked",
    params = Bundle()
  )

  fun sendBottomBarCategoriesClick() = analyticsSender.logEvent(
    name = "bn_categories_clicked",
    params = Bundle()
  )

  fun sendNotificationOptIn() = analyticsSender.logEvent(
    name = "notification_opt_in",
    params = Bundle()
  )

  fun sendNotificationOptOut() = analyticsSender.logEvent(
    name = "notification_opt_out",
    params = Bundle()
  )

  fun sendGetNotifiedContinueClick() = analyticsSender.logEvent(
    name = "get_notified_continue_clicked",
    params = Bundle()
  )

  fun sendDownloadCancel(
    packageName: String,
    analyticsContext: AnalyticsUIContext,
  ) = analyticsSender.logEvent(
    name = "download_canceled",
    params = getInstallData(
      packageName = packageName,
      analyticsContext = analyticsContext
    )
  )

  fun sendSearchMadeEvent(searchMeta: SearchMeta) = analyticsSender.logEvent(
    name = "search_made",
    params = Bundle()
      .addString("inserted_keyword", searchMeta.insertedKeyword)
      .addString("search_keyword", searchMeta.searchKeyword)
      .addString("search_type", searchMeta.searchType)
  )

  fun sendNotEnoughSpaceDialogShow(
    packageName: String,
    appSize: Long,
  ) = analyticsSender.logEvent(
    name = "oos_not_enough_space",
    params = Bundle()
      .addPackageName(packageName)
      .addAppSize(appSize)
  )

  fun sendUninstallClick(
    packageName: String,
    appSize: Long,
  ) = analyticsSender.logEvent(
    name = "oos_uninstall_clicked",
    params = Bundle()
      .addPackageName(packageName)
      .addAppSize(appSize)
  )

  fun sendOOsGoBackButtonClick() = analyticsSender.logEvent(
    name = "oos_go_back_clicked",
    params = Bundle()
  )

  fun sendDownloadOverWifiDisabled() = analyticsSender.logEvent(
    name = "download_over_wifi_disabled",
    params = Bundle()
  )

  fun sendDownloadOverWifiEnabled() = analyticsSender.logEvent(
    name = "download_over_wifi_enabled",
    params = Bundle()
  )

  fun sendWifiPromptShown(
    app: App,
    downloadOnlyOverWifiSetting: Boolean,
  ) = analyticsSender.logEvent(
    name = "wifi_prompt_shown",
    params = Bundle()
      .addDownloadOnlyOverWifiSetting(downloadOnlyOverWifiSetting)
      .addPackageName(app.packageName)
      .addAppSize(app.appSize)
  )

  fun sendWaitForWifiClicked(
    app: App,
    downloadOnlyOverWifi: Boolean,
  ) = analyticsSender.logEvent(
    name = "wait_for_wifi_clicked",
    params = Bundle()
      .addDownloadOnlyOverWifiSetting(downloadOnlyOverWifi)
      .addPackageName(app.packageName)
      .addAppSize(app.appSize)
  )

  fun sendDownloadNowClicked(
    downloadOnlyOverWifi: Boolean,
    promptType: String,
    packageName: String,
    appSize: Long,
  ) = analyticsSender.logEvent(
    name = "download_now_clicked",
    params = Bundle()
      .addDownloadOnlyOverWifiSetting(downloadOnlyOverWifi)
      .addPromptType(promptType)
      .addPackageName(packageName)
      .addAppSize(appSize)
  )

  fun sendPaymentStartEvent(
    packageName: String,
    productInfoData: ProductInfoData?,
  ) = analyticsSender.logEvent(
    name = "iap_payment_start",
    params = getPurchaseData(packageName, productInfoData)
  )

  fun sendPaymentMethodsDismissedEvent(
    packageName: String,
    productInfoData: ProductInfoData?,
  ) = analyticsSender.logEvent(
    name = "iap_payment_dismissed",
    params = getPurchaseData(
      packageName = packageName,
      productInfoData = productInfoData
    )
      .addPaymentMethod("list")
      .addContext("start")
  )

  fun sendPaymentDismissedEvent(
    paymentMethod: PaymentMethod<*>,
    context: String?,
  ) = analyticsSender.logEvent(
    name = "iap_payment_dismissed",
    params = getPurchaseData(
      packageName = paymentMethod.purchaseRequest.domain,
      productInfoData = paymentMethod.productInfo
    )
      .addPaymentMethod(paymentMethod.id)
      .addContext(context)
  )

  fun sendPaymentBackEvent(paymentMethod: PaymentMethod<*>) = analyticsSender.logEvent(
    name = "iap_payment_back",
    params = getPurchaseData(
      packageName = paymentMethod.purchaseRequest.domain,
      productInfoData = paymentMethod.productInfo
    )
      .addPaymentMethod(paymentMethod.id)
  )

  fun sendPaymentBuyEvent(paymentMethod: PaymentMethod<*>) = analyticsSender.logEvent(
    name = "iap_payment_buy",
    params = getPurchaseData(
      packageName = paymentMethod.purchaseRequest.domain,
      productInfoData = paymentMethod.productInfo
    )
      .addPaymentMethod(paymentMethod.id)
  )

  fun sendPaymentTryAgainEvent(paymentMethod: PaymentMethod<*>) = analyticsSender.logEvent(
    name = "iap_payment_try_again",
    params = getPurchaseData(
      packageName = paymentMethod.purchaseRequest.domain,
      productInfoData = paymentMethod.productInfo
    )
      .addPaymentMethod(paymentMethod.id)
  )

  fun sendPaymentConclusionEvent(
    paymentMethod: PaymentMethod<*>,
    status: String,
    errorCode: String? = null,
  ) = analyticsSender.logEvent(
    name = "iap_payment_conclusion",
    params = getPurchaseData(
      packageName = paymentMethod.purchaseRequest.domain,
      productInfoData = paymentMethod.productInfo
    )
      .addPaymentMethod(paymentMethod.id)
      .addPaymentStatus(status)
      .addPaymentErrorCode(errorCode)
  )

  fun sendPaymentMethodsEvent(paymentMethod: PaymentMethod<*>) = analyticsSender.logEvent(
    name = "iap_payment_methods",
    params = getPurchaseData(
      packageName = paymentMethod.purchaseRequest.domain,
      productInfoData = paymentMethod.productInfo
    )
      .addPaymentMethod(paymentMethod.id)
  )

  fun sendPaymentSupportClicked() = analyticsSender.logEvent(
    name = "payment_support_clicked",
    params = Bundle()
  )

  fun sendSendFeedbackClicked() = analyticsSender.logEvent(
    name = "send_feedback_clicked",
    params = Bundle()
  )

  fun sendFeedbackSent(feedbackType: String) = analyticsSender.logEvent(
    name = "feedback_sent",
    params = Bundle().addString("feedback_type", feedbackType)
  )

  /**
   * Helper functions for better readability
   */

  private fun getInstallData(
    packageName: String,
    hasAPPCBilling: Boolean? = null,
    analyticsContext: AnalyticsUIContext,
  ): Bundle = Bundle()
    .addContext(analyticsContext.currentScreen)
    .addSectionId(analyticsContext.bundleMeta?.tag)
    .addPackageName(packageName)
    .addSectionType(analyticsContext.bundleMeta?.bundleSource)
    .addItemPosition(analyticsContext.itemPosition)
    .addAPPCBilling(hasAPPCBilling)
    .addInsertedKeyword(analyticsContext.searchMeta?.insertedKeyword)
    .addSearchKeyword(analyticsContext.searchMeta?.searchKeyword)
    .addSearchType(analyticsContext.searchMeta?.searchType)

  private fun getInstallData(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ): Bundle = Bundle()
    .addContext(analyticsPayload?.context)
    .addSectionId(analyticsPayload?.bundleMeta?.tag)
    .addPackageName(packageName)
    .addSectionType(analyticsPayload?.bundleMeta?.bundleSource)
    .addItemPosition(analyticsPayload?.itemPosition)
    .addAPPCBilling(analyticsPayload?.isAppCoins)

  private fun getPurchaseData(
    packageName: String,
    productInfoData: ProductInfoData?,
  ): Bundle = Bundle()
    .addPackageName(packageName)
    .addSkuId(productInfoData?.sku)
    .addSkuName(productInfoData?.title)
    .addPrice(productInfoData?.priceValue)
    .addCurrency(productInfoData?.priceCurrency)

  private fun Bundle.addContext(context: String?): Bundle = addString("context", context)
  private fun Bundle.addSectionId(sectionId: String?): Bundle = addString("section_id", sectionId)
  private fun Bundle.addSectionType(sectionType: String?): Bundle =
    addString("section_type", sectionType?.takeIf { it != BundleSource.NONE.name })

  private fun Bundle.addItemPosition(position: Int?): Bundle = addInt("item_position", position)

  private fun Bundle.addInsertedKeyword(insertedKeyword: String?) =
    addString("inserted_keyword", insertedKeyword)

  private fun Bundle.addSearchKeyword(searchKeyword: String?) =
    addString("search_keyword", searchKeyword)

  private fun Bundle.addSearchType(searchType: String?) = addString("search_type", searchType)

  private fun Bundle.addPackageName(packageName: String) = addString("package_name", packageName)
  private fun Bundle.addCategory(categoryName: String): Bundle = addString("category", categoryName)
  private fun Bundle.addAPPCBilling(hasAPPCBilling: Boolean?): Bundle =
    addString("appc_billing", hasAPPCBilling?.toString())

  private fun Bundle.addAppSize(appSize: Long): Bundle = addString("App_size", appSize.toString())
  private fun Bundle.addDownloadOnlyOverWifiSetting(downloadOnlyOverWifi: Boolean): Bundle =
    addString("wifi_setting", downloadOnlyOverWifi.toString())

  private fun Bundle.addPromptType(promptType: String): Bundle =
    addString("prompt_type", promptType)

  private fun Bundle.addService(networkType: String): Bundle = addString("service", networkType)
  private fun Bundle.addStatusSuccess(status: String = "success"): Bundle =
    addString("status", status)

  private fun Bundle.addStatusCancel(): Bundle = addString("status", "cancel")
  private fun Bundle.addStatusFail(): Bundle = addString("status", "fail")
  private fun Bundle.addErrorMessage(message: String?): Bundle =
    addString("error_message", message ?: "failure")

  private fun Bundle.addSkuId(skuId: String?): Bundle = addString("sku_id", skuId)
  private fun Bundle.addSkuName(skuName: String?): Bundle = addString("Sku_name", skuName)
  private fun Bundle.addPrice(price: String?): Bundle = addString("price", price)
  private fun Bundle.addCurrency(currency: String?): Bundle = addString("currency", currency)
  private fun Bundle.addPaymentMethod(paymentMethod: String): Bundle =
    addString("payment_method", paymentMethod)

  private fun Bundle.addPaymentStatus(status: String): Bundle = addString("status", status)
  private fun Bundle.addPaymentErrorCode(errorCode: String?): Bundle =
    addString("error_code", errorCode)

  private fun Bundle.addString(
    key: String,
    value: String?,
  ): Bundle = apply {
    value?.let { putString(key, it) }
  }

  private fun Bundle.addInt(
    key: String,
    value: Int?,
  ): Bundle = apply {
    value?.let { putInt(key, it) }
  }
}
