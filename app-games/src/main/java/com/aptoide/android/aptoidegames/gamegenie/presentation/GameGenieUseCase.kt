package com.aptoide.android.aptoidegames.gamegenie.presentation

import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.AppMapper
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieAppRepository
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteractionHistory
import com.aptoide.android.aptoidegames.gamegenie.domain.CompanionSuggestions
import com.aptoide.android.aptoidegames.gamegenie.domain.ConversationInfo
import com.aptoide.android.aptoidegames.gamegenie.domain.GameContext
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChatHistory
import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.domain.UserMessage
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieCompanionRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieMetadata
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import com.aptoide.android.aptoidegames.gamegenie.io_models.GenieSseEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

private const val MAX_CHATS = 15

sealed class GameGenieStreamUpdate {
  data class TextChunk(val delta: String) : GameGenieStreamUpdate()

  data class AppsResolved(val apps: List<App>) : GameGenieStreamUpdate()

  data class Video(val videoId: String?) : GameGenieStreamUpdate()

  data class FollowUps(val followUps: List<String>) : GameGenieStreamUpdate()

  data class Failed(val message: String?) : GameGenieStreamUpdate()

  data class Completed(val chat: GameGenieChat) : GameGenieStreamUpdate()
}

private fun isBase64String(str: String): Boolean {
  if (str.contains("/") && str.length < 500) {
    return false
  }

  if (str.length > 500 && str.matches(Regex("^[A-Za-z0-9+/]*={0,2}$"))) {
    return true
  }

  return false
}

