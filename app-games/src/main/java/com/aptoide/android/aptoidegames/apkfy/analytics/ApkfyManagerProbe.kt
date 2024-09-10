package com.aptoide.android.aptoidegames.apkfy.analytics

import cm.aptoide.pt.feature_mmp.apkfy.domain.ApkfyManager
import cm.aptoide.pt.feature_mmp.apkfy.domain.ApkfyModel
import com.aptoide.android.aptoidegames.analytics.BIAnalytics

class ApkfyManagerProbe(
  private val apkfyManager: ApkfyManager,
  private val biAnalytics: BIAnalytics,
) : ApkfyManager {

  companion object {
    private const val UTM_PROPERTY_NO_APKFY = "NO_APKFY"
    private const val UTM_PROPERTY_APKFY_WITHOUT_UTMS = "APKFY_BUT_NO_UTM"
  }

  override suspend fun getApkfy() = apkfyManager.getApkfy()?.also(::setApkfyUTMProperties)

  private fun setApkfyUTMProperties(apkfyModel: ApkfyModel) {
    apkfyModel.run {
      if (hasUTMs()) {
        biAnalytics.setUTMProperties(
          utmSource = utmSource,
          utmMedium = utmMedium,
          utmCampaign = utmCampaign,
          utmTerm = utmTerm,
          utmContent = utmContent
        )
      } else if (packageName == null && oemId == null) {
        //Safe to assume there are no utms, so no need to check
        biAnalytics.setUTMProperties(
          utmSource = UTM_PROPERTY_NO_APKFY,
          utmMedium = UTM_PROPERTY_NO_APKFY,
          utmCampaign = UTM_PROPERTY_NO_APKFY,
          utmTerm = UTM_PROPERTY_NO_APKFY,
          utmContent = UTM_PROPERTY_NO_APKFY
        )
      } else {
        biAnalytics.setUTMProperties(
          utmSource = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmMedium = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmCampaign = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmTerm = UTM_PROPERTY_APKFY_WITHOUT_UTMS,
          utmContent = UTM_PROPERTY_APKFY_WITHOUT_UTMS
        )
      }
    }
  }
}
