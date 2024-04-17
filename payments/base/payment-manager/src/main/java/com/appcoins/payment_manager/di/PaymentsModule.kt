package com.appcoins.payment_manager.di

import com.appcoins.payment_manager.manager.PaymentManagerImpl
import com.appcoins.payment_manager.manager.PaymentMethodFactoryProvider
import com.appcoins.payment_manager.repository.broker.PaymentsRepositoryImpl
import com.appcoins.payment_method.adyen.di.AdyenModule
import com.appcoins.payment_method.paypal.di.PayPalModule
import com.appcoins.payments.arch.PaymentManager
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.payments.arch.StaticGetAllowedIds
import com.appcoins.payments.network.di.NetworkModule
import com.appcoins.product_inventory.di.ProductModule

object PaymentsModule {

  val paymentManager: PaymentManager by lazy {
    PaymentManagerImpl(
      productInventoryRepository = ProductModule.productInventoryRepository,
      walletProvider = PaymentsInitializer.walletProvider,
      paymentsRepository = PaymentsRepositoryImpl(
        restClient = NetworkModule.microServicesRestClient,
        channel = PaymentsInitializer.channel
      ),
      paymentMethodFactory = PaymentMethodFactoryProvider(
        paymentMethodFactories = listOf(
          PayPalModule.paypalPaymentMethodFactory,
          AdyenModule.adyenPaymentMethodFactory
        ),
        getAllowedIds = PaymentsInitializer.getAllowedIds
          ?: StaticGetAllowedIds(
            PayPalModule.paypalPaymentMethodFactory.knownIds,
            AdyenModule.adyenPaymentMethodFactory.knownIds
          )
      )
    )
  }
}
