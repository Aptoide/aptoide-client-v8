package com.appcoins.payments.arch

data class ProductInfoData(
  val sku: String,
  val title: String,
  val description: String?,
  val priceValue: String,
  val priceCurrency: String,
  val priceInDollars: String
)
