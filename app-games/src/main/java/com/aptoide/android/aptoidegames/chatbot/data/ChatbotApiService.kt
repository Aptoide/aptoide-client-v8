package com.aptoide.android.aptoidegames.chatbot.data

import com.aptoide.android.aptoidegames.chatbot.io_models.ChatbotRequest
import com.aptoide.android.aptoidegames.chatbot.io_models.ChatbotResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatbotApiService {
    @POST("chat")
    suspend fun postMessages(
        @Body request: ChatbotRequest
    ): ChatbotResponse
}
