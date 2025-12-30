package cm.aptoide.pt.feature_campaigns

import android.net.Uri
import androidx.annotation.Keep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface Campaign {
  val adListId: String?
  val campaignId: String?

  var deepLinkUtms: Map<String, String>

  suspend fun sendImpressionEvent(
    type: String,
    toReplace: Map<String, String> = emptyMap(),
    toAppend: Map<String, String> = emptyMap(),
  )

  suspend fun sendClickEvent(
    type: String,
    toReplace: Map<String, String> = emptyMap(),
    toAppend: Map<String, String> = emptyMap(),
  )

  suspend fun sendDownloadEvent(
    type: String,
    toReplace: Map<String, String> = emptyMap(),
    toAppend: Map<String, String> = emptyMap(),
  )

  fun extractParameter(
    parameter: String,
    type: String,
  ): String?
}

data class CampaignImpl constructor(
  private val impressions: List<CampaignTuple>,
  private val clicks: List<CampaignTuple>,
  val downloads: List<CampaignTuple>,
  override val campaignId: String? = null,
  private val repository: CampaignRepository,
) : Campaign {
  override var adListId: String? = null

  override var deepLinkUtms: Map<String, String> = mutableMapOf()

  override suspend fun sendImpressionEvent(
    type: String,
    toReplace: Map<String, String>,
    toAppend: Map<String, String>,
  ) = withContext(Dispatchers.IO) {
    impressions.filter { it.name == type }
      .forEach { repository.knock((it.url.injectToUrl(toReplace, toAppend))) }
  }

  override suspend fun sendClickEvent(
    type: String,
    toReplace: Map<String, String>,
    toAppend: Map<String, String>,
  ) = withContext(Dispatchers.IO) {
    clicks.filter { it.name == type }
      .forEach { repository.knock((it.url.injectToUrl(toReplace, toAppend))) }
  }

  override suspend fun sendDownloadEvent(
    type: String,
    toReplace: Map<String, String>,
    toAppend: Map<String, String>,
  ) = withContext(Dispatchers.IO) {
    downloads.filter { it.name == type }
      .forEach { repository.knock((it.url.injectToUrl(toReplace, toAppend))) }
  }

  override fun extractParameter(
    parameter: String,
    type: String,
  ): String? =
    getParameter(impressions, parameter, type) ?: getParameter(clicks, parameter, type)
    ?: getParameter(downloads, parameter, type)

  private fun getParameter(
    campaignList: List<CampaignTuple>,
    parameter: String,
    type: String?,
  ): String? {
    campaignList.filter { it.name == type }.forEach {
      val url = Uri.parse(it.url)
      val param = url.getQueryParameter(parameter)

      if (param != null) return param
    }

    return null
  }

  private fun String.injectToUrl(
    toReplace: Map<String, String>,
    toAppend: Map<String, String>,
  ): String {
    var newUrl = this
    toReplace.forEach {
      newUrl = newUrl.replace(it.key, it.value)
    }

    val filtered = toAppend.filterNot { (k, _) ->
      Regex("([?&])$k=").containsMatchIn(newUrl)
    }

    val params = filtered
      .map { "${it.key}=${it.value}" }
      .joinToString("&")
      .takeIf { it.isNotEmpty() }
      ?.let {
        (if (newUrl.contains("?")) "&" else "?") + it
      }
    return newUrl + (params ?: "")
  }
}

interface CampaignRepository {
  suspend fun knock(url: String)
}

@Keep
data class CampaignTuple(
  val name: String,
  val url: String,
)
