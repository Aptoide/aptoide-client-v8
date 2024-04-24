package com.appcoins.payments.di

import com.appcoins.payments.arch.Environment.DEV
import com.appcoins.payments.arch.Environment.PROD
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.methods.paypal.PaypalPaymentMethodFactory
import com.appcoins.payments.methods.paypal.repository.PaypalHttpHeadersProviderImpl
import com.appcoins.payments.methods.paypal.repository.PaypalRepositoryImpl
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
