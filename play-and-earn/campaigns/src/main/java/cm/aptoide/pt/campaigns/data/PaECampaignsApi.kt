package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.model.PaECampaignJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionsJson
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface PaECampaignsApi {

  @GET("/play-and-earn/campaigns")
  suspend fun getCampaigns(
    @Query("sections") sections: List<String> = listOf("trending")
  ): PaECampaignJson

  @GET("/play-and-earn/campaigns/{package_name}/missions")
  suspend fun getCampaignMissions(
    @Path("package_name") packageName: String
  ): PaEMissionsJson
}
