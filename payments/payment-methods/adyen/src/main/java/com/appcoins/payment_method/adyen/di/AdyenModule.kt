package com.appcoins.payment_method.adyen.di

import com.appcoins.payment_method.adyen.repository.AdyenV2Repository
import com.appcoins.payment_method.adyen.repository.AdyenV2RepositoryImpl
import com.appcoins.payments.arch.GetUserAgent
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.di.MicroServicesHostUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class AdyenModule {

  @Singleton
  @Provides
  fun provideAdyenV2Repository(
    @MicroServicesHostUrl baseUrl: String,
    getUserAgent: GetUserAgent,
  ): AdyenV2Repository = AdyenV2RepositoryImpl(
    RestClient.with(
      baseUrl = baseUrl,
      getUserAgent = getUserAgent
    )
  )
}
