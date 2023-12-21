package cm.aptoide.pt.payment_manager.di

import cm.aptoide.pt.payment_manager.manager.PaymentManager
import cm.aptoide.pt.payment_manager.manager.PaymentManagerImpl
import cm.aptoide.pt.payment_manager.repository.broker.BrokerRepository
import cm.aptoide.pt.payment_manager.repository.broker.BrokerRepositoryImpl
import cm.aptoide.pt.payment_manager.repository.developer_wallet.DeveloperWalletRepository
import cm.aptoide.pt.payment_manager.repository.developer_wallet.DeveloperWalletRepositoryImpl
import cm.aptoide.pt.payment_manager.repository.product.ProductRepository
import cm.aptoide.pt.payment_manager.repository.product.ProductRepositoryImpl
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
  fun bindProductRepository(productRepository: ProductRepositoryImpl): ProductRepository

  @Singleton
  @Binds
  fun bindBrokerRepository(brokerRepository: BrokerRepositoryImpl): BrokerRepository

  @Singleton
  @Binds
  fun bindDeveloperWalletRepository(developerWalletRepository: DeveloperWalletRepositoryImpl): DeveloperWalletRepository
}