package com.appcoins.payments.di

import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.uri_handler.PaymentScreenContentProvider
import com.appcoins.uri_handler.handler.UriHandler
import com.appcoins.uri_handler.handler.UriHandlerImpl

val PaymentsInitializer.uriHandler: UriHandler by lazy {
  UriHandlerImpl(
    oemIdExtractor = PaymentsInitializer.oemIdExtractor,
    oemPackageExtractor = PaymentsInitializer.oemPackageExtractor,
  )
}

var PaymentsInitializer.paymentScreenContentProvider: PaymentScreenContentProvider
  get() = psContentProvider
  set(value) {
    psContentProvider = value
  }
private lateinit var psContentProvider: PaymentScreenContentProvider
