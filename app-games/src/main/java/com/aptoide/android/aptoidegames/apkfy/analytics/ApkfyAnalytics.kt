package com.aptoide.android.aptoidegames.apkfy.analytics

import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.UserProperty
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import javax.inject.Inject

class ApkfyAnalytics @Inject constructor(
  private val genericAnalytics: GenericAnalytics,
  private val biAnalytics: BIAnalytics,
) {

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

  companion object {
    private const val P_STATUS = "status"
    private const val P_DATA = "data"
    private const val P_ERROR_MESSAGE = "error_message"
    private const val P_ERROR_TYPE = "error_type"
    private const val P_ERROR_HTTP_CODE = "error_http_code"
    private const val P_RETRY = "retry"
    private const val P_CALL_NUMBER = "call_number"
  }
}
