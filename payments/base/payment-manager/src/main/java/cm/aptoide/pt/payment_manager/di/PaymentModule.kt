package cm.aptoide.pt.payment_manager.di

import cm.aptoide.pt.payment_manager.manager.PaymentManager
import cm.aptoide.pt.payment_manager.manager.PaymentManagerImpl
import cm.aptoide.pt.payment_manager.payment.PaymentMethodFactory
import cm.aptoide.pt.payment_manager.payment.credit_card.CreditCardPaymentMethodFactory
import cm.aptoide.pt.payment_manager.repository.broker.BrokerRepository
import cm.aptoide.pt.payment_manager.repository.broker.BrokerRepositoryImpl
import cm.aptoide.pt.payment_manager.repository.product.ProductRepository
import cm.aptoide.pt.payment_manager.repository.product.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
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

  companion object {
    @JvmStatic
    @Provides
    @Singleton
    fun provideListPaymentMethodsFactory() : List<PaymentMethodFactory<*>> = listOf(
      CreditCardPaymentMethodFactory()
    )
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OSPDataStore
