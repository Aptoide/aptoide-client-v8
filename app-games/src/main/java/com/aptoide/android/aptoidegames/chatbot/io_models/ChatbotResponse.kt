package com.aptoide.android.aptoidegames.chatbot.io_models

import com.aptoide.android.aptoidegames.chatbot.domain.ChatInteraction
import com.aptoide.android.aptoidegames.chatbot.domain.GameContext
import com.google.gson.annotations.SerializedName

data class ChatbotResponse (
    val conversation: List<ChatInteraction>,
    val state: String,
    val context: List<GameContext>,
    @SerializedName("package") val packageName: String?,
)