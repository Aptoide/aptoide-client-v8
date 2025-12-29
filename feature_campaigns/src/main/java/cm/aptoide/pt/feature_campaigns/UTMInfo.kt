package cm.aptoide.pt.feature_campaigns

data class UTMInfo(
  val utmSource: String = "aptoide",
  val utmMedium: String,
  val utmCampaign: String,
  val utmContent: String,
  val utmTerm: String? = null
)
