package cm.aptoide.pt.feature_mmp.apkfy.domain

data class ApkfyModel(
  val packageName: String?,
  val appId: Long?,
  val oemId: String?,
  val guestUid: String,
  val utmSource: String?,
  val utmMedium: String?,
  val utmCampaign: String?,
  val utmTerm: String?,
  val utmContent: String?,
)
