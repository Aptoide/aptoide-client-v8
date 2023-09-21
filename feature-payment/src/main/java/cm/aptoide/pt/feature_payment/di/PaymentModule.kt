package cm.aptoide.pt.feature_payment.di

import cm.aptoide.pt.feature_payment.manager.PaymentManager
import cm.aptoide.pt.feature_payment.manager.PaymentManagerImpl
import cm.aptoide.pt.feature_payment.network.BrokerApi
import cm.aptoide.pt.feature_payment.network.BrokerApiImpl
import cm.aptoide.pt.feature_payment.repository.broker.BrokerRepository
import cm.aptoide.pt.feature_payment.repository.broker.BrokerRepositoryImpl
import cm.aptoide.pt.feature_payment.repository.product.ProductRepository
import cm.aptoide.pt.feature_payment.repository.product.ProductRepositoryImpl
import cm.aptoide.pt.feature_payment.wallet.WalletProvider
import cm.aptoide.pt.feature_payment.wallet.WalletProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PaymentModule {

  @Singleton
  @Binds
  fun bindPaymentManager(paymentManager: PaymentManagerImpl) : PaymentManager

  @Singleton
  @Binds
  fun bindProductRepository(productRepository: ProductRepositoryImpl) : ProductRepository

  @Singleton
  @Binds
  fun bindWalletProvider(walletProvider: WalletProviderImpl) : WalletProvider

  @Singleton
  @Binds
  fun bindBrokerRepository(brokerRepository: BrokerRepositoryImpl) : BrokerRepository

  @Singleton
  @Binds
  fun bindBrokerApi(brokerApi: BrokerApiImpl) : BrokerApi
}