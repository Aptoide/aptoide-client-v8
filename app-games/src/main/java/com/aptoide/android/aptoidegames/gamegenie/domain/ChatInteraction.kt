package com.aptoide.android.aptoidegames.gamegenie.domain

data class ChatInteraction (
    val gpt: String,
    val user: String?,
    val apps: List<GameContext>
)