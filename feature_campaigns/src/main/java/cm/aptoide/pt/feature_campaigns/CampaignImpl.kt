package cm.aptoide.pt.feature_campaigns

interface Campaign {
  suspend fun sendClickEvent()
  suspend fun sendImpressionEvent()
}

data class CampaignImpl constructor(
  private val impressions: List<String>,
  private val clicks: List<String>,
  private val repository: CampaignRepository,
) : Campaign {

  override suspend fun sendImpressionEvent() = impressions.forEach { repository.knock(it) }
  override suspend fun sendClickEvent() = clicks.forEach { repository.knock(it) }
}

interface CampaignRepository {
  suspend fun knock(url: String)
}
