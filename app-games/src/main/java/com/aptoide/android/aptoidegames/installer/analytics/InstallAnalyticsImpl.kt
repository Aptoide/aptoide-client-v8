package com.aptoide.android.aptoidegames.installer.analytics

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.net.toUri
import cm.aptoide.pt.extensions.isActiveNetworkMetered
import cm.aptoide.pt.extensions.toInt
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.UserProperty
import com.aptoide.android.aptoidegames.analytics.asNullableParameter
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.dto.InstallAction
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import com.aptoide.android.aptoidegames.analytics.toBIParameters
import com.aptoide.android.aptoidegames.analytics.toBiParameters
import com.aptoide.android.aptoidegames.analytics.toGenericParameters
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalyticsImpl.Companion.P_APP_SIZE_MB
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalyticsImpl.Companion.P_UPDATE_TYPE
import com.aptoide.android.aptoidegames.installer.ff.getDownloaderVariant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Inet4Address
import java.net.Inet6Address

class InstallAnalyticsImpl(
  private val context: Context,
  private val featureFlags: FeatureFlags,
  private val genericAnalytics: GenericAnalytics,
  private val biAnalytics: BIAnalytics,
  private val storeName: String,
  private val silentInstallChecker: SilentInstallChecker,
) : InstallAnalytics {

  init {
    CoroutineScope(Dispatchers.Main).launch {
      biAnalytics.setUserProperties(
        UserProperty(
          "downloader_variant",
          featureFlags.getDownloaderVariant()
        )
      )
    }
  }

  override fun setUsedInstallerProperty(installerUsed: String) {
    biAnalytics.setUserProperties(UserProperty("installer", installerUsed))
  }

  override fun sendClickEvent(
    app: App,
    analyticsContext: AnalyticsUIContext,
    networkType: String,
  ) {
    when (analyticsContext.installAction) {
      InstallAction.INSTALL -> "install_clicked"
      InstallAction.UPDATE -> "update_clicked"
      InstallAction.MIGRATE -> "migrate_clicked"
      InstallAction.RETRY -> "retry_app_clicked"
      InstallAction.UNINSTALL -> "oos_uninstall_clicked"
      else -> null
    }?.also { name ->
      genericAnalytics.logEvent(
        name = name,
        params = analyticsContext.toGenericParameters(
          *app.toGenericParameters(),
          P_UPDATE_TYPE to getUserClicks(app.packageName),
          P_SERVICE to networkType
        )
      )
    }

    when (analyticsContext.installAction) {
      InstallAction.INSTALL,
      InstallAction.UPDATE,
      InstallAction.MIGRATE,
      InstallAction.RETRY -> sendBIClickEvent(app = app, analyticsContext = analyticsContext)

      else -> Unit
    }

    if (analyticsContext.isApkfy) {
      genericAnalytics.logEvent(
        name = "install_clicked_apkfy",
        params = analyticsContext.toGenericParameters(
          *app.toGenericParameters(),
          P_UPDATE_TYPE to getUserClicks(app.packageName),
          P_SERVICE to networkType
        )
      )
    }
  }

  override fun sendOpenClick(
    packageName: String,
    hasAPPCBilling: Boolean?,
    analyticsContext: AnalyticsUIContext,
  ) {
    genericAnalytics.logEvent(
      name = "open_clicked",
      params = analyticsContext.toGenericParameters(
        GenericAnalytics.P_PACKAGE_NAME to packageName,
        GenericAnalytics.P_APPC_BILLING to hasAPPCBilling,
        P_UPDATE_TYPE to getUserClicks(packageName)
      )
    )
  }

  override fun sendOnInstallationQueued(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
    genericAnalytics.logEvent(
      name = "download_queue_entry",
      params = installPackageInfo.toAppGenericParameters(packageName = packageName)
    )
  }

  override fun sendOnInstallationRemovedFromQueue(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
    genericAnalytics.logEvent(
      name = "download_queue_exit",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
      )
    )
  }

  override fun sendDownloadStartedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
    genericAnalytics.logEvent(
      name = "app_download",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "started"
      )
    )
  }

  override fun sendDownloadCompletedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    downloadedBytesPerSecond: Double,
  ) {
    genericAnalytics.logEvent(
      name = "app_download",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "success"
      )
    )

    logBIDownloadEvent(
      packageName = packageName,
      status = "success",
      installPackageInfo = installPackageInfo,
      downloadedBytesPerSecond = downloadedBytesPerSecond
    )
  }

  override fun sendDownloadCachedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ) {
    genericAnalytics.logEvent(
      name = "app_download",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "cached"
      )
    )

    logBIDownloadEvent(
      packageName = packageName,
      status = "cached",
      installPackageInfo = installPackageInfo
    )
  }

  override fun sendDownloadErrorEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    downloadedBytesPerSecond: Double,
    errorMessage: String?,
    errorType: String?,
    errorCode: Int?,
    errorUrl: String?,
  ) {
    genericAnalytics.logEvent(
      name = "app_download",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "fail",
        P_ERROR_MESSAGE to (errorMessage ?: "failure")
      )
    )

    logBIDownloadEvent(
      packageName = packageName,
      status = "fail",
      installPackageInfo = installPackageInfo,
      downloadedBytesPerSecond = downloadedBytesPerSecond,
      P_ERROR_MESSAGE to errorMessage,
      P_ERROR_TYPE to errorType,
      P_ERROR_HTTP_CODE to errorCode,
      P_ERROR_URL to errorUrl
    )
  }

  override fun sendDownloadAbortEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    errorMessage: String?,
  ) {
    genericAnalytics.logEvent(
      name = "app_download",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "fail",
        P_ERROR_MESSAGE to (errorMessage ?: "failure")
      )
    )

    logBIDownloadEvent(
      packageName = packageName,
      status = "abort",
      installPackageInfo = installPackageInfo,
      downloadedBytesPerSecond = -1.0,
      P_ERROR_MESSAGE to errorMessage,
      P_ERROR_TYPE to "permission",
    )
  }

  override fun sendDownloadCanceledEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    downloadedBytesPerSecond: Double,
  ) {
    genericAnalytics.logEvent(
      name = "app_download",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "cancel"
      )
    )

    logBIDownloadEvent(
      packageName = packageName,
      status = "cancel",
      installPackageInfo = installPackageInfo,
      downloadedBytesPerSecond = downloadedBytesPerSecond,
      P_ERROR_TYPE to "downloading"
    )
  }

  override fun sendDownloadCanceledInQueueEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
    logBIDownloadEvent(
      packageName = packageName,
      status = "cancel",
      installPackageInfo = installPackageInfo,
      downloadedBytesPerSecond = -1.0,
      P_ERROR_TYPE to "queue"
    )
  }

  override fun sendAutomaticQueueDownloadCancelEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
    logBIDownloadEvent(
      packageName = packageName,
      status = "cancel_queue",
      installPackageInfo = installPackageInfo,
      downloadedBytesPerSecond = -1.0,
      P_ERROR_TYPE to "queue"
    )
  }

  override fun sendInstallDialogImpressionEvent(
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

  override fun sendInstallStartedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
    genericAnalytics.logEvent(
      name = "app_installed",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "started"
      )
    )
  }

  override fun sendInstallCancelEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
    genericAnalytics.logEvent(
      name = "app_installed",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "cancel"
      )
    )
  }

  override fun sendInstallCompletedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ) {
    genericAnalytics.logEvent(
      name = "app_installed",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "success"
      )
    )

    logBIInstallEvent(
      packageName = packageName,
      status = "success",
      installPackageInfo = installPackageInfo
    )

    if (installPackageInfo.payload.toAnalyticsPayload()?.isApkfy == true) {
      genericAnalytics.logEvent(
        name = "apkfy_install_success",
        params = installPackageInfo.toAppGenericParameters(
          packageName = packageName,
        )
      )
    }
  }

  override fun sendInstallErrorEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    errorMessage: String?,
    errorType: String?,
  ) {
    genericAnalytics.logEvent(
      name = "app_installed",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "fail",
        P_ERROR_MESSAGE to (errorMessage ?: "failure")
      )
    )

    logBIInstallEvent(
      packageName = packageName,
      status = "fail",
      installPackageInfo = installPackageInfo,
      P_ERROR_MESSAGE to errorMessage,
      P_ERROR_TYPE to errorType,
    )
  }

  override fun sendInstallAbortEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
    errorMessage: String?,
  ) {
    genericAnalytics.logEvent(
      name = "app_installed",
      params = installPackageInfo.toAppGenericParameters(
        packageName = packageName,
        P_STATUS to "fail",
        P_ERROR_MESSAGE to (errorMessage ?: "failure")
      )
    )

    logBIInstallEvent(
      packageName = packageName,
      status = "abort",
      installPackageInfo = installPackageInfo,
      P_ERROR_MESSAGE to errorMessage,
      P_ERROR_TYPE to "permission",
    )
  }

  override fun sendResumeDownloadClick(
    app: App,
    downloadOnlyOverWifiSetting: Boolean,
  ) {
    genericAnalytics.logEvent(
      name = "resume_download_clicked",
      params = mapOf(
        *app.toGenericParameters(),
        P_UPDATE_TYPE to getUserClicks(app.packageName),
        P_WIFI_SETTING to downloadOnlyOverWifiSetting
      )
    )
  }

  override fun sendDownloadRestartedEvent(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ) = genericAnalytics.logEvent(
    name = "app_download",
    params = installPackageInfo.toAppGenericParameters(
      packageName = packageName,
      P_STATUS to "restart"
    )
  )

  override fun sendDownloadCancel(
    app: App,
    analyticsContext: AnalyticsUIContext,
  ) {
    genericAnalytics.logEvent(
      name = "download_canceled",
      params = analyticsContext.toGenericParameters(
        *app.toGenericParameters(),
        P_UPDATE_TYPE to getUserClicks(app.packageName)
      )
    )
  }

  override fun sendWifiPromptShown(
    app: App,
    downloadOnlyOverWifiSetting: Boolean,
  ) = genericAnalytics.logEvent(
    name = "wifi_prompt_shown",
    params = mapOf(
      *app.toGenericParameters(),
      P_WIFI_SETTING to downloadOnlyOverWifiSetting,
    )
  )

  override fun sendWaitForWifiClicked(
    app: App,
    downloadOnlyOverWifi: Boolean,
  ) = genericAnalytics.logEvent(
    name = "wait_for_wifi_clicked",
    params = mapOf(
      GenericAnalytics.P_PACKAGE_NAME to app.packageName,
      GenericAnalytics.P_APP_SIZE to app.appSize,
      P_UPDATE_TYPE to getUserClicks(app.packageName),
      P_WIFI_SETTING to downloadOnlyOverWifi
    )
  )

  override fun sendDownloadNowClicked(
    packageName: String,
    appSize: Long,
    promptType: String,
    downloadOnlyOverWifi: Boolean,
  ) = genericAnalytics.logEvent(
    name = "download_now_clicked",
    params = mapOf(
      GenericAnalytics.P_PACKAGE_NAME to packageName,
      GenericAnalytics.P_APP_SIZE to appSize,
      P_UPDATE_TYPE to getUserClicks(packageName),
      P_WIFI_SETTING to downloadOnlyOverWifi,
      P_PROMPT_TYPE to promptType
    )
  )

  override fun sendApkfyRobloxExp82InstallClickEvent(
    numberOfCheckPresses: Int,
    autoOpenDefault: Boolean,
    autoOpenFinal: Boolean,
    switchCheckDiff: Int
  ) {
    genericAnalytics.logEvent(
      name = "exp82_apkfy_install_click",
      params = mapOf(
        "check_diff" to numberOfCheckPresses,
        "auto_open_default" to autoOpenDefault,
        "auto_open_final" to autoOpenFinal,
        "switch_check_diff" to switchCheckDiff
      )
    )
  }

  /** Reused functions **/

  private fun sendBIClickEvent(
    app: App,
    analyticsContext: AnalyticsUIContext,
  ) = biAnalytics.logEvent(
    name = "click_on_install_button",
    app.toBIParameters(aabTypes = null) +
      analyticsContext.toBiParameters(
        P_STORE to storeName,
        P_TRUSTED_BADGE to app.malware.asNullableParameter(),
        P_UPDATE_TYPE to getUserClicks(app.packageName)
      )
  )

  private fun logBIDownloadEvent(
    packageName: String,
    status: String,
    installPackageInfo: InstallPackageInfo,
    downloadedBytesPerSecond: Double = -1.0,
    vararg pairs: Pair<String, Any?>
  ) = biAnalytics.logEvent(
    name = "download",
    installPackageInfo.payload
      .toAnalyticsPayload()
      .let {
        it.toAppBIParameters(packageName) +
          it?.toAnalyticsUiContext().toBiParameters(
            P_STATUS to status,
            installPackageInfo.getAppSizeSegment(),
            P_STORE to it?.store,
            P_TRUSTED_BADGE to it?.trustedBadge,
            P_DOWNLOAD_SPEED_MB to getDownloadSpeedInterval(downloadedBytesPerSecond),
            P_NETWORK_IP_VERSION to context.ipVersion,
            P_NETWORK_TYPE to if (context.isActiveNetworkMetered) "metered" else "unmetered",
            P_URL_DOMAIN to installPackageInfo.domainPrefix,
            *pairs
          )
      }
  )

  private fun logBIInstallEvent(
    packageName: String,
    status: String,
    installPackageInfo: InstallPackageInfo,
    vararg pairs: Pair<String, Any?>
  ) = biAnalytics.logEvent(
    name = "install",
    installPackageInfo.payload
      .toAnalyticsPayload()
      .let {
        it.toAppBIParameters(packageName) +
          it?.toAnalyticsUiContext().toBiParameters(
            P_STATUS to status,
            installPackageInfo.getAppSizeSegment(),
            P_STORE to it?.store,
            P_TRUSTED_BADGE to it?.trustedBadge,
            *pairs
          )
      }
  )

  private fun getUserClicks(packageName: String): String = silentInstallChecker
    .canInstallSilently(packageName)
    .not()
    .toInt()
    .plus(1)
    .toTapsValue()

  companion object {
    private const val P_STORE = "store"
    private const val P_STATUS = "status"
    private const val P_TRUSTED_BADGE = "trusted_badge"
    private const val P_ERROR_MESSAGE = "error_message"
    private const val P_ERROR_TYPE = "error_type"
    private const val P_ERROR_HTTP_CODE = "error_http_code"
    private const val P_ERROR_URL = "error_url"
    private const val P_NETWORK_IP_VERSION = "network_ip_version"
    private const val P_NETWORK_TYPE = "network_type"
    private const val P_URL_DOMAIN = "url_domain"
    internal const val P_WIFI_SETTING = "wifi_setting"
    internal const val P_PROMPT_TYPE = "prompt_type"
    internal const val P_SERVICE = "service"
    internal const val P_UPDATE_TYPE = "update_type"
    internal const val P_APP_SIZE_MB = "app_size_mb"
    internal const val P_DOWNLOAD_SPEED_MB = "download_speed_mbps"
  }
}

