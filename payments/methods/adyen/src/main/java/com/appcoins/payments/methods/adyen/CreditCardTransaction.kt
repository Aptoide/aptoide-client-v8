package com.appcoins.payments.methods.adyen

import com.appcoins.payments.arch.Transaction
import com.appcoins.payments.arch.Transaction.Companion.RETRY_DELAY
import com.appcoins.payments.arch.TransactionStatus
import com.appcoins.payments.arch.TransactionStatus.FAILED
import com.appcoins.payments.arch.TransactionStatus.PENDING_USER_PAYMENT
import com.appcoins.payments.arch.WalletData
import com.appcoins.payments.methods.adyen.repository.AdyenV2Repository
import com.appcoins.payments.methods.adyen.repository.model.PaymentResponse
import com.appcoins.payments.network.HttpException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

class CreditCardTransaction internal constructor(
  private var currentStatus: TransactionStatus,
  private var _paymentResponse: PaymentResponse?,
  override val uid: String,
  private val walletData: WalletData,
  private val adyenRepository: AdyenV2Repository,
) : Transaction {

  val paymentResponse: PaymentResponse?
    get() = this._paymentResponse

  private val channel = Channel<TransactionStatus>()

  override val status: Flow<TransactionStatus> = waitForTransactionSuccess()

  private fun waitForTransactionSuccess(): Flow<TransactionStatus> = flow {
    emit(currentStatus)

    while (!isEndingState(currentStatus)) {
      if (currentStatus == PENDING_USER_PAYMENT) {
        val status = channel.receive()
        currentStatus = status
        emit(status)
      } else {
        delay(RETRY_DELAY)

        try {
          val result = adyenRepository.getCreditCardTransaction(
            uId = uid,
            walletAddress = walletData.address,
            walletSignature = walletData.signature
          ).status

          currentStatus = result
        } catch (exception: Throwable) {
          if (exception is HttpException && exception.code in 400..599) {
            currentStatus = FAILED
          }
        }
        emit(currentStatus)
      }
    }
  }

  suspend fun submitActionResponse(
    paymentData: String?,
    paymentDetails: JSONObject?,
  ) {
    adyenRepository.submitActionResult(
      ewt = walletData.ewt,
      uid = uid,
      walletAddress = walletData.address,
      paymentData = paymentData,
      paymentDetails = paymentDetails
    )
      .let {
        _paymentResponse = it.payment
        channel.trySend(it.status)
      }
  }
}
