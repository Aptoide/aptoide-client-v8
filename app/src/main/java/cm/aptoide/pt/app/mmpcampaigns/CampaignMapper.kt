package cm.aptoide.pt.app.mmpcampaigns

import cm.aptoide.pt.dataprovider.model.v7.listapp.Urls
import cm.aptoide.pt.dataprovider.model.v7.listapp.Urls.Url

class CampaignMapper {

  fun mapCampaign(urls: Urls?): Campaign {
    return if (urls != null) {
      Campaign(
        mapCampaignUrlList(urls.impression ?: emptyList()),
        mapCampaignUrlList(urls.click ?: emptyList()),
        mapCampaignUrlList(urls.download ?: emptyList())
      )
    } else {
      Campaign(emptyList(), emptyList(), emptyList())
    }
  }

  private fun mapCampaignUrlList(urlList: List<Url>): List<CampaignUrl> {
    val campaignUrlList: MutableList<CampaignUrl> = ArrayList()
    for (url in urlList) {
      campaignUrlList.add(CampaignUrl(url.name, url.url))
    }
    return campaignUrlList
  }
}