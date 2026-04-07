package com.aptoide.android.aptoidegames.promotions.di

import android.content.Context
import androidx.room.Room
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.promotions.data.AGPromotionsRepository
import com.aptoide.android.aptoidegames.promotions.data.PromotionsRepository
import com.aptoide.android.aptoidegames.promotions.data.database.PromotionsDatabase
import com.aptoide.android.aptoidegames.promotions.data.database.SkippedPromotionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Provides
  @Singleton
  fun providesPromotionsRepository(
    featureFlags: FeatureFlags,
  ): PromotionsRepository = AGPromotionsRepository(
    featureFlags = featureFlags,
  )

  @Singleton
  @Provides
  fun providePromotionsDatabase(
    @ApplicationContext appContext: Context,
  ): PromotionsDatabase = Room.databaseBuilder(
    appContext,
    PromotionsDatabase::class.java,
    "ag_promotions.db"
  ).build()

  @Provides
  @Singleton
  fun providesSkippedPromotionsRepository(
    database: PromotionsDatabase,
  ): SkippedPromotionsRepository = database.getSkippedPromotionsDao()
}
