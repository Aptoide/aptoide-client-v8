package com.aptoide.android.aptoidegames.gamegenie.data.database.model

data class ChatInteractionEntity(
  val gpt: String,
  val user: UserMessageEntity?,
  val videoId: String?,
  val apps: String,
)
