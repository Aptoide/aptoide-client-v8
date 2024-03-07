package com.appcoins.product_inventory.model

import com.appcoins.payments.json.Json

@Json
data class ConsumablesResponse(val items: List<ProductInfoResponse>)

@Json
data class ProductInfoResponse(
  val sku: String,
  val title: String,
  val description: String?,
  val price: SkuPriceJSON,
)

@Json
data class SkuPriceJSON(
  val currency: String,
  val value: String,
  val label: String,
  val symbol: String,
  val micros: Long,
  val vat: SkuVatJSON?,
  val appc: AppcJSON,
  val usd: UsdJSON,
)

@Json
data class SkuVatJSON(
  val tax: String,
  val value: String,
  val label: String,
  val micros: Long,
  val origin: ORIGIN,
  val policy: POLICY,
  val country: String,
)

@Json
data class AppcJSON(
  val value: String,
  val label: String,
  val micros: Long,
  val vat: VatJSON?,
)

@Json
data class VatJSON(
  val value: String,
  val label: String,
  val micros: Long,
)

@Json
data class UsdJSON(
  val value: String,
  val label: String,
  val micros: Long,
  val vat: VatJSON?,
)

@Json
enum class ORIGIN {
  BUYER,
  SELLER
}

@Json
enum class POLICY {
  AUTOMATIC,
  SELF_MANAGED
}
