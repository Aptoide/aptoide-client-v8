package cm.aptoide.pt.feature_campaigns.data

import androidx.annotation.Keep

@Keep
data class CampaignJson(val campaign: CampaignDataJson, val urls: CampaignUrlsJson)

@Keep
data class CampaignDataJson(val id: Long, val name: String, val label: String)

@Keep
data class CampaignUrlsJson(
  val impressions: List<String>,
  val clicks: List<String>,
  val downloads: List<String>,
  val installs: List<String>,
)
