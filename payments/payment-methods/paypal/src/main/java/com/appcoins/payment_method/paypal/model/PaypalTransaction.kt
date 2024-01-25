package com.appcoins.payment_method.paypal.model

import com.appcoins.payment_manager.transaction.Transaction
import com.appcoins.payment_manager.transaction.TransactionStatus
import kotlinx.coroutines.flow.Flow

class PaypalTransaction internal constructor(
  override val uid: String,
  override val status: Flow<TransactionStatus>,
) : Transaction
