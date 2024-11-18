package com.aptoide.android.aptoidegames.gamegenie.io_models

import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.google.gson.annotations.SerializedName

data class GameGenieResponse (
    val id: String,
    val conversation: List<ChatInteraction>,
)