package com.aptoide.android.aptoidegames.gamegenie.data

import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import javax.inject.Inject

class GameGenieRepository @Inject constructor(
    private val apiService: GameGenieApiService
) {
    suspend fun getMessages(request: GameGenieRequest): GameGenieResponse {
        return apiService.postMessages(request)
    }
}
