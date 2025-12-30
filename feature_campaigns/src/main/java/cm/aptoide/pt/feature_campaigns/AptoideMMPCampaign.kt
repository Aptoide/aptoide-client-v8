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
    lateinit var utmSource: String
    var guestUID: String? = null
    val allowedBundleTags: MutableMap<String, UTMInfo> = mutableMapOf()
    fun init(
      oemid: String,
      utmSource: String,
    ) {
      this.oemid = oemid
      this.utmSource = utmSource
    }
  }

  fun sendImpressionEvent(
    bundleTag: String,
    packageName: String? = null,
  ) {
    CoroutineScope(Dispatchers.Main).launch {
      val utmInfo = allowedBundleTags[bundleTag]
      val utmParams = buildBaseMap(
        utmMedium = utmInfo?.utmMedium,
        utmCampaign = campaign.campaignId ?: utmInfo?.utmCampaign
      )

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

  fun sendClickEvent(bundleTag: String?) {
    if (!allowedBundleTags.keys.contains(bundleTag)) return
    CoroutineScope(Dispatchers.Main).launch {
      val utmInfo = allowedBundleTags[bundleTag]
      val utmParams = buildAppendMap(
        utmMedium = utmInfo?.utmMedium,
        utmCampaign = campaign.campaignId ?: utmInfo?.utmCampaign,
        utmContent = utmInfo?.utmContent,
        searchKeyword = utmInfo?.utmTerm,
      )

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

  fun sendDownloadEvent(
    bundleTag: String?,
    searchKeyword: String? = null,
    currentScreen: String?,
    utmCampaign: String? = null,
    utmSourceExterior: String? = null,
  ) {
    val utmInfo = allowedBundleTags[bundleTag]
    val utmMedium = bundleTag?.let { utmInfo?.utmMedium } ?: currentScreen
    CoroutineScope(Dispatchers.Main).launch {
      val utmParams = buildAppendMap(
        utmMedium = utmMedium,
        utmCampaign = utmCampaign ?: utmInfo?.utmCampaign,
        utmContent = utmInfo?.utmContent,
        searchKeyword = searchKeyword ?: utmInfo?.utmTerm,
        utmSourceExterior = utmSourceExterior,
      )

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

  private fun buildBaseMap(
    utmMedium: String?,
    utmCampaign: String?,
    utmSourceExterior: String? = null,
  ): Map<String, String> {
    val map = mutableMapOf<String, String>()
    if (utmSourceExterior != null) {
      utmSourceExterior.let { map["utm_source"] = "$it+$utmSource" }
    } else {
      utmSource.let { map["utm_source"] = it }
    }
    utmMedium?.let { map["utm_medium"] = it }
    utmCampaign?.let { campaign ->
      map["utm_campaign"] = campaign
    }
    guestUID?.takeIf { it.isNotEmpty() }?.let { map["guest_uid"] = it }
    return map
  }

  private fun buildAppendMap(
    utmMedium: String?,
    utmCampaign: String?,
    utmContent: String?,
    utmSourceExterior: String? = null,
    searchKeyword: String? = null,
  ): Map<String, String> {
    val map = buildBaseMap(utmMedium, utmCampaign, utmSourceExterior).toMutableMap()
    utmContent?.let { map["utm_content"] = it }
    searchKeyword?.takeIf { it.isNotEmpty() }?.let { map["utm_term"] = it }
    return map
  }
}

fun Campaign.toAptoideMMPCampaign(): AptoideMMPCampaign = AptoideMMPCampaign(this)
