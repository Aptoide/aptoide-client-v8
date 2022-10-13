package cm.aptoide.pt.feature_campaigns.data

data class CampaignJson(val campaign: CampaignDataJson, val urls: CampaignUrlsJson)

data class CampaignDataJson(val id: Long, val name: String, val label: String)

data class CampaignUrlsJson(
  val impressions: List<String>,
  val clicks: List<String>,
  val downloads: List<String>,
  val installs: List<String>,
)
