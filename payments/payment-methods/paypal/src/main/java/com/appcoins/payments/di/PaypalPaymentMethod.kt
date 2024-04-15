package com.appcoins.payments.di

import com.appcoins.payment_method.paypal.PaypalPaymentMethodFactory
import com.appcoins.payment_method.paypal.repository.PaypalHttpHeadersProviderImpl
import com.appcoins.payment_method.paypal.repository.PaypalRepositoryImpl
import com.appcoins.payments.arch.Environment.DEV
import com.appcoins.payments.arch.Environment.PROD
import com.appcoins.payments.arch.PaymentMethodFactory
import lib.android.paypal.com.magnessdk.Environment.LIVE
import lib.android.paypal.com.magnessdk.Environment.SANDBOX

val Payments.paypalPaymentMethodFactory: PaymentMethodFactory<Unit> by lazyInit {
  PaypalPaymentMethodFactory(
    repository = PaypalRepositoryImpl(
      restClient = microServicesRestClient,
      paypalHttpHeaderProvider = PaypalHttpHeadersProviderImpl(
        context = context,
        magnesEnvironment = when (environment) {
          DEV -> SANDBOX
          PROD -> LIVE
        }
      )
    )
  )
}
