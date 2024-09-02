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
            viewModelState.update { it.copy(type = ChatbotUiStateType.LOADING) }
            try {
                // Get the current conversation
                val currentConversation = uiState.value.conversation

                // Update the last ChatInteraction with the user's message
                val updatedConversation = if (currentConversation.isNotEmpty()) {
                    val lastInteraction = currentConversation.last().copy(user = userMessage)
                    currentConversation.dropLast(1) + lastInteraction
                } else {
                    currentConversation
                }
                println(updatedConversation)
                val request = ChatbotRequest(
                    conversation = updatedConversation,
                    context = uiState.value.context,
                    state = uiState.value.state
                )
                val response = chatbotRepository.getMessages(request)
                println(response)

                val messages = response.conversation.toChatbotMessageList()
                val newConvo = response.conversation
                val newState = response.state
                val newContext = response.context
                val newPackageName = response.packageName

                viewModelState.update {
                    it.copy(
                        messages = messages,
                        type = ChatbotUiStateType.IDLE,
                        conversation = newConvo,
                        state = ConversationIntent.fromValue(newState) ?: ConversationIntent.OTHER,
                        context = newContext,
                        packageName = newPackageName ?: null
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
    val res = mutableListOf<ChatbotMessage>()
    this.forEach { interaction ->
        res.add(ChatbotMessage(MessageAuthor.GPT, interaction.gpt))
        if (interaction.user != null) {
            res.add(ChatbotMessage(MessageAuthor.USER, interaction.user))
        }
    }
    return res.toList()
}

private data class ChatbotViewModelState(
    val messages: List<ChatbotMessage> = emptyList(),
    val type: ChatbotUiStateType = ChatbotUiStateType.IDLE,
    val conversation: List<ChatInteraction> = listOf(
        ChatInteraction(
            "Hello! I'm here to help you search or discover apps and games. Use me as needed.",
            null
        )
    ),
    val state: ConversationIntent = ConversationIntent.START,
    val context: List<GameContext> = emptyList(),
    val packageName: String? = null
) {
    fun toUiState(): ChatbotUIState =
        ChatbotUIState(
            messages = conversation.toChatbotMessageList(),
            type = type,
            conversation = conversation,
            state = state,
            context = context,
            packageName = packageName
        )
}

