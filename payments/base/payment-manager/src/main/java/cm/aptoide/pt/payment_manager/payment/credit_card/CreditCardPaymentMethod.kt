package cm.aptoide.pt.payment_manager.payment.credit_card

import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import cm.aptoide.pt.payment_manager.payment.PaymentMethod
import cm.aptoide.pt.payment_manager.payment.model.PaymentDetails
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.wallet.domain.WalletData

class CreditCardPaymentMethod(
  override val id: String,
  override val label: String,
  override val iconUrl: String,
  override val available: Boolean,
  private val wallet: WalletData,
  private val productInfo: ProductInfoData,
  private val purchaseRequest: PurchaseRequest,
) : PaymentMethod<PaymentDetails> {

  override fun createTransaction(paymentDetails: PaymentDetails): Transaction {
    TODO("Not yet implemented")
  }
}
