package cm.aptoide.pt.feature_payment.payment

import cm.aptoide.pt.feature_payment.payment.model.PaymentDetails
import cm.aptoide.pt.feature_payment.repository.broker.domain.PaymentMethodData
import cm.aptoide.pt.feature_payment.repository.product.domain.ProductInfoData
import cm.aptoide.pt.feature_payment.transaction.Transaction
import cm.aptoide.pt.feature_payment.wallet.domain.WalletData
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
