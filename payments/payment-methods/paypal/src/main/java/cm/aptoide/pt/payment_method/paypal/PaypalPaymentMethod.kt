package cm.aptoide.pt.payment_method.paypal

import cm.aptoide.pt.payment_manager.manager.PurchaseRequest
import cm.aptoide.pt.payment_manager.payment.PaymentMethod
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.wallet.WalletData

class PaypalPaymentMethod internal constructor(
  override val id: String,
  override val label: String,
  override val iconUrl: String,
  override val available: Boolean,
  override val developerWallet: String,
  override val wallet: WalletData,
  override val productInfo: ProductInfoData,
  override val purchaseRequest: PurchaseRequest
) : PaymentMethod<Unit> {

  override suspend fun createTransaction(
    paymentDetails: Unit,
    storePaymentMethod: Boolean,
  ): Transaction {
    TODO("Not yet implemented")
  }
}
