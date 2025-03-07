package com.aptoide.android.aptoidegames.gamegenie.domain

data class GameGenieChat(
  val id: String,
  val title: String,
  val conversation: List<ChatInteraction>,
)
