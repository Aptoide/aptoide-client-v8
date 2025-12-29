package com.aptoide.android.aptoidegames.apkfy.analytics

import android.content.Context
import cm.aptoide.pt.feature_apkfy.domain.ApkfyModel
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.UserProperty
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ApkfyAnalytics @Inject constructor(
  private val genericAnalytics: GenericAnalytics,
  private val biAnalytics: BIAnalytics,
  @ApplicationContext private val context: Context,
) {

  fun setGuestUIDUserProperty(guestUid: String) =
    biAnalytics.setUserProperties(UserProperty("aptoide_mmp_guest_id", guestUid))

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

  fun sendRobloxExp81ApkfyShown() = genericAnalytics.logEvent("exp81_roblox_apkfy_shown", params = emptyMap())

  fun sendApkfyTimeout() = genericAnalytics.logEvent("apkfy_timeout", params = emptyMap())

  fun sendApkfyScreenBackClicked() =
    genericAnalytics.logEvent("apkfy_screen_back_clicked", params = emptyMap())

  fun setApkfyUTMProperties(apkfyModel: ApkfyModel) {
    apkfyModel.run {
      if (hasUTMs()) {
        biAnalytics.setUTMProperties(
          utmSource = utmSource,
          utmMedium = utmMedium,
          utmCampaign = utmCampaign,
          utmTerm = utmTerm,
          utmContent = utmContent,
          utmOemId = oemId,
          utmPackageName = packageName ?: APKFY_BUT_NO_APP
        )
      } else if (hasApkfy()) {
        if (packageName == context.packageName) {
          biAnalytics.setUTMProperties(
            utmSource = UTM_PROPERTY_DIRECT_WITHOUT_UTMS,
            utmMedium = UTM_PROPERTY_DIRECT_WITHOUT_UTMS,
            utmCampaign = UTM_PROPERTY_DIRECT_WITHOUT_UTMS,
            utmTerm = UTM_PROPERTY_DIRECT_WITHOUT_UTMS,
            utmContent = UTM_PROPERTY_DIRECT_WITHOUT_UTMS,
            utmOemId = oemId ?: UTM_PROPERTY_DIRECT_WITHOUT_UTMS,
            utmPackageName = packageName
          )
        } else {
          biAnalytics.setUTMProperties(
            utmSource = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
            utmMedium = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
            utmCampaign = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
            utmTerm = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
            utmContent = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
            utmOemId = oemId ?: UTM_PROPERTY_APKFY_WITHOUT_UTMS,
            utmPackageName = packageName
          )
        }
      } else {
        biAnalytics.setUTMProperties(
          utmSource = UTM_PROPERTY_NO_APKFY,
          utmMedium = UTM_PROPERTY_NO_APKFY,
          utmCampaign = UTM_PROPERTY_NO_APKFY,
          utmTerm = UTM_PROPERTY_NO_APKFY,
          utmContent = UTM_PROPERTY_NO_APKFY,
          utmOemId = UTM_PROPERTY_NO_APKFY,
          utmPackageName = UTM_PROPERTY_NO_APKFY
        )
      }
    }
  }

  companion object {
    private const val UTM_PROPERTY_NO_APKFY = "NO_APKFY"
    private const val UTM_PROPERTY_APKFY_WITHOUT_UTMS = "APKFY_BUT_NO_UTM"
    private const val APKFY_BUT_NO_APP = "APKFY_BUT_NO_APP"
    private const val UTM_PROPERTY_DIRECT_WITHOUT_UTMS = "DIRECT_BUT_NO_UTM"

    private const val P_STATUS = "status"
    private const val P_DATA = "data"
    private const val P_ERROR_MESSAGE = "error_message"
    private const val P_ERROR_TYPE = "error_type"
    private const val P_ERROR_HTTP_CODE = "error_http_code"
    private const val P_RETRY = "retry"
    private const val P_CALL_NUMBER = "call_number"
  }
}
