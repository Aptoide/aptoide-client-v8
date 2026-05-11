package cm.aptoide.pt.feature_gamegenie.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import cm.aptoide.pt.aptoide_network.di.GameGenieOkHttp
import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.feature_apps.data.AppMapper
import cm.aptoide.pt.feature_gamegenie.data.GameCompanionsRepository
import cm.aptoide.pt.feature_gamegenie.data.GameCompanionsRepositoryImpl
import cm.aptoide.pt.feature_gamegenie.data.GameGenieApiService
import cm.aptoide.pt.feature_gamegenie.data.GameGenieAppRepository
import cm.aptoide.pt.feature_gamegenie.data.GameGenieAppRepositoryImpl
import cm.aptoide.pt.feature_gamegenie.data.GameGenieLocalRepository
import cm.aptoide.pt.feature_gamegenie.data.GameGenieSharedPreferencesRepository
import cm.aptoide.pt.feature_gamegenie.data.database.CachedCompanionGameDao
import cm.aptoide.pt.feature_gamegenie.data.database.GameGenieDatabase
import cm.aptoide.pt.feature_gamegenie.data.database.GameGenieHistoryDao
import cm.aptoide.pt.feature_gamegenie.presentation.GameGenieManager
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
    impl: GameGenieSharedPreferencesRepository,
  ): GameGenieLocalRepository

  @Binds
  @Singleton
  abstract fun bindGameCompanionsRepository(
    impl: GameCompanionsRepositoryImpl,
  ): GameCompanionsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object GameGenieModule {
  @Provides
  @Singleton
  fun provideChatbotApiService(
    @GameGenieOkHttp okHttpClient: OkHttpClient,
    @GameGenieBaseUrl baseUrl: String,
  ): GameGenieApiService {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(baseUrl)
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
    .fallbackToDestructiveMigration(true)
    .addMigrations(GameGenieDatabase.SixthMigration())
    .build()

  @Singleton
  @Provides
  fun provideGameGenieDao(database: GameGenieDatabase): GameGenieHistoryDao =
    database.getGameGenieHistoryDao()

  @Singleton
  @Provides
  fun provideCachedCompanionGameDao(database: GameGenieDatabase): CachedCompanionGameDao =
    database.getCachedCompanionGameDao()

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
