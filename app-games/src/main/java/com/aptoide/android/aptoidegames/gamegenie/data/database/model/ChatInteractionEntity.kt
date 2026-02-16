package com.aptoide.android.aptoidegames.gamegenie.data.database.model

import androidx.annotation.Keep

@Keep
data class ChatInteractionEntity(
  val gpt: String,
  val user: UserMessageEntity?,
  val videoId: String?,
  val apps: String,
)