private fun InstallPackageInfo.toAppGenericParameters(
  packageName: String,
  vararg pairs: Pair<String, Any?>
): Map<String, Any> = payload
  .toAnalyticsPayload()
  .toAppGenericParameters(packageName, getAppSizeSegment(), *pairs)

private fun InstallPackageInfo.getAppSizeSegment(): Pair<String, Int> = P_APP_SIZE_MB.to(
  filesSize.toInt()
    .takeIf { it > 0 }
    ?.div(100_000_000)
    ?.plus(1)
    ?.times(100)
    ?: 0
)

private fun AnalyticsPayload?.toAppGenericParameters(
  packageName: String,
  vararg pairs: Pair<String, Any?>
): Map<String, Any> =
  this?.run {
    toAnalyticsUiContext()
      .toGenericParameters(
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

fun getDownloadSpeedInterval(bytesPerSecond: Double): String {
  if (bytesPerSecond <= 0.0) return "n-a"

  var speed = bytesPerSecond
  var scale = "BPS"

  if (speed >= 1024) {
    speed /= 1024
    scale = "KBPS"
  }
  if (speed >= 1024) {
    speed /= 1024
    scale = "MBPS"
  }
  if (speed >= 1024) {
    speed /= 1024
    scale = "GBPS"
  }

  val interval = when {
    speed == 0.0 -> "0"
    speed <= 2 -> "1-2"
    speed <= 5 -> "3-5"
    speed <= 10 -> "6-10"
    speed <= 20 -> "11-20"
    speed <= 50 -> "21-50"
    speed <= 100 -> "51-100"
    speed <= 200 -> "101-200"
    speed <= 500 -> "201-500"
    speed <= 1000 -> "501-1000"
    else -> ">1000"
  }

  return "$interval $scale"
}

val Context.ipVersion
  get() = (getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
    .run { this?.getLinkProperties(activeNetwork)?.linkAddresses ?: emptyList() }
    .mapNotNull {
      when (it.address) {
        is Inet4Address -> "ipv4"
        is Inet6Address -> "ipv6"
        else -> null // Normally will never end up here. But if it will we filter it out.
      }
    }
    .toSet() // Remove duplicates
    .sorted() // Make v 4 be first
    .joinToString("+") // Results in `ipv4+ipv6` in case both networks are available

private val InstallPackageInfo.domainPrefix: String?
  get() = installationFiles.firstOrNull()
    ?.url
    ?.toUri()
    ?.host
    ?.replace("(\\.apk)?\\.aptoide\\.com".toRegex(), "")
    ?.ifEmpty { null }
    ?: "n-a"
