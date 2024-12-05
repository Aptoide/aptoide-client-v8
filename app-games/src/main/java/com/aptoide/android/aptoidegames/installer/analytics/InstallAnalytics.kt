package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.asNullableParameter
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import com.aptoide.android.aptoidegames.analytics.toBIParameters
import com.aptoide.android.aptoidegames.analytics.toBiParameters
import com.aptoide.android.aptoidegames.analytics.toGenericParameters
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics.Companion.P_UPDATE_TYPE

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
    genericAnalytics.logEvent(
      name = "install_clicked",
      params = analyticsContext.toGenericParameters(
        *app.toGenericParameters(),
        P_SERVICE to networkType
      )
    )

    sendBIClickEvent(
      app = app,
      analyticsContext = analyticsContext,
      action = "install"
    )

    if (analyticsContext.isApkfy) {
      genericAnalytics.logEvent(
        name = "install_clicked_apkfy",
        params = analyticsContext.toGenericParameters(
          *app.toGenericParameters(),
          P_SERVICE to networkType
        )
      )
    }
  }

  fun sendUpdateClickEvent(
    app: App,
    analyticsContext: AnalyticsUIContext,
    networkType: String,
  ) {
    genericAnalytics.logEvent(
      name = "update_clicked",
      params = analyticsContext.toGenericParameters(
        *app.toGenericParameters(),
        P_SERVICE to networkType
      )
    )

    sendBIClickEvent(
      app = app,
      analyticsContext = analyticsContext,
      action = "update"
    )
  }

  fun sendRetryClick(
    app: App,
    networkType: String,
    analyticsContext: AnalyticsUIContext,
  ) {
    genericAnalytics.logEvent(
      name = "retry_app_clicked",
      params = analyticsContext.toGenericParameters(
        *app.toGenericParameters(),
        P_SERVICE to networkType
      )
    )
    sendBIClickEvent(
      app = app,
      analyticsContext = analyticsContext,
      action = "retry"
    )
  }

  fun sendOpenClick(
    packageName: String,
    hasAPPCBilling: Boolean?,
    analyticsContext: AnalyticsUIContext,
  ) {
    genericAnalytics.logEvent(
      name = "open_clicked",
      params = analyticsContext.toGenericParameters(
        GenericAnalytics.P_PACKAGE_NAME to packageName,
        GenericAnalytics.P_APPC_BILLING to hasAPPCBilling
      )
    )
  }

  fun sendDownloadStartedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) {
    genericAnalytics.logEvent(
      name = "app_download",
      params = analyticsPayload.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "started"
      )
    )
  }

  fun sendDownloadCompletedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    appSizeSegment: Int,
  ) {
    genericAnalytics.logEvent(
      name = "app_download",
      params = analyticsPayload.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "success"
      )
    )

    logBIDownloadEvent(
      packageName = packageName,
      status = "success",
      analyticsPayload = analyticsPayload,
      appSizeSegment = appSizeSegment,
    )
  }

  fun sendDownloadErrorEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    appSizeSegment: Int,
    errorMessage: String?,
    errorType: String?,
    errorCode: Int?,
  ) {
    genericAnalytics.logEvent(
      name = "app_download",
      params = analyticsPayload.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "fail",
        P_ERROR_MESSAGE to (errorMessage ?: "failure")
      )
    )

    logBIDownloadEvent(
      packageName = packageName,
      status = "fail",
      analyticsPayload = analyticsPayload,
      appSizeSegment = appSizeSegment,
      P_ERROR_MESSAGE to errorMessage,
      P_ERROR_TYPE to errorType,
      P_ERROR_HTTP_CODE to errorCode,
    )
  }

  fun sendDownloadAbortEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    appSizeSegment: Int,
    errorMessage: String?,
  ) {
    genericAnalytics.logEvent(
      name = "app_download",
      params = analyticsPayload.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "fail",
        P_ERROR_MESSAGE to (errorMessage ?: "failure")
      )
    )

    logBIDownloadEvent(
      packageName = packageName,
      status = "abort",
      analyticsPayload = analyticsPayload,
      appSizeSegment = appSizeSegment,
      P_ERROR_MESSAGE to errorMessage,
      P_ERROR_TYPE to "permission",
    )
  }

  fun sendDownloadCancelEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    appSizeSegment: Int,
  ) {
    genericAnalytics.logEvent(
      name = "app_download",
      params = analyticsPayload.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "cancel"
      )
    )

    logBIDownloadEvent(
      packageName = packageName,
      status = "cancel",
      analyticsPayload = analyticsPayload,
      appSizeSegment = appSizeSegment,
    )
  }

  fun sendInstallDialogImpressionEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?
  ) {
    biAnalytics.logEvent(
      "install_dialog_impression",
      analyticsPayload.let {
        it.toAppBIParameters(packageName) +
          mapOfNonNull(
            P_STORE to it?.store,
            P_TRUSTED_BADGE to it?.trustedBadge
          )
      }
    )
  }

  fun sendInstallStartedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) {
    genericAnalytics.logEvent(
      name = "app_installed",
      params = analyticsPayload.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "started"
      )
    )
  }

  fun sendInstallCancelEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) {
    genericAnalytics.logEvent(
      name = "app_installed",
      params = analyticsPayload.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "cancel"
      )
    )
  }

  fun sendInstallCompletedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) {
    genericAnalytics.logEvent(
      name = "app_installed",
      params = analyticsPayload.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "success"
      )
    )

    logBIInstallEvent(
      packageName = packageName,
      status = "success",
      analyticsPayload = analyticsPayload
    )
  }

  fun sendInstallErrorEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    errorMessage: String?,
    errorType: String?,
  ) {
    genericAnalytics.logEvent(
      name = "app_installed",
      params = analyticsPayload.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "fail",
        P_ERROR_MESSAGE to (errorMessage ?: "failure")
      )
    )

    logBIInstallEvent(
      packageName = packageName,
      status = "fail",
      analyticsPayload = analyticsPayload,
      P_ERROR_MESSAGE to errorMessage,
      P_ERROR_TYPE to errorType,
    )
  }

  fun sendUninstallClick(app: App) {
    genericAnalytics.logEvent(
      name = "oos_uninstall_clicked",
      params = mapOf(
        *app.toGenericParameters()
      )
    )
  }

  fun sendResumeDownloadClick(
    app: App,
    downloadOnlyOverWifiSetting: Boolean,
  ) {
    genericAnalytics.logEvent(
      name = "resume_download_clicked",
      params = mapOf(
        *app.toGenericParameters(),
        P_WIFI_SETTING to downloadOnlyOverWifiSetting
      )
    )
  }

  fun sendDownloadRestartedEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
  ) = genericAnalytics.logEvent(
    name = "app_download",
    params = analyticsPayload.toAppGenericParameters(
      packageName = packageName,
      P_STATUS to "restart"
    )
  )

  fun sendDownloadCancel(
    app: App,
    analyticsContext: AnalyticsUIContext,
  ) {
    genericAnalytics.logEvent(
      name = "download_canceled",
      params = analyticsContext.toGenericParameters(
        *app.toGenericParameters()
      )
    )
  }

  fun sendWifiPromptShown(
    app: App,
    downloadOnlyOverWifiSetting: Boolean,
  ) = genericAnalytics.logEvent(
    name = "wifi_prompt_shown",
    params = mapOf(
      *app.toGenericParameters(),
      P_WIFI_SETTING to downloadOnlyOverWifiSetting,
    )
  )

  fun sendWaitForWifiClicked(
    app: App,
    downloadOnlyOverWifi: Boolean,
  ) = genericAnalytics.logEvent(
    name = "wait_for_wifi_clicked",
    params = mapOf(
      GenericAnalytics.P_PACKAGE_NAME to app.packageName,
      GenericAnalytics.P_APP_SIZE to app.appSize,
      P_WIFI_SETTING to downloadOnlyOverWifi
    )
  )

  fun sendDownloadNowClicked(
    packageName: String,
    appSize: Long,
    promptType: String,
    downloadOnlyOverWifi: Boolean,
  ) = genericAnalytics.logEvent(
    name = "download_now_clicked",
    params = mapOf(
      GenericAnalytics.P_PACKAGE_NAME to packageName,
      GenericAnalytics.P_APP_SIZE to appSize,
      P_WIFI_SETTING to downloadOnlyOverWifi,
      P_PROMPT_TYPE to promptType
    )
  )

  /**Reused functions**/

  private fun sendBIClickEvent(
    app: App,
    analyticsContext: AnalyticsUIContext,
    action: String,
  ) = biAnalytics.logEvent(
    name = "click_on_install_button",
    app.toBIParameters(aabTypes = null) +
      analyticsContext.toBiParameters(
        P_ACTION to action,
        P_STORE to storeName,
        P_TRUSTED_BADGE to app.malware.asNullableParameter()
      )
  )

  private fun logBIDownloadEvent(
    packageName: String,
    status: String,
    analyticsPayload: AnalyticsPayload?,
    appSizeSegment: Int,
    vararg pairs: Pair<String, Any?>
  ) = biAnalytics.logEvent(
    name = "download",
    analyticsPayload.let {
      it.toAppBIParameters(packageName) +
        it?.toAnalyticsUiContext().toBiParameters(
          P_STATUS to status,
          P_APP_SIZE_MB to appSizeSegment,
          P_STORE to it?.store,
          P_TRUSTED_BADGE to it?.trustedBadge,
          *pairs
        )
    }
  )

  private fun logBIInstallEvent(
    packageName: String,
    status: String,
    analyticsPayload: AnalyticsPayload?,
    vararg pairs: Pair<String, Any?>
  ) = biAnalytics.logEvent(
    name = "install",
    analyticsPayload.let {
      it.toAppBIParameters(packageName) +
        it?.toAnalyticsUiContext().toBiParameters(
          P_STATUS to status,
          P_STORE to it?.store,
          P_TRUSTED_BADGE to it?.trustedBadge,
          *pairs
        )
    }
  )

  companion object {
    private const val P_ACTION = "action"
    private const val P_STORE = "store"
    private const val P_STATUS = "status"
    private const val P_APP_SIZE_MB = "app_size_mb"
    private const val P_TRUSTED_BADGE = "trusted_badge"
    private const val P_ERROR_MESSAGE = "error_message"
    private const val P_ERROR_TYPE = "error_type"
    private const val P_ERROR_HTTP_CODE = "error_http_code"
    internal const val P_WIFI_SETTING = "wifi_setting"
    internal const val P_PROMPT_TYPE = "prompt_type"
    internal const val P_SERVICE = "service"
    internal const val P_UPDATE_TYPE = "update_type"
  }
}

