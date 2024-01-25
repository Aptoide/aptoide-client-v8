package com.appcoins.payment_manager.repository.product.domain

data class ProductInfoData(
  val sku: String,
  val title: String,
  val description: String?,
  val priceValue: String,
  val priceCurrency: String,
  val priceInDollars: String
)
