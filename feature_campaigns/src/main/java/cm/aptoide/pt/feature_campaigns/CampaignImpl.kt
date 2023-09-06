package cm.aptoide.pt.feature_campaigns

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface Campaign {
  suspend fun sendClickEvent()
  suspend fun sendImpressionEvent()

  fun extractCampaignId(): String?
}

data class CampaignImpl constructor(
  private val impressions: List<String>,
  private val clicks: List<String>,
  private val repository: CampaignRepository,
  private val normalize: suspend (String, String) -> String
) : Campaign {
  var adListId: String? = null

  override suspend fun sendImpressionEvent() = withContext(Dispatchers.IO) {
    val adListId = adListId ?: return@withContext
    impressions.forEach { repository.knock(normalize(it, adListId)) }
  }

  override suspend fun sendClickEvent() = withContext(Dispatchers.IO) {
    val adListId = adListId ?: return@withContext
    clicks.forEach { repository.knock(normalize(it, adListId)) }
  }

  override fun extractCampaignId(): String? =
    getCampaignId(impressions) ?: getCampaignId(clicks)

  private fun getCampaignId(urlList: List<String>): String? {
    urlList.forEach {
      val url = Uri.parse(it).buildUpon().build()
      val campaignId = url.getQueryParameter("campaignId")

      if (campaignId != null) return campaignId
    }

    return null
  }
}

interface CampaignRepository {
  suspend fun knock(url: String)
}
