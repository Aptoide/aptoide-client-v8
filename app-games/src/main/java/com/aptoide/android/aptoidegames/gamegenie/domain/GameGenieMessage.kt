package com.aptoide.android.aptoidegames.gamegenie.domain

data class GameGenieMessage(
  val author: MessageAuthor,
  val messageBody: String,
)

enum class MessageAuthor {
  GPT,
  USER
}

fun GameGenieMessage.isUserMessage() = this.author == MessageAuthor.USER

fun String.toMessageAuthor(): MessageAuthor {
  return if (this == "gpt") MessageAuthor.GPT
  else MessageAuthor.USER
}