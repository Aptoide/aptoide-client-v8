package cm.aptoide.pt.feature_apkfy.domain

import cm.aptoide.pt.feature_apps.domain.AppSource

data class ApkfyModel(
  override val packageName: String?,
  override val appId: Long?,
  val oemId: String?,
  val guestUid: String,
  val utmSource: String?,
  val utmMedium: String?,
  val utmCampaign: String?,
  val utmTerm: String?,
  val utmContent: String?,
) : AppSource {
  fun hasUTMs() = this.run {
    utmSource != null || utmMedium != null || utmCampaign != null || utmTerm != null || utmContent != null
  }
}
