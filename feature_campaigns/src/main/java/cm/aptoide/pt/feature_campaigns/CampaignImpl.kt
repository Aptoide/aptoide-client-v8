package cm.aptoide.pt.feature_campaigns

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

interface Campaign {
  suspend fun sendClickEvent()
  suspend fun sendImpressionEvent()
}

data class CampaignImpl constructor(
  private val impressions: List<String>,
  private val clicks: List<String>,
  private val repository: CampaignRepository,
  private val normalize: suspend (String, String) -> String
) : Campaign {

  private val adListId by lazy {
    UUID.randomUUID().toString()
  }

  override suspend fun sendImpressionEvent() = withContext(Dispatchers.IO) {
    impressions.forEach { repository.knock(normalize(it, adListId)) }
  }

  override suspend fun sendClickEvent() = withContext(Dispatchers.IO) {
    clicks.forEach { repository.knock(normalize(it, adListId)) }
  }
}

interface CampaignRepository {
  suspend fun knock(url: String)
}
