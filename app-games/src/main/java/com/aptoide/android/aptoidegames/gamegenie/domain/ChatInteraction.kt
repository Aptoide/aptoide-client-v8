package com.aptoide.android.aptoidegames.gamegenie.domain

import androidx.annotation.Keep
import cm.aptoide.pt.feature_apps.data.App

@Keep
data class ChatInteraction(
  val gpt: String,
  val user: UserMessage?,
  val videoId: String?,
  val apps: List<App>,
)