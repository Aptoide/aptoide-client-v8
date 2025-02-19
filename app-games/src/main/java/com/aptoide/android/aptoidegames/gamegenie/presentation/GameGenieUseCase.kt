package com.aptoide.android.aptoidegames.gamegenie.presentation

import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.ConversationInfo
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameGenieUseCase @Inject constructor(
  private val gameGenieManager: GameGenieManager,
) {
  suspend fun getToken(): Token {
    return gameGenieManager.getToken()
  }

  suspend fun reloadConversation(chat: GameGenieChat): GameGenieChat {
    return sendMessage(chat, chat.conversation.lastOrNull()?.user ?: "")
  }

  suspend fun sendMessage(
    chat: GameGenieChat,
    userMessage: String,
  ): GameGenieChat {
    val updatedConversation = chat.conversation.toMutableList().apply {
      if (isNotEmpty()) {
        this[lastIndex] = last().copy(user = userMessage)
      }
    }
    return postMessage(chat.id, updatedConversation).fold(
      onSuccess = { response ->
        val convertedChat = response.toGameGenieChat()
        gameGenieManager.saveChat(convertedChat)
        convertedChat
      },
      onFailure = { throw it }
    )
  }

  suspend fun loadChat(id: String): GameGenieChat? {
    return runCatching { gameGenieManager.getChatById(id).toDomain() }.getOrNull()
  }

  fun getAllChats(): Flow<List<ConversationInfo>> {
    return gameGenieManager.getAllChats()
      .map { conversations -> conversations.map { conversation -> conversation.toConversationInfo() } }
  }

  private suspend fun postMessage(
    id: String,
    conversation: List<ChatInteraction>,
  ): Result<GameGenieResponse> {
    return runCatching {
      val token = getToken()
      gameGenieManager.postMessage(token, GameGenieRequest(id, conversation))
    }
  }

  suspend fun deleteChat(id: String) {
    gameGenieManager.deleteChat(id)
  }

  private fun GameGenieHistoryEntity.toConversationInfo(): ConversationInfo {
    val userMessage = conversation[0].user
    return if (userMessage == null) ConversationInfo(
      id = id,
      title = null, // for now
      firstMessage = conversation[0].gpt
    ) else ConversationInfo(
      id = id,
      title = null, // for now
      firstMessage = userMessage
    )
  }
}
