package com.appcoins.payment_method.adyen.di

import com.adyen.checkout.components.model.payments.request.PaymentMethodDetails
import com.appcoins.payment_method.adyen.AdyenPaymentMethodFactory
import com.appcoins.payment_method.adyen.repository.AdyenV2RepositoryImpl
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.network.di.NetworkModule

object AdyenModule {

  val adyenPaymentMethodFactory: PaymentMethodFactory<Pair<String, PaymentMethodDetails>>
    by lazy {
      AdyenPaymentMethodFactory(
        adyenRepository = AdyenV2RepositoryImpl(
          NetworkModule.microServicesRestClient
        )
      )
    }
}
