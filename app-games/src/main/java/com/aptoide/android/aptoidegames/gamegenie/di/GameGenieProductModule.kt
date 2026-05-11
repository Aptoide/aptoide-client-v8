package com.aptoide.android.aptoidegames.gamegenie.di

import cm.aptoide.pt.aptoide_network.di.GameGenieOkHttp
import cm.aptoide.pt.feature_gamegenie.di.GameGenieBaseUrl
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.gamegenie.data.GamesFeedApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GameGenieProductModule {

  @Provides
  @GameGenieBaseUrl
  fun provideGameGenieBaseUrl(): String = BuildConfig.GAME_GENIE_API

  @Provides
  @Singleton
  fun provideGamesFeedApiService(
    @GameGenieOkHttp okHttpClient: OkHttpClient,
  ): GamesFeedApiService {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(BuildConfig.GAME_GENIE_API)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(GamesFeedApiService::class.java)
  }
}
