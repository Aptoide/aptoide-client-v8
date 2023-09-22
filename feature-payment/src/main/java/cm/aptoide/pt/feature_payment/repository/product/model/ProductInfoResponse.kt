package cm.aptoide.pt.feature_payment.repository.product.model

import androidx.annotation.Keep

@Keep
data class ProductInfoResponse(
  val sku: String,
  val title: String,
  val description: String?,
  val price: SkuPriceJSON,
)

@Keep
data class SkuPriceJSON(
  val currency: String,
  val value: String,
  val label: String,
  val symbol: String,
  val micros: Long,
  val vat: SkuVatJSON?,
  val appc: AppcJSON,
)

@Keep
data class SkuVatJSON(
  val tax: String,
  val value: String,
  val label: String,
  val micros: Long,
  val origin: ORIGIN,
  val policy: POLICY,
  val country: String,
)

@Keep
data class AppcJSON(
  val value: String,
  val label: String,
  val micros: Long,
  val vat: VatJSON?
)

@Keep
data class VatJSON(
  val value: String,
  val label: String,
  val micros: Long,
)

@Keep
enum class ORIGIN {
  BUYER,
  SELLER
}

@Keep
enum class POLICY {
  AUTOMATIC,
  SELF_MANAGED
}
