package cm.aptoide.pt.feature_campaigns.data

import cm.aptoide.pt.aptoide_network.data.network.base_response.BaseV7DataListResponse
import cm.aptoide.pt.feature_campaigns.Campaign
import cm.aptoide.pt.feature_campaigns.CampaignImpl
import cm.aptoide.pt.feature_campaigns.CampaignRepository
import cm.aptoide.pt.feature_campaigns.CampaignsRepository
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

class CampaignsApiRepository @Inject constructor(
  private val campaignsApi: CampaignsApi,
  private val campaignRepository: CampaignRepository
) : CampaignsRepository {

  override suspend fun getCampaigns(appPackage: String): List<Campaign> =
    campaignsApi.getCampaigns(appPackage).datalist?.list?.map {
      CampaignImpl(
        id = it.campaign.id,
        name = it.campaign.name,
        label = it.campaign.label,
        clicks = it.urls.clicks,
        downloads = it.urls.downloads,
        installs = it.urls.installs,
        repository = campaignRepository
      )
    } ?: emptyList()
}

interface CampaignsApi {
  @GET("ads/get/location=native-aptoide:appview")
  suspend fun getCampaigns(
    @Query("packageName") packageName: String,
  ): BaseV7DataListResponse<CampaignJson>
}