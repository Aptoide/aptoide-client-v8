package com.aptoide.android.aptoidegames.gamegenie.data

import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import com.aptoide.android.aptoidegames.gamegenie.io_models.TokenResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GameGenieApiService {
  @POST("chat")
  suspend fun postMessages(
    @Header("Authorization") bearerToken: String,
    @Body request: GameGenieRequest,
  ): GameGenieResponse

  @POST("token")
  suspend fun getToken(): TokenResponse
}
