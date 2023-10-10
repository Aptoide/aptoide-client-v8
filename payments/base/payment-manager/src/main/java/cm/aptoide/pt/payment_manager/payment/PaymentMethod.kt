package cm.aptoide.pt.payment_manager.payment

import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.wallet.domain.WalletData

interface PaymentMethod<T> {
  val id: String
  val label: String
  val iconUrl: String
  val available: Boolean
  val wallet: WalletData
  val productInfo: ProductInfoData
  val purchaseRequest: PurchaseRequest

  fun createTransaction(paymentDetails: T): Transaction
}
