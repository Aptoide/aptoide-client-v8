package cm.aptoide.pt.payment_method.paypal.repository.model

import androidx.annotation.Keep
import cm.aptoide.pt.payment_manager.transaction.TransactionStatus

@Keep
internal data class TransactionResponse(
  val uid: String,
  val hash: String?,
  val status: TransactionStatus,
  val data: ErrorData?,
)

@Keep
internal data class ErrorData(
  val name: String?,
  val message: String?,
)
