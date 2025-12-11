package com.aptoide.android.aptoidegames.gamegenie.presentation

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import cm.aptoide.pt.feature_apps.data.AppMapper
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import cm.aptoide.pt.install_manager.App
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieAppRepository
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameCompanionEntity
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteractionHistory
import com.aptoide.android.aptoidegames.gamegenie.domain.CompanionSuggestions
import com.aptoide.android.aptoidegames.gamegenie.domain.ConversationInfo
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.domain.GameContext
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChatHistory
import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.domain.UserMessage
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieCompanionRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieMetadata
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.io.IOException
import javax.inject.Inject

private const val MAX_CHATS = 15

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
      val file = java.io.File(path)
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
  private val packageManager: PackageManager,
  private val installManager: InstallManager,
  private val repository: CategoriesRepository,
) {
  suspend fun getToken(): Token {
    return gameGenieManager.getToken()
  }

  fun getInstalledApps(): Flow<List<GameContext>> = appRepository.getInstalledApps()

  suspend fun getGameCompanionsList(): Flow<List<GameCompanion>> {
    val apps = installManager.installedApps.toMutableSet()

    val installedAppsFlow: Flow<List<PackageInfo>> = installManager.appsChanges
      .map { apps.apply { add(it) } }
      .onStart { emit(apps) }
      .map { set -> filterGames(set.mapNotNull(App::packageInfo)) }

    return combine(
      gameGenieManager.getAllGameCompanions(),
      installedAppsFlow
    ) { companionsFromDb: List<GameCompanionEntity>, installedPackages: List<PackageInfo> ->

      val installedMap = installedPackages.associateBy { it.packageName }

      val orderedFromDb = companionsFromDb
        .filter { installedMap.containsKey(it.gamePackageName) }
        .map { entity ->
          val pkg = installedMap[entity.gamePackageName]!!
          GameCompanion(
            name = pkg.applicationInfo?.loadLabel(packageManager).toString(),
            packageName = pkg.packageName,
            versionName = pkg.versionName,
            image = pkg.applicationInfo?.loadIcon(packageManager)
          )
        }

      val missingFromDb = installedPackages
        .filterNot { pkg -> companionsFromDb.any { it.gamePackageName == pkg.packageName } }
        .sortedByDescending { it.firstInstallTime }
        .map { pkg ->
          GameCompanion(
            name = pkg.applicationInfo?.loadLabel(packageManager).toString(),
            packageName = pkg.packageName,
            versionName = pkg.versionName,
            image = pkg.applicationInfo?.loadIcon(packageManager)
          )
        }

      orderedFromDb + missingFromDb
    }
  }

  private suspend fun filterGames(appsList: List<PackageInfo>): List<PackageInfo> {
    val gamesPackageInfoList = ArrayList<PackageInfo>()
    val undefinedPackageInfoMap = HashMap<String, PackageInfo>()
    appsList.forEach { packageInfo ->
      when (packageInfo.applicationInfo?.category) {
        ApplicationInfo.CATEGORY_GAME -> gamesPackageInfoList.add(packageInfo)
        else -> undefinedPackageInfoMap[packageInfo.packageName] = packageInfo
      }
    }
    try {
      repository.getAppsCategories(undefinedPackageInfoMap.keys.toList())
        .map { appCategory ->
          when (appCategory.type) {
            "GAME" -> undefinedPackageInfoMap[appCategory.name]?.let {
              gamesPackageInfoList.add(
                it
              )
            }

            else -> {}
          }
        }
    } catch (e: IOException) {
      e.printStackTrace()
    } catch (t: Throwable) {
      t.printStackTrace()
    }
    return gamesPackageInfoList
  }

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

  suspend fun sendCompanionMessage(
    chat: GameGenieChatHistory,
    userMessage: String,
    selectedGame: String,
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

    return postCompanionMessage(
      chat.id, chat.title, conversationForApi, selectedGame
    ).fold(
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
        gameGenieManager.saveOrUpdateChatCompanion(selectedGame, chatWithFilePaths)
        chatWithFilePaths
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
              }
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

  private suspend fun postCompanionMessage(
    id: String,
    title: String,
    conversation: List<ChatInteractionHistory>,
    selectedGame: String,
  ): Result<GameGenieResponse> {
    return runCatching {
      val token = getToken()
      gameGenieManager.postMessageCompanion(
        token,
        GameGenieCompanionRequest(
          id,
          title,
          conversation,
          selectedGame,
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
