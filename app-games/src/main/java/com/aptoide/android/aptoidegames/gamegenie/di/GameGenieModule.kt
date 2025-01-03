package com.aptoide.android.aptoidegames.gamegenie.di

import android.content.Context
import androidx.room.Room
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieApiService
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieRepository
import com.aptoide.android.aptoidegames.gamegenie.data.database.GameGenieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object GameGenieModule {
  @Provides
  fun provideChatbotApiService(@BaseOkHttp okHttpClient: OkHttpClient): GameGenieApiService {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(BuildConfig.GAME_GENIE_API)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(GameGenieApiService::class.java)
  }

  @Provides
  fun provideChatbotRepository(apiService: GameGenieApiService): GameGenieRepository {
    return GameGenieRepository(apiService)
  }

  @Singleton
  @Provides
  fun provideGameGenieDatabase(
    @ApplicationContext appContext: Context,
  ): GameGenieDatabase = Room.databaseBuilder(
    appContext,
    GameGenieDatabase::class.java,
    "ag_game_genie.db"
  ).build()
}
