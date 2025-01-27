package com.aptoide.android.aptoidegames.apkfy.analytics

import cm.aptoide.pt.feature_apkfy.domain.ApkfyModel
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import jakarta.inject.Inject

class ApkfyAnalytics @Inject constructor(
  private val biAnalytics: BIAnalytics,
) {

  fun setGuestUIDUserProperty(guestUid: String) = biAnalytics.setUserProperties(
    "aptoide_mmp_guest_id" to guestUid
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

  fun setApkfyUTMProperties(apkfyModel: ApkfyModel) {
    apkfyModel.run {
      if (hasUTMs()) {
        biAnalytics.setUTMProperties(
          utmSource = utmSource,
          utmMedium = utmMedium,
          utmCampaign = utmCampaign,
          utmTerm = utmTerm,
          utmContent = utmContent,
          utmPackageName = packageName
        )
      } else if (hasApkfy()) {
        biAnalytics.setUTMProperties(
          utmSource = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmMedium = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmCampaign = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmTerm = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmContent = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmPackageName = packageName
        )
      } else {
        biAnalytics.setUTMProperties(
          utmSource = UTM_PROPERTY_NO_APKFY,
          utmMedium = UTM_PROPERTY_NO_APKFY,
          utmCampaign = UTM_PROPERTY_NO_APKFY,
          utmTerm = UTM_PROPERTY_NO_APKFY,
          utmContent = UTM_PROPERTY_NO_APKFY,
          utmPackageName = UTM_PROPERTY_NO_APKFY
        )
      }
    }
  }

  companion object {
    private const val UTM_PROPERTY_NO_APKFY = "NO_APKFY"
    private const val UTM_PROPERTY_APKFY_WITHOUT_UTMS = "APKFY_BUT_NO_UTM"

    private const val P_STATUS = "status"
    private const val P_DATA = "data"
    private const val P_ERROR_MESSAGE = "error_message"
    private const val P_ERROR_TYPE = "error_type"
    private const val P_ERROR_HTTP_CODE = "error_http_code"
    private const val P_RETRY = "retry"
    private const val P_CALL_NUMBER = "call_number"
  }
}
