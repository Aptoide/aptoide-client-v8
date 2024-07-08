package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.asNullableParameter
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsPayload
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import com.aptoide.android.aptoidegames.analytics.toAppBIParameters
import com.aptoide.android.aptoidegames.analytics.toBIParameters

class InstallAnalytics(
  private val genericAnalytics: GenericAnalytics,
  private val biAnalytics: BIAnalytics,
  private val storeName: String,
) {

  fun sendInstallClickEvent(
    app: App,
    analyticsContext: AnalyticsUIContext,
    networkType: String,
  ) {
    genericAnalytics.sendInstallClick(
      app = app,
      networkType = networkType,
      analyticsContext = analyticsContext,
    )

    sendClickEvent(
      app = app,
      analyticsContext = analyticsContext,
      action = "install"
    )
  }

  fun sendUpdateClickEvent(
    app: App,
    analyticsContext: AnalyticsUIContext,
    networkType: String,
  ) {
    genericAnalytics.sendUpdateClick(
      app = app,
      networkType = networkType,
      analyticsContext = analyticsContext,
    )

    sendClickEvent(
      app = app,
      analyticsContext = analyticsContext,
      action = "update"
    )
  }

  private fun sendClickEvent(
    app: App,
    analyticsContext: AnalyticsUIContext,
    action: String,
  ) {
    biAnalytics.logEvent(
      name = "click_on_install_button",
      app.toBIParameters(aabTypes = null) +
        mapOfNonNull(
          P_ACTION to action,
          P_APKFY_APP_INSTALL to analyticsContext.isApkfy,
          P_CONTEXT to analyticsContext.currentScreen,
          P_PREVIOUS_CONTEXT to analyticsContext.previousScreen,
          P_STORE to storeName,
          P_TAG to analyticsContext.bundleMeta?.tag,
          P_TRUSTED_BADGE to app.malware.asNullableParameter()
        )
    )
  }

  fun sendDownloadStartedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) {
    genericAnalytics.sendDownloadStartedEvent(
      packageName = packageName,
      analyticsPayload = analyticsPayload
    )
  }

  fun sendDownloadCompletedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) {
    genericAnalytics.sendDownloadCompletedEvent(
      packageName = packageName,
      analyticsPayload = analyticsPayload
    )

    biAnalytics.logEvent(
      name = "download",
      analyticsPayload.let {
        it.toAppBIParameters(packageName) +
          mapOfNonNull(
            P_STATUS to "success",
            P_APKFY_APP_INSTALL to it?.isApkfy,
            P_CONTEXT to it?.context,
            P_PREVIOUS_CONTEXT to it?.previousContext,
            P_STORE to it?.store,
            P_TAG to it?.bundleMeta?.tag,
            P_TRUSTED_BADGE to it?.trustedBadge
          )
      }
    )
  }

  fun sendDownloadErrorEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    errorMessage: String?,
    errorType: String?,
  ) {
    genericAnalytics.sendDownloadErrorEvent(
      packageName = packageName,
      analyticsPayload = analyticsPayload,
      errorMessage = errorMessage
    )

    biAnalytics.logEvent(
      name = "download",
      analyticsPayload.let {
        it.toAppBIParameters(packageName) +
          mapOfNonNull(
            P_STATUS to "fail",
            P_APKFY_APP_INSTALL to it?.isApkfy,
            P_CONTEXT to it?.context,
            P_ERROR_MESSAGE to errorMessage,
            P_ERROR_TYPE to errorType,
            P_PREVIOUS_CONTEXT to it?.previousContext,
            P_STORE to it?.store,
            P_TAG to it?.bundleMeta?.tag,
            P_TRUSTED_BADGE to it?.trustedBadge
          )
      }
    )
  }

  fun sendDownloadCancelEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) {
    genericAnalytics.sendDownloadCancelEvent(
      packageName = packageName,
      analyticsPayload = analyticsPayload
    )
  }

  fun sendInstallStartedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) {
    genericAnalytics.sendInstallStartedEvent(
      packageName = packageName,
      analyticsPayload = analyticsPayload
    )
  }

  fun sendInstallCancelEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) {
    genericAnalytics.sendInstallCancelEvent(
      packageName = packageName,
      analyticsPayload = analyticsPayload
    )
  }

  fun sendInstallCompletedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) {
    genericAnalytics.sendInstallCompletedEvent(
      packageName = packageName,
      analyticsPayload = analyticsPayload
    )

    biAnalytics.logEvent(
      name = "install",
      analyticsPayload.let {
        it.toAppBIParameters(packageName) +
          mapOfNonNull(
            P_STATUS to "success",
            P_APKFY_APP_INSTALL to it?.isApkfy,
            P_CONTEXT to it?.context,
            P_PREVIOUS_CONTEXT to it?.previousContext,
            P_STORE to it?.store,
            P_TAG to it?.bundleMeta?.tag,
            P_TRUSTED_BADGE to it?.trustedBadge
          )
      }
    )
  }

  fun sendInstallErrorEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    errorMessage: String?,
    errorType: String?,
  ) {
    genericAnalytics.sendInstallErrorEvent(
      packageName = packageName,
      analyticsPayload = analyticsPayload,
      errorMessage = errorMessage
    )

    biAnalytics.logEvent(
      name = "install",
      analyticsPayload.let {
        it.toAppBIParameters(packageName) +
          mapOfNonNull(
            P_STATUS to "fail",
            P_APKFY_APP_INSTALL to it?.isApkfy,
            P_CONTEXT to it?.context,
            P_ERROR_MESSAGE to errorMessage,
            P_ERROR_TYPE to errorType,
            P_PREVIOUS_CONTEXT to it?.previousContext,
            P_STORE to it?.store,
            P_TAG to it?.bundleMeta?.tag,
            P_TRUSTED_BADGE to it?.trustedBadge
          )
      }
    )
  }

  fun sendUninstallClick(
    packageName: String,
    appSize: Long,
  ) {
    genericAnalytics.sendUninstallClick(
      packageName = packageName,
      appSize = appSize
    )
  }

  fun sendResumeDownloadClick(
    packageName: String,
    downloadOnlyOverWifiSetting: Boolean,
    appSize: Long,
  ) {
    genericAnalytics.sendResumeDownloadClick(
      packageName = packageName,
      downloadOnlyOverWifiSetting = downloadOnlyOverWifiSetting,
      appSize = appSize
    )
  }

  fun sendDownloadCancel(
    packageName: String,
    analyticsContext: AnalyticsUIContext,
  ) {
    genericAnalytics.sendDownloadCancel(
      packageName = packageName,
      analyticsContext = analyticsContext
    )
  }

  fun sendOpenClick(
    packageName: String,
    hasAPPCBilling: Boolean? = null,
    analyticsContext: AnalyticsUIContext,
  ) {
    genericAnalytics.sendOpenClick(
      packageName = packageName,
      hasAPPCBilling = hasAPPCBilling,
      analyticsContext = analyticsContext
    )
  }

  fun sendRetryClick(
    app: App,
    networkType: String,
    analyticsContext: AnalyticsUIContext,
  ) {
    genericAnalytics.sendRetryClick(
      app = app,
      networkType = networkType,
      analyticsContext = analyticsContext
    )
  }

  companion object {
    private const val P_ACTION = "action"
    private const val P_APKFY_APP_INSTALL = "apkfy_app_install"
    private const val P_CONTEXT = "context"
    private const val P_PREVIOUS_CONTEXT = "previous_context"
    private const val P_STORE = "store"
    private const val P_STATUS = "status"
    private const val P_TAG = "tag"
    private const val P_TRUSTED_BADGE = "trusted_badge"
    private const val P_ERROR_MESSAGE = "error_message"
    private const val P_ERROR_TYPE = "error_type"
  }
}
