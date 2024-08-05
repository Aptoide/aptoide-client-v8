package cm.aptoide.pt.feature_campaigns

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MMPLinkerCampaign(
  private val campaign: Campaign,
) {
  val campaignType = "mmp-linker"

  fun sendDownloadEvent(oemid: String) {
    CoroutineScope(Dispatchers.Main).launch {
      campaign.sendDownloadEvent(type = campaignType, toReplace = mapOf("{{OEMID}}" to oemid))
    }
  }
}

fun Campaign.toMMPLinkerCampaign(): MMPLinkerCampaign = MMPLinkerCampaign(this)
