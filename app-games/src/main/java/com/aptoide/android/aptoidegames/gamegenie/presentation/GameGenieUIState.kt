package com.aptoide.android.aptoidegames.gamegenie.presentation

import com.aptoide.android.aptoidegames.gamegenie.domain.GameCompanion
import com.aptoide.android.aptoidegames.gamegenie.domain.GameGenieChat
import com.aptoide.android.aptoidegames.gamegenie.domain.Suggestion
import com.aptoide.android.aptoidegames.gamegenie.domain.Token

data class GameGenieUIState(
  val type: GameGenieUIStateType,
  val chat: GameGenieChat,
  val apps: List<String> = emptyList(),
  val token: Token?,
  val selectedGame: GameCompanion? = null,
  val suggestions: List<Suggestion> = emptyList(),
  val isStreaming: Boolean = false,
)

enum class GameGenieUIStateType {
  IDLE,
  LOADING,
  NO_CONNECTION,
  ERROR,
  LOADING_CHAT
}
