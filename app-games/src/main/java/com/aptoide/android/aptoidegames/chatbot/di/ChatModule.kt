package com.aptoide.android.aptoidegames.chatbot.di

import com.aptoide.android.aptoidegames.chatbot.data.ChatbotApiService
import com.aptoide.android.aptoidegames.chatbot.data.ChatbotRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal object ChatModule {
    @Provides
    fun provideChatbotApiService(): ChatbotApiService {
        return Retrofit.Builder()
            .baseUrl("https://42d7-2001-8a0-6e3f-a700-bd65-c63f-b9e9-c637.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatbotApiService::class.java)
    }

    @Provides
    fun provideChatbotRepository(apiService: ChatbotApiService): ChatbotRepository {
        return ChatbotRepository(apiService)
    }
}
