package com.aptoide.android.aptoidegames.gamegenie.data

import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GameGenieApiService {
    @POST("chat")
    suspend fun postMessages(
        @Body request: GameGenieRequest
    ): GameGenieResponse
}
