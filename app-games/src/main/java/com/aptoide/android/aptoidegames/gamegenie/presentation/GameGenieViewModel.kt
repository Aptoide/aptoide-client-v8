package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.GameContext
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieUIStateType.NO_CONNECTION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
) : ViewModel() {
  private val viewModelState = MutableStateFlow(GameGenieViewModelState())
  private val _installedApps = MutableStateFlow<List<GameContext>>(emptyList())

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    loadInstalledApps()
    refreshToken()
  }

  private fun loadInstalledApps() {
    viewModelScope.launch {
      gameGenieUseCase.getInstalledApps().collect { apps ->
        _installedApps.value = apps
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

  fun sendMessage(userMessage: String) {
    viewModelScope.launch {
      try {
        updateLoadingState { updateConversation(userMessage) }
        val chat = gameGenieUseCase.sendMessage(
          chat = uiState.value.chat.toGameGenieChatHistory(),
          userMessage = userMessage,
          installedApps = _installedApps.value
        )
        updateSuccessState(chat)
      } catch (e: Throwable) {
        handleError(e)
      }
    }
  }

  private fun updateConversation(userMessage: String): List<ChatInteraction> =
    uiState.value.chat.conversation.run {
      if (isNotEmpty()) {
        val lastInteraction = last().copy(user = userMessage)
        dropLast(1) + lastInteraction
      } else {
        this
      }
    }

  fun createNewChat() {
    viewModelState.update { it.empty(it.token) }
  }

  fun loadConversation(id: String) {
    viewModelScope.launch {
      gameGenieUseCase.loadChat(id)
        .collectLatest { newChat ->
          newChat?.let {
            viewModelState.update { state ->
              state.copy(
                chat = newChat,
                apps = newChat.conversation.flatMap { interaction ->
                  interaction.apps.map { app -> app.packageName }
                }
              )
            }
          }
        }
    }
  }

  private fun updateLoadingState(updateConversation: () -> List<ChatInteraction>) {
    val updatedConversation = updateConversation()
    viewModelState.update {
      it.copy(
        type = GameGenieUIStateType.LOADING,
        chat = it.chat.copy(conversation = updatedConversation),
        apps = emptyList()
      )
    }
  }

  private fun updateSuccessState(response: GameGenieChat) {
    viewModelState.update {
      it.copy(
        type = GameGenieUIStateType.IDLE,
        chat = it.chat.copy(
          id = response.id,
          title = response.title,
          conversation = viewModelState.value.chat.conversation + response.conversation.last()
        ),
        apps = response.conversation.lastOrNull()?.apps?.map { app -> app.packageName }
          ?: emptyList()
      )
    }
  }

  fun setGeneralError() {
    viewModelState.update {
      it.copy(
        type = GameGenieUIStateType.ERROR
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