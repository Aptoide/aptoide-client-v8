package com.aptoide.android.aptoidegames.apkfy.analytics

import cm.aptoide.pt.device_api.error.DeviceApiException
import cm.aptoide.pt.feature_apkfy.domain.ApkfyResolution
import cm.aptoide.pt.feature_apkfy.domain.ApkfyResolutionReporter
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.UserProperty
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import com.aptoide.android.aptoidegames.installer.analytics.catalogSource
import retrofit2.HttpException
import javax.inject.Inject

class ApkfyAnalytics @Inject constructor(
  private val genericAnalytics: GenericAnalytics,
  private val biAnalytics: BIAnalytics,
) : ApkfyResolutionReporter {

  // In Firebase, so the cohort titles that cannot be installed yet - or at all - show up
  override fun report(packageName: String?, resolution: ApkfyResolution) =
    genericAnalytics.logEvent(
      name = "apkfy_resolution",
      params = when (resolution) {
        is ApkfyResolution.Resolved -> mapOfNonNull(
          P_PACKAGE_NAME to resolution.app.packageName,
          P_SOURCE to resolution.app.origin.catalogSource,
          P_STATUS to if (resolution.unavailableReason == null) "found" else "not_installable",
          P_REASON to resolution.unavailableReason?.analyticsValue,
        )

        is ApkfyResolution.NotFound -> mapOfNonNull(
          P_PACKAGE_NAME to packageName,
          P_STATUS to "not_found",
          P_REASON to "not_found",
        )

        is ApkfyResolution.Failed -> mapOfNonNull(
          P_PACKAGE_NAME to packageName,
          P_SOURCE to resolution.source.catalogSource,
          P_STATUS to "failed",
          P_ERROR_TYPE to resolution.error::class.simpleName,
          P_ERROR_HTTP_CODE to resolution.error.httpCode,
        )
      }
    )

  fun sendApkfySuccessEvent(
    data: String,
    isRetry: Boolean,
    callNumber: Int,
  ) {
    biAnalytics.logEvent(
      name = "ag_aptoide_mmp",
      mapOfNonNull(
        P_STATUS to "success",
        P_DATA to data,
        P_RETRY to isRetry,
        P_CALL_NUMBER to callNumber
      )
    )
  }

  fun sendApkfyFailEvent(
    errorMessage: String?,
    errorType: String?,
    errorCode: Int? = null,
    isRetry: Boolean,
    callNumber: Int,
  ) = biAnalytics.logEvent(
    name = "ag_aptoide_mmp",
    mapOfNonNull(
      P_STATUS to "fail",
      P_ERROR_MESSAGE to errorMessage,
      P_ERROR_TYPE to errorType,
      P_ERROR_HTTP_CODE to errorCode,
      P_RETRY to isRetry,
      P_CALL_NUMBER to callNumber
    )
  )

  fun sendApkfyShown() = genericAnalytics.logEvent("apkfy_shown", params = emptyMap())

  fun sendRobloxApkfyShown() = genericAnalytics.logEvent("roblox_apkfy_shown", params = emptyMap())

  fun sendRobloxExp83ApkfyShown() =
    genericAnalytics.logEvent("exp83_roblox_apkfy_shown", params = emptyMap())

  fun setExp83GroupUserProperty(variant: String) =
    biAnalytics.setUserProperties(UserProperty("exp83_group", variant))

  fun sendApkfyTimeout() = genericAnalytics.logEvent("apkfy_timeout", params = emptyMap())

  fun sendApkfyScreenBackClicked() =
    genericAnalytics.logEvent("apkfy_screen_back_clicked", params = emptyMap())

  fun sendExp83RecommendationInstallClick() =
    genericAnalytics.logEvent("exp83_recommendation_install_click", params = emptyMap())

  fun sendExp83OpenRobloxClick() =
    genericAnalytics.logEvent("exp83_open_roblox_click", params = emptyMap())

  private val Throwable.httpCode: Int?
    get() = when (this) {
      is HttpException -> code()
      is DeviceApiException -> statusCode.takeIf { it != DeviceApiException.NO_STATUS }
      else -> null
    }

  companion object {
    private const val P_PACKAGE_NAME = "package_name"
    private const val P_SOURCE = "source"
    private const val P_REASON = "reason"
    private const val P_STATUS = "status"
    private const val P_DATA = "data"
    private const val P_ERROR_MESSAGE = "error_message"
    private const val P_ERROR_TYPE = "error_type"
    private const val P_ERROR_HTTP_CODE = "error_http_code"
    private const val P_RETRY = "retry"
    private const val P_CALL_NUMBER = "call_number"
  }
}
