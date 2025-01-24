package com.aptoide.android.aptoidegames.apkfy.analytics

import cm.aptoide.pt.feature_apkfy.domain.ApkfyModel
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import jakarta.inject.Inject

class ApkfyAnalytics @Inject constructor(
  private val biAnalytics: BIAnalytics,
) {

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
  }
}
