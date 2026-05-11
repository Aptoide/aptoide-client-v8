package com.aptoide.android.aptoidegames.gamesfeed.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.gamegenie.data.GamesFeedApiService
import com.aptoide.android.aptoidegames.gamesFeedVisibilityDataStore
import com.aptoide.android.aptoidegames.gamesfeed.repository.AptoideGamesFeedLocalRepository
import com.aptoide.android.aptoidegames.gamesfeed.repository.AptoideGamesFeedRepository
import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedLocalRepository
import com.aptoide.android.aptoidegames.gamesfeed.repository.GamesFeedRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GamesFeedModule {

  @Singleton
  @Provides
  fun provideGamesFeedRepository(
    gameGenieApiService: GamesFeedApiService,
    installManager: InstallManager,
  ): GamesFeedRepository {
    return AptoideGamesFeedRepository(gameGenieApiService, installManager)
  }

  @Singleton
  @Provides
  @GamesFeedVisibilityDataStore
  fun provideGamesFeedVisibilityDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
    appContext.gamesFeedVisibilityDataStore

  @Singleton
  @Provides
  fun provideGamesFeedLocalRepository(@GamesFeedVisibilityDataStore dataStore: DataStore<Preferences>): GamesFeedLocalRepository =
    AptoideGamesFeedLocalRepository(dataStore)

  @Qualifier
  @Retention(AnnotationRetention.BINARY)
  annotation class GamesFeedVisibilityDataStore
}
