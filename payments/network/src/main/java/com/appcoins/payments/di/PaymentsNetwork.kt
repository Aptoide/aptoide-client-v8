package com.appcoins.payments.di

import com.appcoins.payments.arch.Environment.DEV
import com.appcoins.payments.arch.Environment.PROD
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.RestClientInjectParams

var Payments.restClientInjectParams: RestClientInjectParams by lateInit()

val Payments.backendRestClient
  get() = RestClient.with(
    baseUrl = when (environment) {
      DEV -> "https://apichain.dev.catappult.io/"
      PROD -> "https://apichain.catappult.io/"
    },
    restClientInjectParams = restClientInjectParams,
    logger = logger
  )

val Payments.microServicesRestClient
  get() = RestClient.with(
    baseUrl = when (environment) {
      DEV -> "https://api.dev.catappult.io/"
      PROD -> "https://api.catappult.io/"
    },
    restClientInjectParams = restClientInjectParams,
    logger = logger
  )
