package com.appcoins.billing.sdk.sku_details

import com.google.gson.annotations.SerializedName

data class SKU(
  val productId: String,
  val type: String,
  @SerializedName("price") val priceBase: String,
  @SerializedName("price_currency_code") val currencyBase: String,
  @SerializedName("price_amount_micros") val amountBase: Long,
  @SerializedName("appc_price") val priceAppc: String,
  @SerializedName("appc_price_currency_code") val currencyAppc: String,
  @SerializedName("appc_price_amount_micros") val amountAppc: Long,
  @SerializedName("fiat_price") val priceFiat: String,
  @SerializedName("fiat_price_currency_code") val currencyFiat: String,
  @SerializedName("fiat_price_amount_micros") val amountFiat: Long,
  val title: String,
  val description: String?,
  @SerializedName("subscription_period")
  val subscriptionPeriod: String?,
  @SerializedName("trial_period")
  val trialPeriod: String?
)
