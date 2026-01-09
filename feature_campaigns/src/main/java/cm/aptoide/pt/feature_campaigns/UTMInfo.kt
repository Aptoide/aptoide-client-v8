package cm.aptoide.pt.feature_campaigns

data class UTMInfo(
  val utmSource: String? = "aptoide",
  val utmMedium: String? = null,
  val utmCampaign: String? = null,
  val utmContent: String? = null,
  val utmTerm: String? = null
)
