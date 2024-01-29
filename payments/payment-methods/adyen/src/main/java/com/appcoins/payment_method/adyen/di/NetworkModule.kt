package com.appcoins.payment_method.adyen.di

import com.appcoins.payment_manager.di.RetrofitAPIBroker
import com.appcoins.payment_method.adyen.repository.AdyenV2Repository
import com.appcoins.payment_method.adyen.repository.AdyenV2RepositoryImpl
import com.appcoins.payment_method.adyen.repository.AdyenV2RepositoryImpl.AdyenV2Api
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkModule {

  @Singleton
  @Binds
  fun bindAdyenV2Repository(repository: AdyenV2RepositoryImpl): AdyenV2Repository

  companion object {
    @Singleton
    @Provides
    fun provideBrokerApi(@RetrofitAPIBroker retrofit: Retrofit): AdyenV2Api =
      retrofit.create(AdyenV2Api::class.java)
  }
}
