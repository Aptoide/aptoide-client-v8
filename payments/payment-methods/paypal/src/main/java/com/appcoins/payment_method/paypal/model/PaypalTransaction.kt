package com.appcoins.payment_method.paypal.model

import com.appcoins.payment_method.paypal.repository.PaypalRepository
import com.appcoins.payments.arch.Transaction
import com.appcoins.payments.arch.Transaction.Companion.RETRY_DELAY
import com.appcoins.payments.arch.TransactionStatus
import com.appcoins.payments.arch.WalletData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class PaypalTransaction internal constructor(
  override val uid: String,
  private var currentStatus: TransactionStatus,
  private val repository: PaypalRepository,
  private val wallet: WalletData,
) : Transaction {

  override val status = waitForTransactionSuccess()

  private fun waitForTransactionSuccess(): Flow<TransactionStatus> = flow {
    emit(currentStatus)

    while (!isEndingState(currentStatus)) {
      delay(RETRY_DELAY)

      try {
        val result = repository.getPaypalTransaction(
          uId = uid,
          walletAddress = wallet.address,
          walletSignature = wallet.signature
        ).status

        currentStatus = result
      } catch (exception: Throwable) {
        if (exception is HttpException && exception.code() in 400..599) {
          currentStatus = TransactionStatus.FAILED
        }
      }
      emit(currentStatus)
    }
  }
}
