package com.appcoins.payments.methods.paypal.repository

interface PaypalHttpHeadersProvider {
  fun getMetadataId(): String
}
