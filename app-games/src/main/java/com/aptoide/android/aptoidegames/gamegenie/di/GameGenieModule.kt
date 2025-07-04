package com.aptoide.android.aptoidegames.gamegenie.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import cm.aptoide.pt.aptoide_network.di.GameGenieOkHttp
import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.feature_apps.data.AppMapper
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.feature_search.data.AutoCompleteSuggestionsRepository
import cm.aptoide.pt.feature_search.data.database.SearchHistoryRepository
import cm.aptoide.pt.feature_search.data.network.RemoteSearchRepository
import cm.aptoide.pt.feature_search.domain.repository.SearchRepository
import cm.aptoide.pt.feature_search.domain.usecase.SearchUseCase
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieApiService
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieAppRepository
import com.aptoide.android.aptoidegames.gamegenie.data.GameGenieAppRepositoryImpl
import com.aptoide.android.aptoidegames.gamegenie.data.database.GameGenieDatabase
import com.aptoide.android.aptoidegames.gamegenie.data.database.GameGenieDatabase.FirstMigration
import com.aptoide.android.aptoidegames.gamegenie.data.database.GameGenieDatabase.SecondMigration
import com.aptoide.android.aptoidegames.gamegenie.data.database.GameGenieHistoryDao
import com.aptoide.android.aptoidegames.gamegenie.presentation.GameGenieManager
import com.aptoide.android.aptoidegames.search.AppGamesSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object GameGenieModule {
  @Provides
  fun provideSearchUseCase(searchRepository: SearchRepository): SearchUseCase {
    return SearchUseCase(searchRepository)
  }

  @Singleton
  @Provides
  fun provideSearchRepository(
    mapper: AppMapper,
    searchHistoryRepository: SearchHistoryRepository,
    remoteSearchRepository: RemoteSearchRepository,
    autoCompleteSuggestionsRepository: AutoCompleteSuggestionsRepository,
    gameGenieManager: GameGenieManager,
    featureFlags: FeatureFlags
  ): SearchRepository {
    return AppGamesSearchRepository(
      mapper = mapper,
      searchHistoryRepository = searchHistoryRepository,
      remoteSearchRepository = remoteSearchRepository,
      autoCompleteSuggestionsRepository = autoCompleteSuggestionsRepository,
      gameGenieManager = gameGenieManager,
      featureFlags = featureFlags
    )
  }

  @Provides
  fun provideChatbotApiService(@GameGenieOkHttp okHttpClient: OkHttpClient): GameGenieApiService {
    return Builder()
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
    return GameGenieManager(apiService, db)
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
      FirstMigration(),
      SecondMigration()
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
