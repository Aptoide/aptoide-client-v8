package com.aptoide.android.aptoidegames.chatbot.io_models

import com.google.gson.annotations.SerializedName


data class ChatbotRequest (
    val conversation: List<ChatInteraction>,
    val state: ConversationIntent?,
    val context: List<GameContext>?,
)

data class ChatbotResponse (
    val conversation: List<ChatInteraction>,
    val state: String,
    val context: List<GameContext>,
    @SerializedName("package") val packageName: String?,
)

data class ChatInteraction (
    val gpt: String,
    val user: String?
)

data class GameContext(
    val id: Int,
    val name: String,
    @SerializedName("package") val packageName: String,
    val icon: String
)

enum class ConversationIntent(val value: String) {
    START("start"),
    SEARCH("search"),
    DISCOVER("discover"),
    OTHER("other"),
    DISCOVER_MORE("discover-more"),
    SEARCH_MORE("search-more"),
    GET_APP("get-app");

    companion object {
        private val map = entries.associateBy { it.value }

        fun fromValue(value: String): ConversationIntent? {
            return map[value]
        }
    }
}
