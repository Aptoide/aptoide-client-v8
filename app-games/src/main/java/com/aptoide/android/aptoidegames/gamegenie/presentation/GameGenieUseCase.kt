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

private const val MAX_CHATS = 15

class GameGenieUseCase @Inject constructor(
  private val gameGenieManager: GameGenieManager,
) {
  suspend fun getToken(): Token {
    return gameGenieManager.getToken()
  }

  suspend fun reloadConversation(chat: GameGenieChat): GameGenieChat {
    val lastMessage = chat.conversation.lastOrNull()?.user ?: ""
    return if (lastMessage.isNotEmpty())
      sendMessage(chat, lastMessage)
    else
      chat
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
    return postMessage(chat.id, chat.title, updatedConversation).fold(
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

  fun getDrawerChats(): Flow<List<ConversationInfo>> =
    gameGenieManager.getAllChats()
      .map { conversations ->
        val pastConversations = conversations.reversed()

        if (pastConversations.size > MAX_CHATS) {
          val chatToDelete = pastConversations.last()
          deleteChat(chatToDelete.id)
        }

        pastConversations.map { conversation ->
          conversation.toConversationInfo()
        }
      }

  private suspend fun postMessage(
    id: String,
    title: String,
    conversation: List<ChatInteraction>,
  ): Result<GameGenieResponse> {
    return runCatching {
      val token = getToken()
      gameGenieManager.postMessage(token, GameGenieRequest(id, title, conversation))
    }
  }

  suspend fun deleteChat(id: String) {
    gameGenieManager.deleteChat(id)
  }

  private fun GameGenieHistoryEntity.toConversationInfo(): ConversationInfo {
    val userMessage = conversation[0].user
    return if (userMessage == null) ConversationInfo(
      id = id,
      title = title,
      firstMessage = conversation[0].gpt
    ) else ConversationInfo(
      id = id,
      title = title,
      firstMessage = userMessage
    )
  }
}
