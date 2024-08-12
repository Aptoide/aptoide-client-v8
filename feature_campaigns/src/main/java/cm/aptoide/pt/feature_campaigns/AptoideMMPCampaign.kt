package cm.aptoide.pt.feature_campaigns

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AptoideMMPCampaign(
  private val campaign: Campaign,
) {
  val campaignType = "aptoide-mmp"

  companion object {
    lateinit var oemid: String
    lateinit var utmSource: String
    val allowedBundleTags: MutableMap<String, Pair<String, String>> = mutableMapOf()
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
      campaign.sendImpressionEvent(
        campaignType,
        buildReplaceMap(packageName),
        buildBaseMap(allowedBundleTags[bundleTag]?.first, allowedBundleTags[bundleTag]?.second)
      )
    }
  }

  fun sendClickEvent(
    bundleTag: String?,
    isCta: Boolean = false,
  ) {
    if (!allowedBundleTags.keys.contains(bundleTag)) return
    CoroutineScope(Dispatchers.Main).launch {
      campaign.sendClickEvent(
        campaignType,
        buildReplaceMap(),
        buildAppendMap(
          utmMedium = allowedBundleTags[bundleTag]?.first,
          utmCampaign = allowedBundleTags[bundleTag]?.second,
          isCta = isCta,
        )
      )
    }
  }

  fun sendDownloadEvent(
    bundleTag: String?,
    searchKeyword: String? = null,
    currentScreen: String?,
    isCta: Boolean = false,
  ) {
    val utmMedium = bundleTag?.let {
      allowedBundleTags[it]?.first
    } ?: currentScreen
    CoroutineScope(Dispatchers.Main).launch {
      campaign.sendDownloadEvent(
        campaignType,
        buildReplaceMap(),
        buildAppendMap(
          utmMedium = utmMedium,
          utmCampaign = allowedBundleTags[bundleTag]?.second,
          searchKeyword = searchKeyword,
          isCta = isCta
        )
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
  ): Map<String, String> {
    val map = mutableMapOf<String, String>()
    utmSource.let { map["utm_source"] = it }
    utmMedium?.let { map["utm_medium"] = it }
    utmCampaign?.let { campaign ->
      map["utm_campaign"] = campaign
    }

    return map
  }

  private fun buildAppendMap(
    utmMedium: String?,
    utmCampaign: String?,
    searchKeyword: String? = null,
    isCta: Boolean,
  ): Map<String, String> {
    val map = buildBaseMap(utmMedium, utmCampaign).toMutableMap()
    utmCampaign?.let { _ ->
      if (isCta) {
        map["utm_content"] = "cta"
      } else {
        this.campaign.placementType?.let { map["utm_content"] = it }
      }
    }
    searchKeyword?.takeIf { it.isNotEmpty() }?.let { map["utm_term"] = it }
    return map
  }
}

fun Campaign.toAptoideMMPCampaign(): AptoideMMPCampaign = AptoideMMPCampaign(this)
