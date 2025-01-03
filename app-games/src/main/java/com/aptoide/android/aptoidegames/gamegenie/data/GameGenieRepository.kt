package com.aptoide.android.aptoidegames.gamegenie.data

import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import com.aptoide.android.aptoidegames.gamegenie.io_models.TokenResponse
import javax.inject.Inject

class GameGenieRepository @Inject constructor(
  private val apiService: GameGenieApiService,
) {
  suspend fun postMessage(
    token: Token,
    request: GameGenieRequest,
  ): GameGenieResponse {
    return apiService.postMessage("Bearer ${token.token}", request)
  }

  suspend fun getToken(): TokenResponse {
    return apiService.getToken()
  }
}
