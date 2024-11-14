package com.aptoide.android.aptoidegames.chatbot.data

import com.aptoide.android.aptoidegames.chatbot.io_models.ChatbotRequest
import com.aptoide.android.aptoidegames.chatbot.io_models.ChatbotResponse
import javax.inject.Inject

class ChatbotRepository @Inject constructor(
    private val apiService: ChatbotApiService
) {
    suspend fun getMessages(request: ChatbotRequest): ChatbotResponse {
        return apiService.postMessages(request)
    }
}
