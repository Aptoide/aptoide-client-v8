package com.android.gamification.data

import com.android.gamification.data.model.LevelsResponse
import com.android.gamification.data.model.UserStatusResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GamificationApi {
  @GET("gamification/1.20230531/user_stats")
  suspend fun getUserStats(
    @Query("address") address: String,
    @Query("lang_code") languageCode: String,
    @Query("currency") currency: String?
  ): UserStatusResponse

  @GET("gamification/levels")
  suspend fun getLevels(
    @Query("address") address: String? = null,
    @Query("currency") currency: String? = null
  ): LevelsResponse
}
