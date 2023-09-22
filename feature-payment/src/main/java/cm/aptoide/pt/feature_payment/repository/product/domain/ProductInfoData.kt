package cm.aptoide.pt.feature_payment.repository.product.domain

data class ProductInfoData(
  val sku: String,
  val title: String,
  val description: String?,
  val priceValue: String,
  val priceCurrency: String,
)
