package cm.aptoide.pt.app.mmpcampaigns

import cm.aptoide.pt.BuildConfig
import io.reactivex.Completable
import kotlinx.coroutines.rx2.rxCompletable

class CampaignManager(private val campaignRepository: CampaignRepository) {

  fun convertCampaign(campaign: Campaign, medium: String): Completable {
    return rxCompletable {
      campaign.download.filter { it.name == "aptoide-mmp" }
        .forEach { campaignRepository.knock(it.url.injectCampaignAttributes(medium)) }
    }
  }
}

fun String.injectCampaignAttributes(medium: String): String {
  var newUrl = this
  newUrl = newUrl.replace("{{OEMID}}", BuildConfig.OEMID)

  val appendMap = mutableMapOf<String, String>()
  appendMap["source"] = "Vanilla"
  appendMap["medium"] = medium

  val params = appendMap
    .map { "${it.key}=${it.value}" }
    .joinToString("&")
    .takeIf { it.isNotEmpty() }
    ?.let {
      (if (newUrl.contains("?")) "&" else "?") + it
    }

  return newUrl + (params ?: "")
}