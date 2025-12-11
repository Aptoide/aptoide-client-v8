package com.aptoide.android.aptoidegames.gamegenie.presentation

import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieApiService
import com.aptoide.android.aptoidegames.gamegenie.data.database.GameCompanionDao
import com.aptoide.android.aptoidegames.gamegenie.data.database.GameGenieHistoryDao
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameCompanionEntity
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.CompanionSuggestions
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.domain.toToken
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieCompanionRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import com.aptoide.android.aptoidegames.gamegenie.io_models.toDomain
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class GameGenieManager @Inject constructor(
  private val gameGenieApi: GameGenieApiService,
  private val gameGenieHistoryDao: GameGenieHistoryDao,
  private val gameCompanionDao: GameCompanionDao,
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

  suspend fun postMessageCompanion(
    token: Token,
    request: GameGenieCompanionRequest,
  ): GameGenieResponse {
    return try {
      gameGenieApi.postMessageCompanion("Bearer ${token.token}", request)
    } catch (e: HttpException) {
      if (e.code() == 401) {
        Timber.i("Token expired, requesting a new token")
        val newToken = fetchNewToken()
        gameGenieApi.postMessageCompanion(
          "Bearer ${newToken.token}", request
        ) // Retry with new token
      } else {
        throw e
      }
    }
  }

  suspend fun getChatById(id: String): GameGenieHistoryEntity? {
    return runCatching {
      gameGenieHistoryDao.getChatById(id)
    }.getOrNull()
  }

  suspend fun getGameCompanionChat(packageName: String): GameCompanionEntity? {
    return runCatching {
      gameCompanionDao.getChatByPackageName(packageName)
    }.getOrNull()
  }

  fun getAllGameCompanions(): Flow<List<GameCompanionEntity>> = gameCompanionDao.getAllChats()

  suspend fun saveOrUpdateChat(chat: GameGenieChat) {
    val oldChat = getChatById(chat.id)
    val newChat = chat.toEntity()
    if (oldChat != null) {
      val lastMessage = oldChat.conversation.last()
        .copy(
          user = chat.conversation[chat.conversation.size - 2].user?.toEntity()
        )
      val fixedOldChat = oldChat.conversation.dropLast(1) + lastMessage
      gameGenieHistoryDao
        .saveChat(
          oldChat.copy(
            conversation = fixedOldChat + newChat.conversation.last()
          )
        )
    } else {
      gameGenieHistoryDao
        .saveChat(newChat)
    }
  }

  suspend fun saveOrUpdateChatCompanion(
    packageName: String,
    chat: GameGenieChat,
  ) {
    val oldChat = getGameCompanionChat(packageName)
    val newChat = chat.toCompanionEntity(packageName)
    if (oldChat != null) {
      val lastMessage = oldChat.conversation.last()
        .copy(
          user = chat.conversation[chat.conversation.size - 2].user?.toEntity()
        )
      val fixedOldChat = oldChat.conversation.dropLast(1) + lastMessage
      gameCompanionDao
        .saveChat(
          oldChat.copy(
            conversation = fixedOldChat + newChat.conversation.last(),
            lastMessageTimestamp = newChat.lastMessageTimestamp
          )
        )
    } else {
      gameCompanionDao
        .saveChat(newChat)
    }
  }

  fun getAllChats(): Flow<List<GameGenieHistoryEntity>> {
    return gameGenieHistoryDao.getAllChats()
  }

  suspend fun deleteChat(id: String) {
    gameGenieHistoryDao.deleteChat(id)
  }

  suspend fun getCompanionSuggestions(
    token: Token,
    selectedGame: String,
    lang: String,
  ): CompanionSuggestions {
    return try {
      gameGenieApi.getCompanionSuggestions("Bearer ${token.token}", selectedGame, lang).toDomain()
    } catch (e: HttpException) {
      if (e.code() == 401) {
        Timber.i("Token expired, requesting a new token")
        val newToken = fetchNewToken()
        gameGenieApi.getCompanionSuggestions(
          "Bearer ${newToken.token}",
          selectedGame,
          lang
        ).toDomain()
      } else {
        throw e
      }
    }
  }
}
