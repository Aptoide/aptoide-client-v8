package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsPayload
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext

class InstallAnalytics(
  private val genericAnalytics: GenericAnalytics,
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
  }

  fun sendDownloadErrorEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    errorMessage: String?,
  ) {
    genericAnalytics.sendDownloadErrorEvent(
      packageName = packageName,
      analyticsPayload = analyticsPayload,
      errorMessage = errorMessage
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
  }

  fun sendInstallErrorEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?,
    errorMessage: String?,
  ) {
    genericAnalytics.sendInstallErrorEvent(
      packageName = packageName,
      analyticsPayload = analyticsPayload,
      errorMessage = errorMessage
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
}
