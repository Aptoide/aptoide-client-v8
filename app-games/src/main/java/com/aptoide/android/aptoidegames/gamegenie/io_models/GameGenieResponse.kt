package com.aptoide.android.aptoidegames.gamegenie.io_models

import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.google.gson.annotations.SerializedName

data class GameGenieResponse (
    val conversation: List<ChatInteraction>,
    val state: String,
    @SerializedName("package") val packageName: String?,
)