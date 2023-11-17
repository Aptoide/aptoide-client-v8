package cm.aptoide.pt.payment_method.paypal.model

import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.transaction.TransactionStatus
import kotlinx.coroutines.flow.Flow

class PaypalTransaction internal constructor(
  override val uid: String,
  override val status: Flow<TransactionStatus>,
) : Transaction
