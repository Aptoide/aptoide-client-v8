package com.appcoins.payments.methods.paypal.repository.model

import com.appcoins.payments.json.Json

@Json
internal data class PaymentDetailsRequest(
  @Json("callback_url") val callbackUrl: String?,
  @Json("domain") val domain: String?,
  @Json("metadata") val metadata: String?,
  @Json("origin") val origin: String?,
  @Json("product") val sku: String?,
  @Json("reference") val reference: String?,
  @Json("type") val type: String?,
  @Json("price.currency") val currency: String?,
  @Json("price.value") val value: String?,
  @Json("entity.oemid") val entityOemId: String?,
  @Json("entity.domain") val entityDomain: String?,
  @Json("entity.promo_code") val entityPromoCode: String?,
  @Json("wallets.user") val user: String?,
  @Json("referrer_url") val referrerUrl: String?,
)
