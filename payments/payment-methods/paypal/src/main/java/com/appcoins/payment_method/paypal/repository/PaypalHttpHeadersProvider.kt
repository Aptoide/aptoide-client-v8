package com.appcoins.payment_method.paypal.repository

import android.content.Context

interface PaypalHttpHeadersProvider {
  fun init(context: Context)
  fun getMetadataId(): String
}
