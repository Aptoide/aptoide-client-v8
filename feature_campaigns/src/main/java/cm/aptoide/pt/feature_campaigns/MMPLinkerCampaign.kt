package cm.aptoide.pt.feature_campaigns

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MMPLinkerCampaign(
  private val campaign: Campaign,
) {
  companion object {
    private lateinit var oemId: String

    fun init(oemId: String) {
      this.oemId = oemId
    }
  }

  val campaignType = "mmp-linker"

  fun sendDownloadEvent() {
    CoroutineScope(Dispatchers.Main).launch {
      campaign.sendDownloadEvent(type = campaignType, toReplace = mapOf("{{OEMID}}" to oemId))
    }
  }
}

fun Campaign.toMMPLinkerCampaign(): MMPLinkerCampaign = MMPLinkerCampaign(this)
