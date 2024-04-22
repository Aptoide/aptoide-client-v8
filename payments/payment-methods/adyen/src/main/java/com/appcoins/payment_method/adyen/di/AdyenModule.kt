package com.appcoins.payment_method.adyen.di

import com.adyen.checkout.adyen3ds2.Adyen3DS2Configuration
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.components.model.payments.request.PaymentMethodDetails
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.redirect.RedirectConfiguration
import com.appcoins.payment_method.adyen.AdyenPaymentMethodFactory
import com.appcoins.payment_method.adyen.repository.AdyenV2RepositoryImpl
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.payments.network.di.NetworkModule

var PaymentsInitializer.adyenKey: String
  get() = apiKey
  set(value) {
    apiKey = value
  }
private lateinit var apiKey: String

var PaymentsInitializer.adyenEnvironment: Environment
  get() = aEnvironment
  set(value) {
    aEnvironment = value
  }
private lateinit var aEnvironment: Environment

val PaymentsInitializer.cardConfiguration by lazy {
  CardConfiguration.Builder(PaymentsInitializer.application, PaymentsInitializer.adyenKey)
    .setEnvironment(PaymentsInitializer.adyenEnvironment)
    .build()
}

val PaymentsInitializer.redirectConfiguration by lazy {
  RedirectConfiguration.Builder(PaymentsInitializer.application, PaymentsInitializer.adyenKey)
    .setEnvironment(PaymentsInitializer.adyenEnvironment)
    .build()
}

val PaymentsInitializer.threeDS2Configuration by lazy {
  Adyen3DS2Configuration.Builder(PaymentsInitializer.application, PaymentsInitializer.adyenKey)
    .setEnvironment(PaymentsInitializer.adyenEnvironment)
    .build()
}

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
