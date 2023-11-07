package cm.aptoide.pt.payment_method.adyen.repository.model

import androidx.annotation.Keep

@Keep
data class ResponseErrorBody(
  val code: String?,
  val path: String?,
  val text: String?,
  val data: Any?,
)
