package com.appcoins.payment_manager.di

import com.appcoins.payment_manager.manager.PaymentManager
import com.appcoins.payment_manager.manager.PaymentManagerImpl
import com.appcoins.payment_manager.repository.broker.PaymentsRepository
import com.appcoins.payment_manager.repository.broker.PaymentsRepositoryImpl
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
internal interface PaymentsModule {

  @Singleton
  @Binds
  fun bindPaymentManager(paymentManager: PaymentManagerImpl): PaymentManager

  companion object {

    @Singleton
    @Provides
    fun providePaymentsRepository(
      @MicroServicesHostUrl baseUrl: String,
      getUserAgent: GetUserAgent,
    ): PaymentsRepository = PaymentsRepositoryImpl(
      RestClient.with(
        baseUrl = baseUrl,
        timeout = Duration.ofSeconds(30),
        getUserAgent = getUserAgent
      )
    )
  }
}
