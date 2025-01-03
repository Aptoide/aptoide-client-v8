package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieRepository
import com.aptoide.android.aptoidegames.gamegenie.data.database.GameGenieDatabase
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.ChatInteractionEntity
import com.aptoide.android.aptoidegames.gamegenie.data.database.model.GameGenieHistoryEntity
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.domain.toToken
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(
  private val gameGenieRepository: GameGenieRepository,
  gameGenieDatabase: GameGenieDatabase,
) : ViewModel() {
  private val viewModelState = MutableStateFlow(ChatbotViewModelState())
  private val database = gameGenieDatabase.getGameGenieHistoryDao()

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    viewModelScope.launch {
      try {
        val token = gameGenieRepository.getToken()
        viewModelState.update {
          it.copy(
            token = token.toToken()
          )
        }
      } catch (e: Exception) {
        e.printStackTrace()
        handleError(e)
      }
    }
  }

  fun reload() {
    handleMessageProcessing(
      updateConversation = { uiState.value.chat.conversation },
      onSuccess = { response, apps -> updateStateForSuccess(response, apps) }
    )
  }

  fun sendMessage(userMessage: String) {
    handleMessageProcessing(
      updateConversation = { updateConversation(userMessage) },
      onSuccess = { response, apps ->
        updateStateForSuccess(response, apps);
      }
    )
  }

  fun setGeneralError() {
    Timber.w("Updated state for general error, all apps failed to load")
    viewModelState.update {
      it.copy(
        type = GameGenieUIStateType.ERROR
      )
    }
  }

  private fun handleMessageProcessing(
    updateConversation: () -> List<ChatInteraction>,
    onSuccess: suspend (GameGenieResponse, List<String>) -> Unit,
  ) {
    viewModelScope.launch {
      val conversation = updateConversation()
      updateStateForLoading(conversation)

      try {
        val token = getTokenIfNeeded()
        val response = makeRequestWithToken {
          gameGenieRepository.postMessage(
            token,
            GameGenieRequest(
              id = uiState.value.chat.id,
              conversation = conversation
            )
          )
        }
        val apps = response.conversation.last().apps.map { app -> app.packageName }
        onSuccess(response, apps)
      } catch (e: Exception) {
        handleError(e)
      }
    }
  }

  private suspend fun getTokenIfNeeded(): Token {
    return uiState.value.token ?: gameGenieRepository.getToken().toToken().also { newToken ->
      viewModelState.update { it.copy(token = newToken) }
    }
  }

  private fun handleError(e: Throwable) {
    Timber.w(e)
    viewModelState.update {
      it.copy(
        type = when (e) {
          is IOException -> GameGenieUIStateType.NO_CONNECTION
          else -> GameGenieUIStateType.ERROR
        }
      )
    }
  }

  private suspend fun makeRequestWithToken(
    requestFunction: suspend () -> GameGenieResponse,
  ): GameGenieResponse {
    return try {
      requestFunction()
    } catch (e: HttpException) {
      if (e.code() == 401) {
        Timber.i("Token expired, requesting a new token")
        val newToken = gameGenieRepository.getToken().toToken()
        viewModelState.update {
          it.copy(
            token = newToken
          )
        }
        // Retry the request with the new token
        return requestFunction()
      } else {
        throw e
      }
    } catch (e: Exception) {
      Timber.e(e, "Request failed with unexpected error")
      throw e
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

  private fun updateStateForLoading(updatedConversation: List<ChatInteraction>) {
    viewModelState.update {
      it.copy(
        type = GameGenieUIStateType.LOADING,
        chat = it.chat.copy(conversation = updatedConversation),
        apps = emptyList()
      )
    }
  }

  private suspend fun updateStateForSuccess(
    response: GameGenieResponse,
    apps: List<String>,
  ) {
    viewModelState.update {
      it.copy(
        type = GameGenieUIStateType.IDLE,
        chat = it.chat.copy(conversation = response.conversation),
        apps = apps,
      )
    }

    database.saveChatById(
      uiState.value.chat.toEntity()
    )
  }
}

private data class ChatbotViewModelState(
  val type: GameGenieUIStateType = GameGenieUIStateType.IDLE,
  val chat: GameGenieChat = GameGenieChat(
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
  fun toUiState(): GameGenieUIState =
    GameGenieUIState(
      type = type,
      chat = chat,
      apps = apps,
      token = token
    )
}

fun GameGenieChat.toEntity(): GameGenieHistoryEntity {
  return GameGenieHistoryEntity(
    id = this.id,
    conversation = this.conversation.map { it.toEntity() }
  )
}

fun ChatInteraction.toEntity(): ChatInteractionEntity {
  return ChatInteractionEntity(
    gpt = this.gpt,
    user = this.user,
    apps = Gson().toJson(this.apps)
  )
}
