package com.appcoins.payments.network

interface RestClientInjectParams {
  fun getUserAgent(): String
  val channel: String
}
