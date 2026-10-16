package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.model.PaECampaignJson
import cm.aptoide.pt.campaigns.data.model.PaEEventMissionsJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionsJson
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface PaECampaignsApi {
  // v2 (AND-911): card totals that count the MMP-confirmed missions this build can earn.
  @GET("api/v2/campaigns")
  suspend fun getCampaigns(
    @Query("sections") sections: List<String> = listOf("trending", "keep_playing"),
  ): PaECampaignJson

  // v2 (AND-910): includes the missions the developer's MMP confirms, the click link and the
  // attribution status. v1 keeps serving releases that can't act on them.
  @GET("api/v2/campaigns/{package_name}/missions")
  suspend fun getCampaignMissions(
    @Path("package_name") packageName: String
  ): PaEMissionsJson

  @GET("api/v2/missions")
  suspend fun getMissions(
    @Query("mission_type") missionType: String
  ): PaEEventMissionsJson
}
