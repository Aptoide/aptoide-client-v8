package cm.aptoide.pt.payment_manager.repository.broker.domain

data class PaymentMethods(
  val items: List<PaymentMethodData>,
)

data class PaymentMethodData(
  val id: String,
  val label: String,
  val iconUrl: String,
  val available: Boolean,
)
