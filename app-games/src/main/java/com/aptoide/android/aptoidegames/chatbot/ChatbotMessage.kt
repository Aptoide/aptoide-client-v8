package com.aptoide.android.aptoidegames.chatbot

data class ChatbotMessage(
    val author: MessageAuthor,
    val messageBody: String
)

enum class MessageAuthor {
    GPT,
    USER
}

fun ChatbotMessage.isUserMessage() = this.author == MessageAuthor.USER

fun String.toMessageAuthor(): MessageAuthor {
    return if(this == "gpt") MessageAuthor.GPT
    else MessageAuthor.USER // not really into this
}