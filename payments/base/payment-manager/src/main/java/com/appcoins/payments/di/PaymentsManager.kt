package com.appcoins.payments.di

import com.appcoins.payment_manager.manager.GetAllowedIds
import com.appcoins.payment_manager.manager.PaymentManagerImpl
import com.appcoins.payment_manager.manager.PaymentMethodFactoryProvider
import com.appcoins.payment_manager.manager.StaticGetAllowedIds
import com.appcoins.payment_manager.repository.broker.PaymentsRepositoryImpl
import com.appcoins.payments.arch.PaymentManager
import com.appcoins.payments.arch.PaymentMethodFactory

var Payments.getAllowedIds: GetAllowedIds by lateInit {
  StaticGetAllowedIds(
    *paymentMethodFactories
      .map { it.knownIds }
      .toTypedArray()
  )
}

var Payments.paymentMethodFactories: List<PaymentMethodFactory<out Any>> by lateInit()

val Payments.paymentManager: PaymentManager by lazyInit {
  PaymentManagerImpl(
    productInventoryRepository = productInventoryRepository,
    walletProvider = walletProvider,
    paymentsRepository = PaymentsRepositoryImpl(
      restClient = microServicesRestClient,
      channel = channel
    ),
    paymentMethodFactory = PaymentMethodFactoryProvider(
      paymentMethodFactories = paymentMethodFactories,
      getAllowedIds = getAllowedIds
    )
  )
}
