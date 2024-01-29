package com.appcoins.payment_manager.di

import com.appcoins.payment_manager.manager.PaymentManager
import com.appcoins.payment_manager.manager.PaymentManagerImpl
import com.appcoins.payment_manager.repository.broker.BrokerRepository
import com.appcoins.payment_manager.repository.broker.BrokerRepositoryImpl
import com.appcoins.payment_manager.repository.developer_wallet.DeveloperWalletRepository
import com.appcoins.payment_manager.repository.developer_wallet.DeveloperWalletRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface PaymentModule {

  @Singleton
  @Binds
  fun bindPaymentManager(paymentManager: PaymentManagerImpl): PaymentManager

  @Singleton
  @Binds
  fun bindBrokerRepository(brokerRepository: BrokerRepositoryImpl): BrokerRepository

  @Singleton
  @Binds
  fun bindDeveloperWalletRepository(developerWalletRepository: DeveloperWalletRepositoryImpl): DeveloperWalletRepository
}
