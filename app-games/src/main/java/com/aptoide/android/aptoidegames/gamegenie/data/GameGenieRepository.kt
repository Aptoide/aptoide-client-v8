package com.aptoide.android.aptoidegames.gamegenie.data

import com.aptoide.android.aptoidegames.gamegenie.data.database.GameGenieHistoryDao
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import com.aptoide.android.aptoidegames.gamegenie.io_models.TokenResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GameGenieRepository @Inject constructor(
  private val apiService: GameGenieApiService,
  private val gameGenieDao: GameGenieHistoryDao,
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

  suspend fun saveChatById(entity: GameGenieHistoryEntity) {
    gameGenieDao.saveChatById(entity)
  }

  suspend fun getAllChats(): Flow<List<GameGenieHistoryEntity>> {
    return gameGenieDao.getAllChats()
  }

  suspend fun getChatById(id: String): GameGenieHistoryEntity {
    return gameGenieDao.getChatById(id)
  }

  suspend fun deleteChat(id: String) {
    gameGenieDao.deleteChat(id)
  }
}
