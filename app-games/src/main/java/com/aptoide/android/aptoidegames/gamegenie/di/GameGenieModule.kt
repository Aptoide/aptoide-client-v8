package com.aptoide.android.aptoidegames.gamegenie.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import cm.aptoide.pt.aptoide_network.di.GameGenieOkHttp
import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.feature_apps.data.AppMapper
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieApiService
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieAppRepository
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieAppRepositoryImpl
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieLocalRepository
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieSharedPreferencesRepository
import com.aptoide.android.aptoidegames.gamegenie.data.database.GameGenieDatabase
import com.aptoide.android.aptoidegames.gamegenie.data.database.GameGenieHistoryDao
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class GameGenieBindsModule {

  @Binds
  @Singleton
  abstract fun bindGameGenieLocalRepository(
    impl: GameGenieSharedPreferencesRepository
  ): GameGenieLocalRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object GameGenieModule {
  @Provides
  @Singleton
  fun provideChatbotApiService(
    @GameGenieOkHttp okHttpClient: OkHttpClient,
  ): GameGenieApiService {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(BuildConfig.GAME_GENIE_API)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(GameGenieApiService::class.java)
  }

  @Provides
  fun provideChatbotManager(
    apiService: GameGenieApiService,
    db: GameGenieDatabase,
  ): GameGenieManager {
    return GameGenieManager(
      apiService,
      db.getGameGenieHistoryDao(),
      db.getGameCompanionDao()
    )
  }

  @Singleton
  @Provides
  fun provideGameGenieDatabase(
    @ApplicationContext appContext: Context,
  ): GameGenieDatabase = Room.databaseBuilder(
    appContext,
    GameGenieDatabase::class.java,
    "ag_game_genie.db"
  )
    .fallbackToDestructiveMigration()
    .addMigrations(
      GameGenieDatabase.FirstMigration(),
      GameGenieDatabase.SecondMigration(),
      GameGenieDatabase.ThirdMigration(),
      GameGenieDatabase.FourthMigration()
    )
    .build()

  @Singleton
  @Provides
  fun provideGameGenieDao(database: GameGenieDatabase): GameGenieHistoryDao =
    database.getGameGenieHistoryDao()

  @Provides
  @Singleton
  fun providesAppRepository(
    @RetrofitV7 retrofitV7: Retrofit,
    appMapper: AppMapper,
    packageManager: PackageManager,
  ): GameGenieAppRepository = GameGenieAppRepositoryImpl(
    appsRemoteDataSource = retrofitV7.create(GameGenieAppRepositoryImpl.Retrofit::class.java),
    mapper = appMapper,
    scope = CoroutineScope(Dispatchers.IO),
    packageManager = packageManager
  )
}
