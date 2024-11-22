package com.aptoide.android.aptoidegames.gamegenie.domain

import androidx.annotation.Keep

@Keep
data class ChatInteraction(
    val gpt: String,
    val user: String?,
    val apps: List<GameContext>,
)

fun List<ChatInteraction>.toChatbotMessageList(): List<GameGenieMessage> {
  return this.flatMap { interaction ->
    listOfNotNull(
      GameGenieMessage(MessageAuthor.GPT, interaction.gpt),
      interaction.user?.let { GameGenieMessage(MessageAuthor.USER, it) }
    )
  }
}
