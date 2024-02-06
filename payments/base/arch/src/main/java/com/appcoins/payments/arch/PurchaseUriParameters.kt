package com.appcoins.payments.arch

const val PURCHASE_URI_OSP_SCHEME = "https"
const val PURCHASE_URI_SDK_SCHEME = "appcoins-sdk"
const val PURCHASE_URI_PATH = "/transaction"

object PurchaseUriParameters {

  const val PRODUCT = "product"
  const val DOMAIN = "domain"
  const val CALLBACK_URL = "callback_url"
  const val ORDER_REFERENCE = "order_reference"
  const val SIGNATURE = "signature"
  const val VALUE = "value"
  const val CURRENCY = "currency"
  const val METADATA = "data"
  const val PRODUCT_TOKEN = "product_token"
  const val SKILLS = "skills"
  const val TYPE = "type"
  const val ORIGIN = "origin"
}
