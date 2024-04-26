package com.appcoins.payments.di

import com.appcoins.payments.arch.PaymentManager
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.manager.GetAllowedIds
import com.appcoins.payments.manager.PaymentManagerImpl
import com.appcoins.payments.manager.PaymentMethodFactoryProvider
import com.appcoins.payments.manager.StaticGetAllowedIds
import com.appcoins.payments.manager.repository.broker.PaymentsRepositoryImpl

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
    productsRepository = productsRepository,
    walletProvider = walletProvider,
    paymentsRepository = PaymentsRepositoryImpl(restClient = microServicesRestClient),
    paymentMethodFactory = PaymentMethodFactoryProvider(
      paymentMethodFactories = paymentMethodFactories,
      getAllowedIds = getAllowedIds
    )
  )
}
