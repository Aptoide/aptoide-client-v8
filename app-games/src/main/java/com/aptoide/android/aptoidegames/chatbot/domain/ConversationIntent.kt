package com.aptoide.android.aptoidegames.chatbot.domain

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
