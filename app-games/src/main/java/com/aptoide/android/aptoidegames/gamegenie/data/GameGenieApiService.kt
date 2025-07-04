package com.aptoide.android.aptoidegames.gamegenie.data

import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieSearchResponse
import com.aptoide.android.aptoidegames.gamegenie.io_models.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GameGenieApiService {
  @POST("v2/chat")
  suspend fun postMessage(
    @Header("Authorization") bearerToken: String,
    @Body request: GameGenieRequest,
  ): GameGenieResponse

  @GET("search")
  suspend fun searchApps(
    @Header("Authorization") bearerToken: String,
    @Query("search") query: String,
    @Query("store_name") store: String? = null,
  ): GameGenieSearchResponse

  @POST("token")
  suspend fun getToken(): TokenResponse
}
