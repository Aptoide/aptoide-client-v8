package com.aptoide.android.aptoidegames.chatbot.io_models

import com.aptoide.android.aptoidegames.chatbot.domain.ChatInteraction
import com.aptoide.android.aptoidegames.chatbot.domain.ConversationIntent
import com.aptoide.android.aptoidegames.chatbot.domain.GameContext

data class ChatbotRequest (
    val conversation: List<ChatInteraction>,
    val state: ConversationIntent?,
)
