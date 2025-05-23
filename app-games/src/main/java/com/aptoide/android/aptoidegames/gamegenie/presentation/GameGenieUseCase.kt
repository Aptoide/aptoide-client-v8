package com.aptoide.android.aptoidegames.gamegenie.presentation

import cm.aptoide.pt.feature_apps.data.AppMapper
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieAppRepository
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteractionHistory
import com.aptoide.android.aptoidegames.gamegenie.domain.ConversationInfo
import com.aptoide.android.aptoidegames.gamegenie.domain.GameContext
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChatHistory
import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieMetadata
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val MAX_CHATS = 15

class GameGenieUseCase @Inject constructor(
  private val gameGenieManager: GameGenieManager,
  private val mapper: AppMapper,
  private val appRepository: GameGenieAppRepository,
) {
  suspend fun getToken(): Token {
    return gameGenieManager.getToken()
  }

  fun getInstalledApps(): Flow<List<GameContext>> = appRepository.getInstalledApps()

  suspend fun reloadConversation(
    chat: GameGenieChat,
    installedApps: List<GameContext>,
  ): GameGenieChat {
    val lastMessage = chat.conversation.lastOrNull()?.user ?: ""
    return if (lastMessage.isNotEmpty())
      sendMessage(chat.toGameGenieChatHistory(), lastMessage, installedApps)
    else
      chat
  }

  suspend fun sendMessage(
    chat: GameGenieChatHistory,
    userMessage: String,
    installedApps: List<GameContext>,
  ): GameGenieChat {
    val updatedConversation = chat.conversation.toMutableList().apply {
      if (isNotEmpty()) {
        this[lastIndex] = last().copy(user = userMessage)
      }
    }
    return postMessage(chat.id, chat.title, updatedConversation, installedApps).fold(
      onSuccess = { response ->
        val convertedChat = response.toGameGenieChat(mapper)
        gameGenieManager.saveOrUpdateChat(convertedChat)
        convertedChat
      },
      onFailure = { throw it }
    )
  }

  fun loadChat(id: String): Flow<GameGenieChat?> = flow {
    runCatching {
      val chat = gameGenieManager.getChatById(id) ?: return@flow emit(null)
      val chatHistory = chat.toDomain()
      val gameGenieChat = GameGenieChat(
        id = chatHistory.id,
        title = chatHistory.title,
        conversation = chatHistory.conversation.map { interaction ->
          ChatInteraction(
            gpt = interaction.gpt,
            user = interaction.user,
            videoId = interaction.videoId,
            apps = interaction.apps.mapNotNull { app ->
              runCatching { appRepository.getApp(app).copy(hasMeta = true) }.getOrNull()
            }
          )
        }
      )
      emit(gameGenieChat)
    }
  }.flowOn(Dispatchers.IO)

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
    conversation: List<ChatInteractionHistory>,
    installedApps: List<GameContext>,
  ): Result<GameGenieResponse> {
    return runCatching {
      val token = getToken()
      gameGenieManager.postMessage(
        token,
        GameGenieRequest(
          id,
          title,
          conversation,
          GameGenieMetadata(installedApps)
        )
      )
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
