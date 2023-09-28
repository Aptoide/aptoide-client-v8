package cm.aptoide.pt.payment_manager.payment.credit_card

import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import cm.aptoide.pt.payment_manager.payment.PaymentMethod
import cm.aptoide.pt.payment_manager.payment.model.PaymentDetails
import cm.aptoide.pt.payment_manager.repository.broker.domain.PaymentMethodData
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.wallet.domain.WalletData

class CreditCardPaymentMethod(
  private val wallet: WalletData,
  private val productInfo: ProductInfoData,
  private val purchaseRequest: PurchaseRequest,
  val paymentMethodData: PaymentMethodData
) : PaymentMethod<PaymentDetails> {

  override fun createTransaction(paymentDetails: PaymentDetails): Transaction {
    TODO("Not yet implemented")
  }

  override fun getProductInfo(): ProductInfoData {
   return productInfo
  }
}
