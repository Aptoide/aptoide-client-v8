package com.aptoide.android.aptoidegames.chatbot.domain

import com.google.gson.annotations.SerializedName

data class GameContext(
    val id: Int,
    val name: String,
    @SerializedName("package") val packageName: String,
    val icon: String
)
