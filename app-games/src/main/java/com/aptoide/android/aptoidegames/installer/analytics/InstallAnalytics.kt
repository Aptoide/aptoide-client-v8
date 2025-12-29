package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext

interface InstallAnalytics {

  fun setUsedInstallerProperty(installerUsed: String) {}

  fun sendClickEvent(
    app: App,
    analyticsContext: AnalyticsUIContext,
    networkType: String,
  ) {
  }

  fun sendOpenClick(
    packageName: String,
    hasAPPCBilling: Boolean?,
    analyticsContext: AnalyticsUIContext,
  ) {
  }

  fun sendOnInstallationQueued(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
  }

  fun sendOnInstallationRemovedFromQueue(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
  }

  fun sendDownloadStartedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
  }

  fun sendDownloadCompletedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    downloadedBytesPerSecond: Double,
  ) {
  }

  fun sendDownloadCachedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ) {
  }

  fun sendDownloadErrorEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    downloadedBytesPerSecond: Double,
    errorMessage: String?,
    errorType: String?,
    errorCode: Int?,
    errorUrl: String?,
  ) {
  }

  fun sendDownloadAbortEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    errorMessage: String?,
  ) {
  }

  fun sendDownloadCanceledEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    downloadedBytesPerSecond: Double,
  ) {
  }

  fun sendDownloadCanceledInQueueEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
  }

  fun sendAutomaticQueueDownloadCancelEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
  }

  fun sendInstallDialogImpressionEvent(
    packageName: String,
    analyticsPayload: AnalyticsPayload?
  ) {
  }

  fun sendInstallStartedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
  }

  fun sendInstallCancelEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
  }

  fun sendInstallCompletedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
  }

  fun sendInstallErrorEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    errorMessage: String?,
    errorType: String?,
  ) {
  }

  fun sendInstallAbortEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    errorMessage: String?,
  ) {
  }

  fun sendResumeDownloadClick(
    app: App,
    downloadOnlyOverWifiSetting: Boolean,
  ) {
  }

  fun sendDownloadRestartedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ) {
  }

  fun sendDownloadCancel(
    app: App,
    analyticsContext: AnalyticsUIContext,
  ) {
  }

  fun sendWifiPromptShown(
    app: App,
    downloadOnlyOverWifiSetting: Boolean,
  ) {
  }

  fun sendWaitForWifiClicked(
    app: App,
    downloadOnlyOverWifi: Boolean,
  ) {
  }

  fun sendDownloadNowClicked(
    packageName: String,
    appSize: Long,
    promptType: String,
    downloadOnlyOverWifi: Boolean,
  ) {
  }

  fun sendApkfyRobloxExp81InstallClickEvent(numberOfCheckPresses: Int) {}
}
