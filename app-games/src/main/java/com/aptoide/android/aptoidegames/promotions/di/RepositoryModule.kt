package com.aptoide.android.aptoidegames.promotions.di

import android.content.Context
import androidx.room.Room
import cm.aptoide.pt.aptoide_network.di.StoreName
import com.aptoide.android.aptoidegames.ahab.di.RetrofitAhab
import com.aptoide.android.aptoidegames.promotions.data.AGPromotionsRepository
import com.aptoide.android.aptoidegames.promotions.data.AGPromotionsRepository.PromotionsApi
import com.aptoide.android.aptoidegames.promotions.data.PromotionsRepository
import com.aptoide.android.aptoidegames.promotions.data.database.PromotionsDatabase
import com.aptoide.android.aptoidegames.promotions.data.database.SkippedPromotionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Provides
  @Singleton
  fun providesPromotionsRepository(
    @RetrofitAhab retrofitAhab: Retrofit,
    @StoreName storeName: String,
  ): PromotionsRepository = AGPromotionsRepository(
    promotionsApi = retrofitAhab.create(PromotionsApi::class.java),
    storeName = storeName,
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
