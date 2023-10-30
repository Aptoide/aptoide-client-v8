package cm.aptoide.pt.payment_method.adyen

import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.transaction.TransactionStatus
import cm.aptoide.pt.payment_manager.wallet.WalletData
import cm.aptoide.pt.payment_method.adyen.repository.AdyenV2Repository
import cm.aptoide.pt.payment_method.adyen.repository.model.PaymentResponse
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
