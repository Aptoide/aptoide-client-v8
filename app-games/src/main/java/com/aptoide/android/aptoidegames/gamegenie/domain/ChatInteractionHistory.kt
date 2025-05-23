package com.aptoide.android.aptoidegames.gamegenie.domain

import androidx.annotation.Keep

@Keep
data class ChatInteractionHistory(
  val gpt: String,
  val user: String?,
  val videoId: String?,
  val apps: List<String>,
)