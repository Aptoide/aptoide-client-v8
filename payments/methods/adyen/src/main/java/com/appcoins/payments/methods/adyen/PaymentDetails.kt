package com.appcoins.payments.methods.adyen

import com.appcoins.payments.json.Json
import org.json.JSONObject

@Json
data class PaymentDetails(
  @Json("payment.method") val adyenPaymentMethod: JSONObject,
  @Json("payment.store_method") val shouldStoreMethod: Boolean,
  @Json("payment.return_url") val returnUrl: String,
  @Json("payment.shopper_interaction") val shopperInteraction: String?,
  @Json("payment.billing_address")
  val billingAddress: BillingAddress?,
  @Json("callback_url") val callbackUrl: String?,
  val domain: String?,
  val metadata: String?,
  val method: String?,
  val origin: String?,
  @Json("product") val sku: String?,
  val reference: String?,
  val type: String?,
  @Json("price.currency") val currency: String?,
  @Json("price.value") val value: String?,
  @Json("entity.oemid") val entityOemId: String?,
  @Json("entity.domain") val entityDomain: String?,
  @Json("entity.promo_code") val entityPromoCode: String?,
  @Json("wallets.user") val user: String?,
  @Json("referrer_url") val referrerUrl: String?,
  @Json("channel") val channel: String?,
)

@Json
data class BillingAddress(
  val street: String,
  val city: String,
  val postalCode: String,
  val houseNumberOrName: String,
  val stateOrProvince: String,
  val country: String,
)
