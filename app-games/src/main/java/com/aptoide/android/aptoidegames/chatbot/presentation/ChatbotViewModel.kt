package com.aptoide.android.aptoidegames.chatbot.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppInfoUseCase
import com.aptoide.android.aptoidegames.chatbot.data.ChatbotRepository
import com.aptoide.android.aptoidegames.chatbot.data.GetAppApiRepository
import com.aptoide.android.aptoidegames.chatbot.data.GetAppApiRepositoryImpl
import com.aptoide.android.aptoidegames.chatbot.data.toDomainModelQuickChatbotFix
import com.aptoide.android.aptoidegames.chatbot.domain.ChatInteraction
import com.aptoide.android.aptoidegames.chatbot.domain.ChatbotMessage
import com.aptoide.android.aptoidegames.chatbot.domain.ConversationIntent
import com.aptoide.android.aptoidegames.chatbot.domain.GameContext
import com.aptoide.android.aptoidegames.chatbot.domain.MessageAuthor
import com.aptoide.android.aptoidegames.chatbot.io_models.ChatbotRequest
import com.aptoide.android.aptoidegames.chatbot.io_models.ChatbotResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val chatbotRepository: ChatbotRepository,
    private val appInfoUseCase: GetAppApiRepository,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(ChatbotViewModelState())

    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun reload(userMessage: String) {
        viewModelScope.launch {
            val updatedConversation = updateConversation(userMessage)

            updateStateForLoading(updatedConversation)

            try {
                val response =
                    chatbotRepository.getMessages(
                        ChatbotRequest(
                            conversation = updatedConversation,
                            state = uiState.value.state
                        )
                    )

                val apps = response.conversation.last().apps.mapNotNull { app ->
                    app.packageName.let {
                        try {
                            appInfoUseCase.getApp(it)
                        } catch (e: Exception) {
                            Timber.w(e, "Failed to fetch app info for package: $it")
                            null // Ignore error, continue with valid apps
                        }
                    }
                }

                updateStateForSuccess(response, apps)

            } catch (e: Throwable) {
                Timber.w(e)
                viewModelState.update {
                    it.copy(
                        type = when (e) {
                            is IOException -> ChatbotUIStateType.NO_CONNECTION
                            else -> ChatbotUIStateType.ERROR
                        }
                    )
                }
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
                type = ChatbotUIStateType.LOADING,
                conversation = updatedConversation,
                apps = emptyList()
            )
        }
    }

    private fun updateStateForSuccess(response: ChatbotResponse, apps: List<App>) {
        viewModelState.update {
            it.copy(
                messages = response.conversation.toChatbotMessageList(),
                type = ChatbotUIStateType.IDLE,
                conversation = response.conversation,
                state = ConversationIntent.fromValue(response.state) ?: ConversationIntent.OTHER,
                apps = apps,
                packageName = response.packageName
            )
        }
    }
}

fun List<ChatInteraction>.toChatbotMessageList(): List<ChatbotMessage> {
    return this.flatMap { interaction ->
        listOfNotNull(
            ChatbotMessage(MessageAuthor.GPT, interaction.gpt),
            interaction.user?.let { ChatbotMessage(MessageAuthor.USER, it) }
        )
    }
}

private data class ChatbotViewModelState(
    val messages: List<ChatbotMessage> = emptyList(),
    val type: ChatbotUIStateType = ChatbotUIStateType.IDLE,
    val conversation: List<ChatInteraction> = listOf(
        ChatInteraction(
            "こんにちは！アプリやゲームの検索や発見をお手伝いします。必要に応じてご利用ください。",
            null,
                emptyList()
        )
    ),
    val state: ConversationIntent = ConversationIntent.START,
    val context: List<GameContext> = emptyList(),
    val apps: List<App> = emptyList(),
    val packageName: String? = null
) {
    fun toUiState(): ChatbotUIState =
        ChatbotUIState(
            // messages state must not be altered by code, only the conversation
            messages = conversation.toChatbotMessageList(),
            type = type,
            conversation = conversation,
            state = state,
            context = context,
            apps = apps,
            packageName = packageName
        )
}