private fun AnalyticsPayload?.toAppGenericParameters(
  packageName: String,
  vararg pairs: Pair<String, Any?>
): Map<String, Any> =
  this?.run {
    toAnalyticsUiContext().toGenericParameters(
      *pairs,
      GenericAnalytics.P_PACKAGE_NAME to packageName,
      GenericAnalytics.P_APPC_BILLING to isAppCoins,
      P_UPDATE_TYPE to userClicks.toTapsValue()
    )
  } ?: mapOfNonNull(*pairs)

private fun AnalyticsPayload?.toAppBIParameters(
  packageName: String,
  vararg pairs: Pair<String, Any?>,
): Map<String, Any> =
  this?.run {
    mapOfNonNull(
      *pairs,
      BIAnalytics.P_PACKAGE_NAME to packageName,
      BIAnalytics.P_APP_AAB to isAab,
      BIAnalytics.P_APP_AAB_INSTALL_TIME to aabTypes,
      BIAnalytics.P_APP_APPC to isAppCoins,
      BIAnalytics.P_APP_VERSION_CODE to versionCode,
      BIAnalytics.P_APP_OBB to hasObb,
      BIAnalytics.P_APP_IN_CATAPPULT to isInCatappult.asNullableParameter(),
      P_UPDATE_TYPE to userClicks.toTapsValue()
    )
  } ?: mapOfNonNull(*pairs)

private fun Int.toTapsValue(): String = if (this == 1) "1-tap" else "$this-taps"
