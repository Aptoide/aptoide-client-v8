package cm.aptoide.pt.app.mmpcampaigns

data class Campaign(
  val impression: List<CampaignUrl>?,
  val click: List<CampaignUrl>?,
  val download: List<CampaignUrl>?
)

data class CampaignUrl(
  val name: String,
  val url: String
)
