package cm.aptoide.pt.payment_manager.payment

import cm.aptoide.pt.payment_manager.payment.model.PaymentDetails
import cm.aptoide.pt.payment_manager.repository.broker.domain.PaymentMethodData
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.wallet.domain.WalletData
import org.json.JSONObject

class UnimplementedPaymentMethod(
  private val wallet: WalletData,
  private val productInfo: ProductInfoData,
  private val paymentMethodData: PaymentMethodData
) : PaymentMethod {

  override fun init(): JSONObject {
    TODO("Not yet implemented")
  }

  override fun createTransaction(paymentDetails: PaymentDetails): Transaction {
    TODO("Not yet implemented")
  }

}

interface PaymentMethod {

  fun init() : JSONObject
  fun createTransaction(paymentDetails: PaymentDetails) : Transaction
}
