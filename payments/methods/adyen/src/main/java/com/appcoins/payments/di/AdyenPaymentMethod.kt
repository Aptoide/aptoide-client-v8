package com.appcoins.payments.di

import com.adyen.checkout.adyen3ds2.Adyen3DS2Configuration
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.components.model.payments.request.PaymentMethodDetails
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.redirect.RedirectComponent
import com.adyen.checkout.redirect.RedirectConfiguration
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.methods.adyen.AdyenPaymentMethodFactory
import com.appcoins.payments.methods.adyen.repository.AdyenV2RepositoryImpl

var Payments.adyenKey: String by lateInit()

var Payments.adyenEnvironment: Environment by lateInit()

val Payments.cardConfiguration: CardConfiguration by lazyInit {
  CardConfiguration.Builder(context, adyenKey)
    .setEnvironment(adyenEnvironment)
    .build()
}

val Payments.redirectConfiguration: RedirectConfiguration by lazyInit {
  RedirectConfiguration.Builder(context, adyenKey)
    .setEnvironment(adyenEnvironment)
    .build()
}

val Payments.threeDS2Configuration: Adyen3DS2Configuration by lazyInit {
  Adyen3DS2Configuration.Builder(context, adyenKey)
    .setEnvironment(adyenEnvironment)
    .build()
}

val Payments.returnUrl by lazyInit {
  RedirectComponent.getReturnUrl(context)
}

val Payments.adyenPaymentMethodFactory: PaymentMethodFactory<Pair<PaymentMethodDetails, Boolean>> by lazyInit {
  AdyenPaymentMethodFactory(
    adyenRepository = AdyenV2RepositoryImpl(microServicesRestClient),
    returnUrl = returnUrl
  )
}
