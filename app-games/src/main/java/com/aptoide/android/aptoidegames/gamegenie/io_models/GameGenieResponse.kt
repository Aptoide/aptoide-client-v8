package com.aptoide.android.aptoidegames.gamegenie.io_models

import androidx.annotation.Keep
import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction

@Keep
data class GameGenieResponse(
  val id: String,
  val conversation: List<ChatInteraction>,
)