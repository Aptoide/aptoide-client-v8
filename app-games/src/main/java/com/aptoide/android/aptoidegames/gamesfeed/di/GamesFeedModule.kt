package com.aptoide.android.aptoidegames.gamesfeed.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
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
  fun provideGamesFeedRepository(featureFlags: FeatureFlags): GamesFeedRepository {
    return AptoideGamesFeedRepository(featureFlags)
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
