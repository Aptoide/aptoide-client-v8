package com.aptoide.android.aptoidegames.chatbot.presentation

import com.aptoide.android.aptoidegames.chatbot.domain.ChatInteraction
import com.aptoide.android.aptoidegames.chatbot.domain.ChatbotMessage
import com.aptoide.android.aptoidegames.chatbot.domain.ConversationIntent
import com.aptoide.android.aptoidegames.chatbot.domain.GameContext


data class ChatbotUIState(
    val messages: List<ChatbotMessage>,
    val type: ChatbotUIStateType,
    val conversation: List<ChatInteraction>,
    val state: ConversationIntent = ConversationIntent.START,
    val context: List<GameContext> = emptyList(),
    val apps: List<GameContext> = emptyList(),
    val packageName: String? = null
    )

enum class ChatbotUIStateType {
    IDLE, LOADING, NO_CONNECTION, ERROR
}