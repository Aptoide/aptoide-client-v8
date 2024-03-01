package com.appcoins.payment_method.adyen.di

import com.adyen.checkout.components.model.payments.request.PaymentMethodDetails
import com.appcoins.payment_method.adyen.CreditCardPaymentMethodFactory
import com.appcoins.payment_method.adyen.repository.AdyenV2RepositoryImpl
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.network.di.NetworkModule

object AdyenModule {

  val creditCardPaymentMethodFactory: PaymentMethodFactory<Pair<String, PaymentMethodDetails>>
    by lazy {
      CreditCardPaymentMethodFactory(
        adyenRepository = AdyenV2RepositoryImpl(
          NetworkModule.microServicesRestClient
        )
      )
    }
}
