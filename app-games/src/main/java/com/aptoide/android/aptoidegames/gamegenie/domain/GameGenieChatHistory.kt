package com.aptoide.android.aptoidegames.gamegenie.domain

import androidx.annotation.Keep

@Keep
data class GameGenieChatHistory(
  val id: String,
  val title: String,
  val conversation: List<ChatInteractionHistory>,
)
