package com.aptoide.android.aptoidegames.gamegenie.di

import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieApiService
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal object GameGenieModule {
    @Provides
    fun provideChatbotApiService(@BaseOkHttp okHttpClient: OkHttpClient): GameGenieApiService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://genie-chatbot.aptoide.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameGenieApiService::class.java)
    }

    @Provides
    fun provideChatbotRepository(apiService: GameGenieApiService): GameGenieRepository {
        return GameGenieRepository(apiService)
    }
}
