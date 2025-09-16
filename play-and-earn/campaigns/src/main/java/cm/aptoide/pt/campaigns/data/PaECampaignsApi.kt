package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.model.PaECampaignJson
import retrofit2.http.GET
import retrofit2.http.Query

internal interface PaECampaignsApi {

  @GET("/play-and-earn/campaigns")
  suspend fun getCampaigns(
    @Query("sections") sections: List<String> = listOf("trending")
  ): PaECampaignJson
}
