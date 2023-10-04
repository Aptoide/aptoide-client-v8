package cm.aptoide.pt.payment_manager.payment.credit_card

import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import cm.aptoide.pt.payment_manager.payment.PaymentMethod
import cm.aptoide.pt.payment_manager.payment.model.PaymentDetails
import cm.aptoide.pt.payment_manager.repository.broker.domain.PaymentMethodData
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.wallet.domain.WalletData
import org.json.JSONObject

class CreditCardPaymentMethod(
  private val wallet: WalletData,
  private val productInfo: ProductInfoData,
  private val purchaseRequest: PurchaseRequest,
) : PaymentMethod<PaymentDetails> {

  override fun createTransaction(paymentDetails: PaymentDetails): Transaction {
    TODO("Not yet implemented")
  }

}
