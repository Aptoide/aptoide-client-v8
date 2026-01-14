package cm.aptoide.pt.feature_campaigns

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class AptoideMMPCampaign(
  private val campaign: Campaign,
) {
  val campaignType = "aptoide-mmp"

  companion object {
    lateinit var oemid: String
    var guestUID: String? = null
    fun init(oemid: String) {
      this.oemid = oemid
    }
  }

  fun sendImpressionEvent(
    utmInfo: UTMInfo,
    packageName: String? = null,
  ) {
    CoroutineScope(Dispatchers.Main).launch {
      val utmParams = buildBaseMap(utmInfo)

      if (BuildConfig.DEBUG) {
        Timber.tag("MMP").i("Impression Event - Type: $campaignType")
        utmParams.entries.forEach {
          Timber.tag("MMP").i("  ${it.key}: ${it.value}")
        }
      }

      campaign.sendImpressionEvent(
        campaignType,
        buildReplaceMap(packageName),
        utmParams
      )
    }
  }

  fun sendClickEvent(utmInfo: UTMInfo) {
    if (!utmInfo.shouldSendClickEvents) return
    CoroutineScope(Dispatchers.Main).launch {
      val utmParams = buildAppendMap(utmInfo)

      if (BuildConfig.DEBUG) {
        Timber.tag("MMP").i("Click Event - Type: $campaignType")
        utmParams.entries.forEach {
          Timber.tag("MMP").i("  ${it.key}: ${it.value}")
        }
      }

      campaign.sendClickEvent(
        campaignType,
        buildReplaceMap(),
        utmParams
      )
    }
  }

  fun sendDownloadEvent(utmInfo: UTMInfo) {
    CoroutineScope(Dispatchers.Main).launch {
      val utmParams = buildAppendMap(utmInfo)

      if (BuildConfig.DEBUG) {
        Timber.tag("MMP").i("Download Event - Type: $campaignType")
        utmParams.entries.forEach {
          Timber.tag("MMP").i("  ${it.key}: ${it.value}")
        }
      }

      campaign.sendDownloadEvent(
        campaignType,
        buildReplaceMap(),
        utmParams
      )
    }
  }

  private fun buildReplaceMap(packageName: String? = null): Map<String, String> {
    val map = mutableMapOf<String, String>()
    map["{{OEMID}}"] = oemid
    val package_name = campaign.extractParameter("package_name", campaignType) ?: ""
    packageName?.let { map[package_name] = packageName }
    return map
  }

  private fun buildBaseMap(utmInfo: UTMInfo): Map<String, String> {
    val map = mutableMapOf<String, String>()
    utmInfo.utmSource?.let { map["utm_source"] = it }
    utmInfo.utmMedium?.let { map["utm_medium"] = it }
    utmInfo.utmCampaign?.let { campaign ->
      map["utm_campaign"] = campaign
    }
    guestUID?.takeIf { it.isNotEmpty() }?.let { map["guest_uid"] = it }
    return map
  }

  private fun buildAppendMap(utmInfo: UTMInfo): Map<String, String> {
    val map = buildBaseMap(utmInfo).toMutableMap()
    utmInfo.utmContent?.let { map["utm_content"] = it }
    utmInfo.utmTerm?.takeIf { it.isNotEmpty() }?.let { map["utm_term"] = it }
    return map
  }
}

fun Campaign.toAptoideMMPCampaign(): AptoideMMPCampaign = AptoideMMPCampaign(this)
