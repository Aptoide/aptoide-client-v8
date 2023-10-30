package cm.aptoide.pt.payment_method.adyen.repository.model

data class ResponseErrorBody(
  val code: String?,
  val path: String?,
  val text: String?,
  val data: Any?,
)
