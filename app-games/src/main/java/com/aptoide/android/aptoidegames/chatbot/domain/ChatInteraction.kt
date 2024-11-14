package com.aptoide.android.aptoidegames.chatbot.domain

data class ChatInteraction (
    val gpt: String,
    val user: String?,
    val apps: List<GameContext>
)