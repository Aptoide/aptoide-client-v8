package com.aptoide.android.aptoidegames.promotions.di

import cm.aptoide.pt.aptoide_network.di.StoreName
import com.aptoide.android.aptoidegames.ahab.di.RetrofitAhab
import com.aptoide.android.aptoidegames.promotions.data.AGPromotionsRepository
import com.aptoide.android.aptoidegames.promotions.data.AGPromotionsRepository.PromotionsApi
import com.aptoide.android.aptoidegames.promotions.data.PromotionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}
