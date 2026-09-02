package com.aptoide.android.aptoidegames.attribution.di

import cm.aptoide.pt.feature_apkfy.di.RetrofitMMP
import com.aptoide.android.aptoidegames.attribution.data.AptoideAttributionRepository
import com.aptoide.android.aptoidegames.attribution.domain.AttributionManager
import com.aptoide.android.aptoidegames.attribution.domain.AttributionManagerImpl
import com.aptoide.android.aptoidegames.attribution.domain.AttributionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AttributionModule {

  @Provides
  @Singleton
  fun provideAttributionRepository(@RetrofitMMP retrofitMMP: Retrofit): AttributionRepository =
    AptoideAttributionRepository(
      mmpRemoteDataSource = retrofitMMP.create(AptoideAttributionRepository.Service::class.java)
    )

  @Provides
  @Singleton
  fun provideAttributionManager(impl: AttributionManagerImpl): AttributionManager = impl
}
