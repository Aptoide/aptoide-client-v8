package com.appcoins.payments.arch

import com.appcoins.payments.json.Json
import kotlinx.coroutines.flow.Flow

interface Transaction {
  companion object {
    const val RETRY_DELAY = 5 * 1000L
  }

  val uid: String
  val status: Flow<TransactionStatus>

  fun isEndingState(status: TransactionStatus): Boolean {
    return (status == TransactionStatus.COMPLETED
      || status == TransactionStatus.FAILED
      || status == TransactionStatus.CANCELED
      || status == TransactionStatus.INVALID_TRANSACTION
      || status == TransactionStatus.FRAUD)
  }
}

@Json
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
