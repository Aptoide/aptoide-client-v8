package com.aptoide.android.aptoidegames.gamegenie.io_models

import androidx.annotation.Keep

@Keep
data class GameGenieResponse(
  val id: String,
  val title: String,
  val conversation: List<ChatInteractionResponse>,
)
