package com.aptoide.android.aptoidegames.chatbot

import com.aptoide.android.aptoidegames.chatbot.io_models.ChatbotRequest
import com.aptoide.android.aptoidegames.chatbot.io_models.ChatbotResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatbotApiService {
    @POST("chat") // Replace with your actual endpoint
    suspend fun getMessages(
        @Body request: ChatbotRequest // The request body
    ): ChatbotResponse
}
