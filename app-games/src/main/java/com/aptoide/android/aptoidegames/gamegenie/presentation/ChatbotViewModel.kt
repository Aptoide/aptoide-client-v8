package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieRepository
import com.aptoide.android.aptoidegames.gamegenie.data.GetAppApiRepository
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieMessage
import com.aptoide.android.aptoidegames.gamegenie.domain.MessageAuthor
import com.aptoide.android.aptoidegames.gamegenie.domain.Token
import com.aptoide.android.aptoidegames.gamegenie.domain.toToken
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieRequest
import com.aptoide.android.aptoidegames.gamegenie.io_models.GameGenieResponse
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
    private val appInfoUseCase: GetAppApiRepository,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ChatbotViewModelState())

    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            val token = gameGenieRepository.getToken()
            viewModelState.update {
                it.copy(
                    token = token.toToken()
                )
            }
        }
    }

    fun reload(userMessage: String) {
        viewModelScope.launch {
            val updatedConversation = updateConversation(userMessage)
            updateStateForLoading(updatedConversation)

            try {
                var token = uiState.value.token
                if (token == null) {
                    token = gameGenieRepository.getToken().toToken()
                    viewModelState.update {
                        it.copy(
                            token = token
                        )
                    }
                }

                val response = makeRequestWithToken(suspend {
                    gameGenieRepository.getMessages(
                        token,
                        GameGenieRequest(
                            id = uiState.value.id,
                            conversation = updatedConversation
                        )
                    )
                })
                val apps = response.conversation.last().apps.mapNotNull { app ->
                    app.packageName.let {
                        try {
                            appInfoUseCase.getApp(it)
                        } catch (e: Exception) {
                            Timber.w(e, "Failed to fetch app info for package: $it")
                            null // ignore error, continue with valid apps
                        }
                    }
                }
                updateStateForSuccess(response, apps)
            } catch (e: Throwable) {
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
        }
    }

    private suspend fun makeRequestWithToken(
        fn: suspend () -> GameGenieResponse
    ): GameGenieResponse {
        return try {
            fn()
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
                return fn()
            } else {
                throw e
            }
        }
    }

    private fun updateConversation(userMessage: String) =
        uiState.value.conversation.run {
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
                conversation = updatedConversation,
                apps = emptyList()
            )
        }
    }

    private fun updateStateForSuccess(response: GameGenieResponse, apps: List<App>) {
        viewModelState.update {
            it.copy(
                type = GameGenieUIStateType.IDLE,
                conversation = response.conversation,
                apps = apps,
            )
        }
    }
}

fun List<ChatInteraction>.toChatbotMessageList(): List<GameGenieMessage> {
    return this.flatMap { interaction ->
        listOfNotNull(
            GameGenieMessage(MessageAuthor.GPT, interaction.gpt),
            interaction.user?.let { GameGenieMessage(MessageAuthor.USER, it) }
        )
    }
}

private data class ChatbotViewModelState(
    val type: GameGenieUIStateType = GameGenieUIStateType.IDLE,
    val conversation: List<ChatInteraction> = listOf(
        ChatInteraction(
            "Hello! I'm here to help you search and discover apps and games. Please use me as needed.",
            null,
            emptyList()
        )
    ),
    val apps: List<App> = emptyList(),
    val id: String = "",
    val token: Token? = null
) {
    fun toUiState(): GameGenieUIState =
        GameGenieUIState(
            type = type,
            conversation = conversation,
            apps = apps,
            id = id,
            token = token
        )
}