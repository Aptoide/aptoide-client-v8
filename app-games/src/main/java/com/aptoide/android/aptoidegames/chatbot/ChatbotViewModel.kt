package com.aptoide.android.aptoidegames.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.chatbot.io_models.ChatInteraction
import com.aptoide.android.aptoidegames.chatbot.io_models.ChatbotRequest
import com.aptoide.android.aptoidegames.chatbot.io_models.ConversationIntent
import com.aptoide.android.aptoidegames.chatbot.io_models.GameContext
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
    private val chatbotRepository: ChatbotRepository
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
            val currentConversation = uiState.value.conversation

            val updatedConversation = if (currentConversation.isNotEmpty()) {
                val lastInteraction = currentConversation.last().copy(user = userMessage)
                currentConversation.dropLast(1) + lastInteraction
            } else {
                currentConversation
            }

            viewModelState.update {
                it.copy(
                    type = ChatbotUiStateType.LOADING,
                    conversation = updatedConversation,
                    apps = emptyList()
                )
            }
            try {
                val response =
                    chatbotRepository.getMessages(
                        ChatbotRequest(
                            conversation = updatedConversation,
                            context = uiState.value.context,
                            state = uiState.value.state
                        )
                    )

                viewModelState.update {
                    it.copy(
                        messages = response.conversation.toChatbotMessageList(),
                        type = ChatbotUiStateType.IDLE,
                        conversation = response.conversation,
                        state = ConversationIntent.fromValue(response.state) ?: ConversationIntent.OTHER,
                        context = response.context,
                        apps = response.conversation.last().apps,
                        packageName = response.packageName
                    )
                }
            } catch (e: Throwable) {
                Timber.w(e)
                viewModelState.update {
                    it.copy(
                        type = when (e) {
                            is IOException -> ChatbotUiStateType.NO_CONNECTION
                            else -> ChatbotUiStateType.ERROR
                        }
                    )
                }
            }
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
    val type: ChatbotUiStateType = ChatbotUiStateType.IDLE,
    val conversation: List<ChatInteraction> = listOf(
        ChatInteraction(
            "Hello! I'm here to help you search or discover apps and games. Use me as needed.",
            null,
                emptyList()
        )
    ),
    val state: ConversationIntent = ConversationIntent.START,
    val context: List<GameContext> = emptyList(),
    val apps: List<GameContext> = emptyList(),
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

