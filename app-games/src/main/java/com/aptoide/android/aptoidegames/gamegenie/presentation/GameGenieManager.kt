package com.aptoide.android.aptoidegames.gamegenie.presentation

import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieApiService
import com.aptoide.android.aptoidegames.gamegenie.data.database.GameGenieDatabase
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.domain.toToken
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class GameGenieManager @Inject constructor(
  private val gameGenieApi: GameGenieApiService,
  private val gameGenieDatabase: GameGenieDatabase,
) {
  private var cachedToken: Token? = null

  suspend fun getToken(): Token {
    return cachedToken ?: fetchNewToken()
  }

  private suspend fun fetchNewToken(): Token {
    return runCatching {
      val newToken = gameGenieApi.getToken().toToken()
      cachedToken = newToken
      newToken
    }.getOrElse { e ->
      Timber.e(e, "Failed to fetch token")
      cachedToken ?: throw IOException("Failed to fetch token and no cached token available", e)
    }
  }

  suspend fun postMessage(
    token: Token,
    request: GameGenieRequest,
  ): GameGenieResponse {
    return try {
      gameGenieApi.postMessage("Bearer ${token.token}", request)
    } catch (e: HttpException) {
      if (e.code() == 401) {
        Timber.i("Token expired, requesting a new token")
        val newToken = fetchNewToken()
        gameGenieApi.postMessage("Bearer ${newToken.token}", request) // Retry with new token
      } else {
        throw e
      }
    }
  }

  suspend fun getChatById(id: String): GameGenieHistoryEntity {
    return gameGenieDatabase.getGameGenieHistoryDao().getChatById(id)
  }

  suspend fun saveChat(chat: GameGenieChat) {
    gameGenieDatabase.getGameGenieHistoryDao().saveChat(chat.toEntity())
  }

  fun getAllChats(): Flow<List<GameGenieHistoryEntity>> {
    return gameGenieDatabase.getGameGenieHistoryDao().getAllChats()
  }

  suspend fun deleteChat(id: String) {
    gameGenieDatabase.getGameGenieHistoryDao().deleteChat(id)
  }
}
