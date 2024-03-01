package com.appcoins.payment_method.paypal.di

import com.appcoins.payment_method.paypal.PaypalPaymentMethodFactory
import com.appcoins.payment_method.paypal.repository.PaypalHttpHeadersProviderImpl
import com.appcoins.payment_method.paypal.repository.PaypalRepositoryImpl
import com.appcoins.payments.arch.Environment.DEV
import com.appcoins.payments.arch.Environment.PROD
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.payments.network.di.NetworkModule

object PayPalModule {

  val paypalPaymentMethodFactory: PaymentMethodFactory<Unit> by lazy {
    PaypalPaymentMethodFactory(
      repository = PaypalRepositoryImpl(
        restClient = NetworkModule.microServicesRestClient,
        paypalHttpHeaderProvider = PaypalHttpHeadersProviderImpl(
          context = PaymentsInitializer.context,
          magnesEnvironment = when (PaymentsInitializer.environment) {
            DEV -> lib.android.paypal.com.magnessdk.Environment.SANDBOX
            PROD -> lib.android.paypal.com.magnessdk.Environment.LIVE
          }
        )
      )
    )
  }
}
