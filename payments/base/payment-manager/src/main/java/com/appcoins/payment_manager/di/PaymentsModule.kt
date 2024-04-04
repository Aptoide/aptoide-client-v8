package com.appcoins.payment_manager.di

import com.appcoins.payment_manager.manager.PaymentManager
import com.appcoins.payment_manager.manager.PaymentMethodFactoryProvider
import com.appcoins.payment_manager.repository.broker.PaymentsRepositoryImpl
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.payments.network.di.NetworkModule
import com.appcoins.product_inventory.di.ProductModule

object PaymentsModule {

  val paymentManager by lazy {
    PaymentManager.with(
      productInventoryRepository = ProductModule.productInventoryRepository,
      walletProvider = PaymentsInitializer.walletProvider,
      paymentsRepository = PaymentsRepositoryImpl(
        restClient = NetworkModule.microServicesRestClient,
        channel = PaymentsInitializer.channel
      ),
      paymentMethodFactory = PaymentMethodFactoryProvider
    )
  }
}
