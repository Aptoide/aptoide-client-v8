package com.aptoide.android.aptoidegames.gamegenie.io_models

import androidx.annotation.Keep
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteractionHistory
import com.aptoide.android.aptoidegames.gamegenie.domain.GameContext

@Keep
data class GameGenieRequest(
  val id: String?,
  val title: String?,
  val conversation: List<ChatInteractionHistory>,
  val metadata: GameGenieMetadata?,
)

@Keep
data class GameGenieMetadata(
  val packages: List<GameContext>,
)