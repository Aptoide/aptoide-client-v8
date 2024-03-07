package com.appcoins.billing.sdk.sku_details

import com.appcoins.payments.json.Json

@Json
data class SKU(
  val productId: String,
  val type: String,
  @Json("price") val priceBase: String,
  @Json("price_currency_code") val currencyBase: String,
  @Json("price_amount_micros") val amountBase: Long,
  @Json("appc_price") val priceAppc: String,
  @Json("appc_price_currency_code") val currencyAppc: String,
  @Json("appc_price_amount_micros") val amountAppc: Long,
  @Json("fiat_price") val priceFiat: String,
  @Json("fiat_price_currency_code") val currencyFiat: String,
  @Json("fiat_price_amount_micros") val amountFiat: Long,
  val title: String,
  val description: String?,
  @Json("subscription_period")
  val subscriptionPeriod: String?,
  @Json("trial_period")
  val trialPeriod: String?,
)
