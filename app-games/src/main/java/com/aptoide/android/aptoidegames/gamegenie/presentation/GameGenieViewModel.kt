package com.aptoide.android.aptoidegames.gamegenie.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.Suggestion
import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.domain.GameContext
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieUIStateType.NO_CONNECTION
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class GameGenieViewModel @Inject constructor(
  private val gameGenieUseCase: GameGenieUseCase,
  @ApplicationContext private val context: Context,
) : ViewModel() {
  private val viewModelState = MutableStateFlow(GameGenieViewModelState())
  private val _installedApps = MutableStateFlow<List<GameContext>>(emptyList())

  private val _conversation = MutableStateFlow<List<ChatInteraction>>(emptyList())
  val conversation: Flow<List<ChatInteraction>> = _conversation
    .map { it.asReversed() }

  private val _firstLoad = MutableStateFlow(true)
  val firstLoad: Flow<Boolean> = _firstLoad

  private val _installedGames = MutableStateFlow<List<GameCompanion>>(emptyList())
  val installedGames = _installedGames.asStateFlow()

  private val _selectedGame = MutableStateFlow<GameCompanion?>(null)
  val selectedGame = _selectedGame.asStateFlow()

  private val _suggestions = MutableStateFlow<List<Suggestion>>(emptyList())
  val companionSuggestions = _suggestions.asStateFlow()

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    loadInstalledApps()
    loadInstalledGames()
    refreshToken()
    _conversation.value = viewModelState.value.chat.conversation
  }

  fun setSelectedGame(game: GameCompanion) {
    _selectedGame.value = game
  }

  fun resetSelectedGame() {
    _selectedGame.value = null
    _suggestions.value = emptyList()
    emptyChat()
  }

  private fun loadInstalledApps() {
    viewModelScope.launch {
      gameGenieUseCase.getInstalledApps().collect { apps ->
        _installedApps.value = apps
      }
    }
  }

  private fun loadInstalledGames() {
    viewModelScope.launch {
      gameGenieUseCase.getGameCompanionsList().collect { games ->
        _installedGames.value = games
      }
    }
  }

  private fun refreshToken() {
    viewModelScope.launch {
      runCatching {
        gameGenieUseCase.getToken().let { token ->
          viewModelState.update { it.copy(token = token) }
        }
      }.getOrElse {
        viewModelState.update {
          it.copy(type = NO_CONNECTION)
        }
      }
    }
  }

  fun reload() {
    viewModelScope.launch {
      runCatching {
        val chat = gameGenieUseCase.reloadConversation(uiState.value.chat, _installedApps.value)
        updateSuccessState(chat)
      }.getOrNull()
    }
  }

  fun sendMessage(
    userMessage: String,
    imagePathOrBase64: String? = null,
  ) {
    viewModelScope.launch {
      try {
        updateConversation(userMessage, imagePathOrBase64)
        updateLoadingState()
        val selectedGame = _selectedGame.value
        val chat = selectedGame?.packageName?.let {
          gameGenieUseCase.sendCompanionMessage(
            chat = uiState.value.chat.toGameGenieChatHistory(),
            userMessage = userMessage,
            selectedGame = it,
            imageBase64 = imagePathOrBase64
          )
        }
          ?: gameGenieUseCase.sendMessage(
            chat = uiState.value.chat.toGameGenieChatHistory(),
            userMessage = userMessage,
            installedApps = _installedApps.value,
            imageBase64 = imagePathOrBase64
          )
        updateSuccessState(chat)
      } catch (e: Throwable) {
        handleError(e)
      }
    }
  }

  private fun updateConversation(
    userMessage: String,
    imagePath: String? = null,
  ) {
    val current = _conversation.value
    val updated = if (current.isNotEmpty()) {
      val lastInteraction = current.last().copy(
        user = com.aptoide.android.aptoidegames.gamegenie.domain.UserMessage(
          text = userMessage,
          image = imagePath
        )
      )
      current.dropLast(1) + lastInteraction
    } else {
      current
    }
    _conversation.value = updated
  }

  fun setFirstLoadDone() {
    _firstLoad.value = false
  }

  fun emptyChat() {
    viewModelScope.launch {
      _firstLoad.value = true
      viewModelState.update { it.empty(it.token) }
      _conversation.value = viewModelState.value.chat.conversation
    }
  }

  fun loadConversation(id: String) {
    viewModelScope.launch {
      _firstLoad.value = true
      viewModelState.update {
        it.copy(type = GameGenieUIStateType.LOADING_CHAT)
      }

      gameGenieUseCase.loadChat(id)
        .collectLatest { newChat ->
          newChat?.let {
            _conversation.value = newChat.conversation
            viewModelState.update { state ->
              state.copy(
                type = GameGenieUIStateType.IDLE,
                chat = newChat,
                apps = newChat.conversation.flatMap { it.apps.map { app -> app.packageName } }
              )
            }
          }
        }
    }
  }

  fun loadCompanionChat(packageName: String) {
    viewModelScope.launch {
      _firstLoad.value = true
      _suggestions.value = emptyList()
      viewModelState.update { it.copy(type = GameGenieUIStateType.LOADING_CHAT) }

      _selectedGame.value?.name?.let { gameName ->
        loadCompanionSuggestions(gameName)
      }

      gameGenieUseCase.loadCompanionChat(packageName)
        .collectLatest { newChat ->
          newChat?.let { chat ->
            _conversation.value = chat.conversation
            viewModelState.update { state ->
              state.copy(
                type = GameGenieUIStateType.IDLE,
                chat = chat,
                apps = chat.conversation.flatMap { it.apps.map { app -> app.packageName } }
              )
            }
          } ?: run {
            viewModelState.update { it.copy(type = GameGenieUIStateType.IDLE) }
            emptyChat()
          }
        }
    }
  }

  private fun loadCompanionSuggestions(gameName: String) {
    viewModelScope.launch {
      runCatching {
        val locale = context.resources.configuration.locales[0]
        val lang = locale.language
        val suggestions = gameGenieUseCase.getCompanionSuggestions(
          selectedGame = gameName,
          lang = lang
        )
        _suggestions.value = suggestions.suggestions
      }.onFailure { e ->
        Timber.w(e, "Failed to load companion suggestions")
        _suggestions.value = emptyList()
      }
    }
  }

  fun updateLoadingState() {
    val updated = _conversation.value
    viewModelState.update {
      it.copy(
        type = GameGenieUIStateType.LOADING,
        chat = it.chat.copy(conversation = updated),
        apps = emptyList()
      )
    }
  }

  private fun updateSuccessState(response: GameGenieChat) {
    val newMessage = response.conversation.last()
    _conversation.value += newMessage

    viewModelState.update {
      it.copy(
        type = GameGenieUIStateType.IDLE,
        chat = it.chat.copy(
          id = response.id,
          title = response.title,
          conversation = _conversation.value
        ),
        apps = response.conversation.lastOrNull()?.apps?.map { app -> app.packageName }
          ?: emptyList()
      )
    }
  }

  private fun handleError(e: Throwable) {
    Timber.w(e)
    viewModelState.update {
      it.copy(type = mapErrorToState(e))
    }
  }

  private fun mapErrorToState(e: Throwable): GameGenieUIStateType {
    return when (e) {
      is IOException -> NO_CONNECTION
      else -> GameGenieUIStateType.ERROR
    }
  }
}

private data class GameGenieViewModelState(
  val type: GameGenieUIStateType = GameGenieUIStateType.IDLE,
  val chat: GameGenieChat = GameGenieChat(
    "",
    "",
    listOf(
      ChatInteraction(
        "",
        null,
        null,
        emptyList()
      )
    )
  ),
  val apps: List<String> = emptyList(), //store package names
  val token: Token? = null,
) {
  fun empty(
    token: Token? = null,
  ) =
    GameGenieViewModelState(
      GameGenieUIStateType.IDLE,
      GameGenieChat(
        "",
        "",
        listOf(
          ChatInteraction(
            "",
            null,
            null,
            emptyList()
          )
        )
      ),
      emptyList(),
      token,
    )

  fun toUiState(): GameGenieUIState =
    GameGenieUIState(
      type = type,
      chat = chat,
      apps = apps,
      token = token,
    )
}
