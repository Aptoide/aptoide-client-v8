package cm.aptoide.pt.app.mmpcampaigns

import android.content.SharedPreferences
import cm.aptoide.pt.BuildConfig
import cm.aptoide.pt.apkfy.ApkFyParser
import io.reactivex.Completable
import kotlinx.coroutines.rx2.rxCompletable

class CampaignManager(private val campaignRepository: CampaignRepository, private val securePreferences: SharedPreferences) {

  fun convertCampaign(campaign: Campaign, medium: String): Completable {
    return rxCompletable {
      campaign.download?.filter { it.name == "aptoide-mmp" }
        ?.forEach { campaignRepository.knock(it.url.injectCampaignAttributes(medium, securePreferences.getString(ApkFyParser.MMP_GUEST_UID, ""))) }
    }
  }
}

fun String.injectCampaignAttributes(medium: String, guestUID: String?): String {
  var newUrl = this
  newUrl = newUrl.replace("{{OEMID}}", BuildConfig.OEMID)

  val appendMap = mutableMapOf<String, String>()
  appendMap["utm_source"] = "Vanilla"
  appendMap["utm_medium"] = medium
  guestUID?.takeIf { it.isNotEmpty() }?.let { appendMap["guest_uid"] = it }

  val params = appendMap
    .map { "${it.key}=${it.value}" }
    .joinToString("&")
    .takeIf { it.isNotEmpty() }
    ?.let {
      (if (newUrl.contains("?")) "&" else "?") + it
    }

  return newUrl + (params ?: "")
}