package com.aptoide.android.aptoidegames.gamegenie.presentation

import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction
import com.aptoide.android.aptoidegames.gamegenie.domain.Token

data class GameGenieUIState(
    val type: GameGenieUIStateType,
    val conversation: List<ChatInteraction>,
    val id: String,
    val apps: List<String> = emptyList(),
    val token: Token?,
)

enum class GameGenieUIStateType {
  IDLE,
  LOADING,
  NO_CONNECTION,
  ERROR
}
