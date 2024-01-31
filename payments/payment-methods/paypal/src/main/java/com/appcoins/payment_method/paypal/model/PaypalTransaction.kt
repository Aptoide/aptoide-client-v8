package com.appcoins.payment_method.paypal.model

import com.appcoins.payments.arch.Transaction
import com.appcoins.payments.arch.TransactionStatus
import kotlinx.coroutines.flow.Flow

class PaypalTransaction internal constructor(
  override val uid: String,
  override val status: Flow<TransactionStatus>,
) : Transaction