private fun encodeImageFileToBase64(filePath: String?): String? {
  return filePath?.let { path ->
    try {
      val file = File(path)
      if (file.exists()) {
        val bytes = file.readBytes()
        encodeToString(bytes, NO_WRAP)
      } else {
        null
      }
    } catch (e: Exception) {
      null
    }
  }
}

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
    val lastMessage = chat.conversation.lastOrNull()?.user
    val lastMessageText = lastMessage?.text ?: ""
    return if (lastMessageText.isNotEmpty())
      sendMessage(chat.toGameGenieChatHistory(), lastMessageText, installedApps)
    else
      chat
  }

  suspend fun sendMessage(
    chat: GameGenieChatHistory,
    userMessage: String,
    installedApps: List<GameContext>,
    imageBase64: String? = null,
  ): GameGenieChat {
    val imageForApi = encodeImageFileToBase64(imageBase64)

    val updatedConversation = chat.conversation.toMutableList().apply {
      if (isNotEmpty()) {
        val userMessageObj = UserMessage(text = userMessage, image = imageBase64)
        this[lastIndex] = last().copy(user = userMessageObj)
      }
    }

    val conversationForApi = updatedConversation.map { interaction ->
      if (interaction.user?.image != null && interaction.user.image == imageBase64) {
        interaction.copy(user = interaction.user.copy(image = imageForApi))
      } else {
        interaction
      }
    }

    return postMessage(chat.id, chat.title, conversationForApi, installedApps).fold(
      onSuccess = { response ->
        val convertedChat = response.toGameGenieChat(mapper)
        val chatWithFilePaths = convertedChat.copy(
          conversation = convertedChat.conversation.mapIndexed { index, interaction ->
            val userImage = interaction.user?.image
            val localInteraction = updatedConversation.getOrNull(index)
            val localImagePath = localInteraction?.user?.image

            if (localImagePath != null && interaction.user != null) {
              interaction.copy(user = interaction.user.copy(image = localImagePath))
            } else if (userImage != null && isBase64String(userImage)) {
              if (index == convertedChat.conversation.lastIndex && imageBase64 != null) {
                interaction.copy(user = interaction.user.copy(image = imageBase64))
              } else {
                interaction
              }
            } else {
              interaction
            }
          }
        )
        gameGenieManager.saveOrUpdateChat(chatWithFilePaths)
        chatWithFilePaths
      },
      onFailure = { throw it }
    )
  }

  fun streamMessage(
    chat: GameGenieChatHistory,
    userMessage: String,
    installedApps: List<GameContext>,
    imageBase64: String? = null,
  ): Flow<GameGenieStreamUpdate> = streamInternal(
    chat = chat,
    userMessage = userMessage,
    imageBase64 = imageBase64,
    openStream = { token, conversationForApi ->
      val request = GameGenieRequest(
        id = chat.id.takeIf { it.isNotBlank() },
        title = chat.title.takeIf { it.isNotBlank() },
        conversation = conversationForApi,
        metadata = GameGenieMetadata(installedApps),
      )
      gameGenieManager.streamMessage(token, request)
    },
    persist = { finalChat -> gameGenieManager.saveOrUpdateChat(finalChat) },
  )

  fun streamCompanionMessage(
    chat: GameGenieChatHistory,
    userMessage: String,
    selectedGame: String,
    imageBase64: String? = null,
  ): Flow<GameGenieStreamUpdate> = streamInternal(
    chat = chat,
    userMessage = userMessage,
    imageBase64 = imageBase64,
    openStream = { token, conversationForApi ->
      val request = GameGenieCompanionRequest(
        id = chat.id.takeIf { it.isNotBlank() },
        title = chat.title.takeIf { it.isNotBlank() },
        conversation = conversationForApi,
        selectedGame = selectedGame,
      )
      gameGenieManager.streamCompanionMessage(token, request)
    },
    persist = { finalChat ->
      gameGenieManager.saveOrUpdateChatCompanion(selectedGame, finalChat)
    },
  )

  private fun streamInternal(
    chat: GameGenieChatHistory,
    userMessage: String,
    imageBase64: String?,
    openStream: suspend (Token, List<ChatInteractionHistory>) -> Flow<GenieSseEvent>,
    persist: suspend (GameGenieChat) -> Unit,
  ): Flow<GameGenieStreamUpdate> = flow {
    val imageForApi = encodeImageFileToBase64(imageBase64)

    val updatedConversation = chat.conversation.toMutableList().apply {
      if (isNotEmpty()) {
        val userMessageObj = UserMessage(text = userMessage, image = imageBase64)
        this[lastIndex] = last().copy(user = userMessageObj)
      }
    }

    val conversationForApi = updatedConversation.map { interaction ->
      if (interaction.user?.image != null && interaction.user.image == imageBase64) {
        interaction.copy(user = interaction.user.copy(image = imageForApi))
      } else {
        interaction
      }
    }

    val token = getToken()

    val gptBuilder = StringBuilder()
    var conversationId: String = chat.id
    var done: GenieSseEvent.Done? = null
    var failure: GenieSseEvent.Error? = null

    var typedAppsResolved: List<App>? = null
    var typedVideoReceived = false
    var typedVideoId: String? = null
    var typedFollowUps: List<String>? = null

    openStream(token, conversationForApi).collect { event ->
      when (event) {
        is GenieSseEvent.Meta -> {
          conversationId = event.id
        }
        is GenieSseEvent.Delta -> {
          if (event.text.isNotEmpty()) {
            gptBuilder.append(event.text)
            emit(GameGenieStreamUpdate.TextChunk(event.text))
          }
        }
        is GenieSseEvent.Apps -> {
          val resolved = event.apps.mapNotNull { ref ->
            runCatching { appRepository.getApp(ref.packageName).copy(hasMeta = true) }.getOrNull()
          }
          typedAppsResolved = resolved
          emit(GameGenieStreamUpdate.AppsResolved(resolved))
        }
        is GenieSseEvent.Video -> {
          typedVideoReceived = true
          typedVideoId = event.videoId
          emit(GameGenieStreamUpdate.Video(event.videoId))
        }
        is GenieSseEvent.FollowUps -> {
          typedFollowUps = event.followUps
          emit(GameGenieStreamUpdate.FollowUps(event.followUps))
        }
        is GenieSseEvent.Done -> {
          done = event
          conversationId = event.id
        }
        is GenieSseEvent.Error -> {
          failure = event
        }
      }
    }

    failure?.let {
      emit(GameGenieStreamUpdate.Failed(it.message))
      return@flow
    }

    val finalDone = done ?: GenieSseEvent.Done(
      id = conversationId,
      title = null,
      apps = emptyList(),
      video = null,
    )

    val resolvedApps = typedAppsResolved
      ?: finalDone.apps.mapNotNull { ref ->
        runCatching { appRepository.getApp(ref.packageName).copy(hasMeta = true) }.getOrNull()
      }
    val resolvedVideo = if (typedVideoReceived) typedVideoId else finalDone.video
    val resolvedFollowUps = typedFollowUps ?: finalDone.followUps

    val finalAssistantInteraction = ChatInteraction(
      gpt = gptBuilder.toString(),
      user = null,
      videoId = resolvedVideo,
      apps = resolvedApps,
      followUps = resolvedFollowUps,
    )

    val persistedConversation = updatedConversation.map { history ->
      ChatInteraction(
        gpt = history.gpt,
        user = history.user,
        videoId = history.videoId,
        apps = emptyList(),
      )
    } + finalAssistantInteraction

    val finalChat = GameGenieChat(
      id = finalDone.id,
      title = finalDone.title ?: chat.title,
      conversation = persistedConversation,
    )

    persist(finalChat)
    emit(GameGenieStreamUpdate.Completed(finalChat))
  }.flowOn(Dispatchers.IO)

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
            },
            followUps = interaction.followUps
          )
        }
      )
      emit(gameGenieChat)
    }
  }.flowOn(Dispatchers.IO)

  fun loadCompanionChat(packageName: String): Flow<GameGenieChat?> = flow {
    gameGenieManager.getGameCompanionChat(packageName)
      ?.toDomain()
      ?.let { chatHistory ->
        val gameGenieChat = GameGenieChat(
          id = chatHistory.id,
          title = chatHistory.title,
          conversation = chatHistory.conversation.map { interaction ->
            ChatInteraction(
              gpt = interaction.gpt,
              user = interaction.user,
              videoId = interaction.videoId,
              apps = interaction.apps.mapNotNull { appName ->
                runCatching { appRepository.getApp(appName).copy(hasMeta = true) }.getOrNull()
              },
              followUps = interaction.followUps
            )
          }
        )
        gameGenieChat
      }
      .also { emit(it) }
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
          GameGenieMetadata(installedApps),
        )
      )
    }
  }

  suspend fun deleteChat(id: String) {
    gameGenieManager.deleteChat(id)
  }

  suspend fun getCompanionSuggestions(
    selectedGame: String,
    lang: String,
  ): CompanionSuggestions {
    val token = getToken()
    return gameGenieManager.getCompanionSuggestions(token, selectedGame, lang)
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
      firstMessage = userMessage.text
    )
  }
}
