package com.appcoins.payment_method.adyen

import com.appcoins.payment_method.adyen.repository.AdyenV2Repository
import com.appcoins.payment_method.adyen.repository.model.PaymentResponse
import com.appcoins.payments.arch.Transaction
import com.appcoins.payments.arch.TransactionStatus
import com.appcoins.payments.arch.WalletData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject

class CreditCardTransaction internal constructor(
  initialStatus: TransactionStatus,
  private var _paymentResponse: PaymentResponse?,
  override val uid: String,
  private val walletData: WalletData,
  private val adyenRepository: AdyenV2Repository,
) : Transaction {

  val paymentResponse: PaymentResponse?
    get() = this._paymentResponse

  private val _status = MutableStateFlow(initialStatus)

  override val status: Flow<TransactionStatus> = _status

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
        _status.emit(it.status)
      }
  }
}
