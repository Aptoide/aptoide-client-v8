package cm.aptoide.pt.payment_manager.transaction

import androidx.annotation.Keep
import kotlinx.coroutines.flow.Flow

interface Transaction {
  val status: Flow<TransactionStatus>
}

@Keep
enum class TransactionStatus {
  PENDING,
  PENDING_SERVICE_AUTHORIZATION,
  SETTLED,
  PROCESSING,
  COMPLETED,
  PENDING_USER_PAYMENT,
  INVALID_TRANSACTION,
  FAILED,
  CANCELED,
  DUPLICATED,
  CHARGEBACK,
  REFUNDED,
  FRAUD,
  PENDING_VALIDATION,
  PENDING_CODE,
  VERIFIED,
  EXPIRED
}
