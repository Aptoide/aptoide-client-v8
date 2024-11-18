package com.aptoide.android.aptoidegames.gamegenie.presentation

import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieMessage
import com.aptoide.android.aptoidegames.gamegenie.domain.GameContext
import com.aptoide.android.aptoidegames.gamegenie.domain.Token


data class GameGenieUIState(
    val type: GameGenieUIStateType,
    val conversation: List<ChatInteraction>,
    val id: String,
    val apps: List<App> = emptyList(),
    val token: Token?
    )

enum class GameGenieUIStateType {
    IDLE, LOADING, NO_CONNECTION, ERROR
}