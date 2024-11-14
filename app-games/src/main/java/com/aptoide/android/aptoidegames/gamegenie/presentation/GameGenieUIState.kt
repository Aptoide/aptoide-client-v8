package com.aptoide.android.aptoidegames.gamegenie.presentation

import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieMessage
import com.aptoide.android.aptoidegames.gamegenie.domain.ConversationIntent
import com.aptoide.android.aptoidegames.gamegenie.domain.GameContext


data class GameGenieUIState(
    val messages: List<GameGenieMessage>,
    val type: GameGenieUIStateType,
    val conversation: List<ChatInteraction>,
    val state: ConversationIntent = ConversationIntent.START,
    val context: List<GameContext> = emptyList(),
    val apps: List<App> = emptyList(),
    val packageName: String? = null
    )

enum class GameGenieUIStateType {
    IDLE, LOADING, NO_CONNECTION, ERROR
}