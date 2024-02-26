package com.appcoins.payment_method.paypal.di

import com.appcoins.payment_method.paypal.repository.PaypalHttpHeadersProvider
import com.appcoins.payment_method.paypal.repository.PaypalHttpHeadersProviderImpl
import com.appcoins.payment_method.paypal.repository.PaypalRepository
import com.appcoins.payment_method.paypal.repository.PaypalRepositoryImpl
import com.appcoins.payments.network.GetUserAgent
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.di.MicroServicesHostUrl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface PayPalModule {

  @Singleton
  @Binds
  fun bindPaypalHttpHeadersProvider(provider: PaypalHttpHeadersProviderImpl): PaypalHttpHeadersProvider

  companion object {
    @Singleton
    @Provides
    fun providePaypalRepository(
      @MicroServicesHostUrl baseUrl: String,
      getUserAgent: GetUserAgent,
      paypalHttpHeaderProvider: PaypalHttpHeadersProvider,
    ): PaypalRepository = PaypalRepositoryImpl(
      RestClient.with(
        baseUrl = baseUrl,
        timeout = Duration.ofSeconds(30),
        getUserAgent = getUserAgent
      ),
      paypalHttpHeaderProvider
    )
  }
}
