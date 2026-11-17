package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.model.PaELeaderboardJson
import cm.aptoide.pt.campaigns.data.model.PaEUserProgressJson
import retrofit2.http.GET
import retrofit2.http.Query

internal interface PaEProgressApi {
  @GET("api/v1/users/me/progress")
  suspend fun getMyProgress(): PaEUserProgressJson

  @GET("api/v1/leaderboard/weekly")
  suspend fun getWeeklyLeaderboard(@Query("limit") limit: Int): PaELeaderboardJson
}
