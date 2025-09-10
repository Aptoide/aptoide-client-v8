package cm.aptoide.pt.wallet.gamification.data

import cm.aptoide.pt.wallet.gamification.data.model.LevelsResponse
import cm.aptoide.pt.wallet.gamification.data.model.UserStatsResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface GamificationApi {
  @GET("gamification/1.20230531/user_stats")
  suspend fun getUserStats(
    @Query("address") address: String,
    @Query("lang_code") languageCode: String,
    @Query("currency") currency: String?
  ): UserStatsResponse

  @GET("gamification/levels")
  suspend fun getLevels(
    @Query("address") address: String? = null,
    @Query("currency") currency: String? = null
  ): LevelsResponse
}
