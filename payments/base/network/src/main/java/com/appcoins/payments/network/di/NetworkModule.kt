package com.appcoins.payments.network.di

import com.appcoins.payments.arch.Environment.DEV
import com.appcoins.payments.arch.Environment.PROD
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.payments.network.RestClient

object NetworkModule {

  val backendRestClient by lazy {
    RestClient.with(
      baseUrl = when (PaymentsInitializer.environment) {
        DEV -> "https://apichain.dev.catappult.io/"
        PROD -> "https://apichain.catappult.io/"
      },
      getUserAgent = PaymentsInitializer.getUserAgent
    )
  }

  val microServicesRestClient by lazy {
    RestClient.with(
      baseUrl = when (PaymentsInitializer.environment) {
        DEV -> "https://api.dev.catappult.io/"
        PROD -> "https://api.catappult.io/"
      },
      getUserAgent = PaymentsInitializer.getUserAgent
    )
  }
}
