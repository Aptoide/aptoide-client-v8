package com.aptoide.android.aptoidegames.gamegenie.domain

import androidx.annotation.Keep
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.gamegenie.purchase.GenieAction

@Keep
data class ChatInteraction(
  val gpt: String,
  val user: UserMessage?,
  val videoId: String?,
  val apps: List<App>,
  val followUps: List<String> = emptyList(),
  /**
   * HACKATHON: transient rich card produced on the client (Genie purchase flow). Never mapped to
   * Room entities nor sent to the backend: turns carrying one are folded out of the history in
   * toGameGenieChatHistory().
   */
  @Transient val action: GenieAction? = null,
)
