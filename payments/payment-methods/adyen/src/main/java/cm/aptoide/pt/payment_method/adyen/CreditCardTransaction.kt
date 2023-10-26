package cm.aptoide.pt.payment_method.adyen

import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.transaction.TransactionStatus
import cm.aptoide.pt.payment_manager.wallet.domain.WalletData
import cm.aptoide.pt.payment_method.adyen.repository.AdyenV2Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class CreditCardTransaction internal constructor(
  initialStatus: TransactionStatus,
  private val walletData: WalletData,
  private val adyenRepository: AdyenV2Repository,
) : Transaction {

  private val _status = MutableStateFlow(initialStatus)

  override val status: Flow<TransactionStatus> = _status
}
