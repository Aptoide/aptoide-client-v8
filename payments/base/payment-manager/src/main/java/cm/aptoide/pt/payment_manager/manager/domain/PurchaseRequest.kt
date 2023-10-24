package cm.aptoide.pt.payment_manager.manager.domain

data class PurchaseRequest(
  val scheme: String,
  val host: String,
  val path: String,
  val product: String?,
  val domain: String,
  val callbackUrl: String?,
  val orderReference: String?,
  val signature: String?,
  val value: Double?,
  val currency: String?,
  val oemId: String?,
)
