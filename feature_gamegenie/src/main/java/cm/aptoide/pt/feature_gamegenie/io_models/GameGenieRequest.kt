package cm.aptoide.pt.feature_gamegenie.io_models

import androidx.annotation.Keep
import cm.aptoide.pt.feature_gamegenie.domain.ChatInteractionHistory
import cm.aptoide.pt.feature_gamegenie.domain.GameContext

@Keep
data class GameGenieRequest(
  val id: String?,
  val title: String?,
  val conversation: List<ChatInteractionHistory>,
  val metadata: GameGenieMetadata?,
)

@Keep
data class GameGenieCompanionRequest(
  val id: String?,
  val title: String?,
  val conversation: List<ChatInteractionHistory>,
  val selectedGame: String,
)

@Keep
data class GameGenieMetadata(
  val packages: List<GameContext>,
)
