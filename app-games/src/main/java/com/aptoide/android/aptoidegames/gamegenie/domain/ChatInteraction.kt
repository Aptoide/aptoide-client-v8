package com.aptoide.android.aptoidegames.gamegenie.domain

import androidx.annotation.Keep

@Keep
data class ChatInteraction(
  val gpt: String,
  val user: String?,
  val apps: List<GameContext>,
)
