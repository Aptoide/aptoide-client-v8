package com.appcoins.payment_method.paypal.di

import com.appcoins.payment_manager.di.RetrofitAPICatappult
import com.appcoins.payment_method.paypal.repository.PaypalHttpHeadersProvider
import com.appcoins.payment_method.paypal.repository.PaypalHttpHeadersProviderImpl
import com.appcoins.payment_method.paypal.repository.PaypalRepository
import com.appcoins.payment_method.paypal.repository.PaypalRepositoryImpl
import com.appcoins.payment_method.paypal.repository.PaypalRepositoryImpl.PaypalV2Api
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
  fun bindPaypalHttpHeadersProvider(provider: PaypalHttpHeadersProviderImpl): PaypalHttpHeadersProvider

  @Singleton
  @Binds
  fun bindPaypalRepository(repository: PaypalRepositoryImpl): PaypalRepository

  companion object {
    @Singleton
    @Provides
    fun providePaypalV2Api(@RetrofitAPICatappult retrofit: Retrofit): PaypalV2Api =
      retrofit.create(PaypalV2Api::class.java)
  }
}
