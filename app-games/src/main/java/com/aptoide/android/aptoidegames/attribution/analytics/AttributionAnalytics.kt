package com.aptoide.android.aptoidegames.attribution.analytics

import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.UserProperty
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import com.aptoide.android.aptoidegames.attribution.domain.AttributionModel
import javax.inject.Inject

class AttributionAnalytics @Inject constructor(
  private val biAnalytics: BIAnalytics,
) {

  fun setGuestUIDUserProperty(guestUid: String) =
    biAnalytics.setUserProperties(UserProperty("aptoide_mmp_guest_id", guestUid))

  fun setAttributionUTMProperties(model: AttributionModel) {
    model.run {
      if (hasUTMs()) {
        biAnalytics.setUTMProperties(
          utmSource = utmSource,
          utmMedium = utmMedium,
          utmCampaign = utmCampaign,
          utmTerm = utmTerm,
          utmContent = utmContent,
          utmOemId = oemId,
          utmPackageName = packageName ?: NO_APKFY,
        )
      } else {
        biAnalytics.setUTMProperties(
          utmSource = NO_APKFY,
          utmMedium = NO_APKFY,
          utmCampaign = NO_APKFY,
          utmTerm = NO_APKFY,
          utmContent = NO_APKFY,
          utmOemId = oemId ?: NO_APKFY,
          utmPackageName = packageName ?: NO_APKFY,
        )
      }
    }
  }

  fun sendAttributionSuccessEvent(
    data: String,
    isRetry: Boolean,
    callNumber: Int,
  ) = biAnalytics.logEvent(
    name = EVENT_NAME,
    mapOfNonNull(
      P_STATUS to "success",
      P_DATA to data,
      P_RETRY to isRetry,
      P_CALL_NUMBER to callNumber,
    )
  )

  fun sendAttributionFailEvent(
    errorMessage: String?,
    errorType: String?,
    errorCode: Int? = null,
    isRetry: Boolean,
    callNumber: Int,
  ) = biAnalytics.logEvent(
    name = EVENT_NAME,
    mapOfNonNull(
      P_STATUS to "fail",
      P_ERROR_MESSAGE to errorMessage,
      P_ERROR_TYPE to errorType,
      P_ERROR_HTTP_CODE to errorCode,
      P_RETRY to isRetry,
      P_CALL_NUMBER to callNumber,
    )
  )

  companion object {
    private const val NO_APKFY = "NO_APKFY"
    private const val EVENT_NAME = "aptoide_mmp_attribution"

    private const val P_STATUS = "status"
    private const val P_DATA = "data"
    private const val P_ERROR_MESSAGE = "error_message"
    private const val P_ERROR_TYPE = "error_type"
    private const val P_ERROR_HTTP_CODE = "error_http_code"
    private const val P_RETRY = "retry"
    private const val P_CALL_NUMBER = "call_number"
  }
}
