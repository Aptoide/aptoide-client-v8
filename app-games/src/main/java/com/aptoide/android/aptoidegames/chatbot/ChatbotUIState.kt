package com.aptoide.android.aptoidegames.chatbot

import com.aptoide.android.aptoidegames.chatbot.io_models.ChatInteraction
import com.aptoide.android.aptoidegames.chatbot.io_models.ConversationIntent
import com.aptoide.android.aptoidegames.chatbot.io_models.GameContext


data class ChatbotUIState(
    val messages: List<ChatbotMessage>,
    val type: ChatbotUiStateType,
    val conversation: List<ChatInteraction>,
    val state: ConversationIntent = ConversationIntent.START,
    val context: List<GameContext> = emptyList(),
    val packageName: String? = null
    )

enum class ChatbotUiStateType {
    IDLE, LOADING, NO_CONNECTION, ERROR
}