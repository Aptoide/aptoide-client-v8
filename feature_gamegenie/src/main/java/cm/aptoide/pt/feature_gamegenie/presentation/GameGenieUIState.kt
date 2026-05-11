package cm.aptoide.pt.feature_gamegenie.presentation

import cm.aptoide.pt.feature_gamegenie.domain.GameCompanion
import cm.aptoide.pt.feature_gamegenie.domain.GameGenieChat
import cm.aptoide.pt.feature_gamegenie.domain.Suggestion
import cm.aptoide.pt.feature_gamegenie.domain.Token

data class GameGenieUIState(
  val type: GameGenieUIStateType,
  val chat: GameGenieChat,
  val apps: List<String> = emptyList(),
  val token: Token?,
  val selectedGame: GameCompanion? = null,
  val suggestions: List<Suggestion> = emptyList(),
)

enum class GameGenieUIStateType {
  IDLE,
  LOADING,
  NO_CONNECTION,
  ERROR,
  LOADING_CHAT
}
