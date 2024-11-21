package com.aptoide.android.aptoidegames.gamegenie.io_models

import com.aptoide.android.aptoidegames.gamegenie.domain.ChatInteraction

data class GameGenieRequest(
  val id: String?,
  val conversation: List<ChatInteraction>,
)
