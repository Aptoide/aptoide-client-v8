package com.aptoide.android.aptoidegames.gamegenie.di

import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieApiService
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal object GameGenieModule {
    @Provides
    fun provideChatbotApiService(): GameGenieApiService {
        return Retrofit.Builder()
            .baseUrl("https://42d7-2001-8a0-6e3f-a700-bd65-c63f-b9e9-c637.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameGenieApiService::class.java)
    }

    @Provides
    fun provideChatbotRepository(apiService: GameGenieApiService): GameGenieRepository {
        return GameGenieRepository(apiService)
    }
}
