package cm.aptoide.pt.payment_method.adyen.credit_card

import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import cm.aptoide.pt.payment_manager.payment.PaymentMethod
import cm.aptoide.pt.payment_manager.payment.model.PaymentDetails
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.wallet.domain.WalletData
import cm.aptoide.pt.payment_method.adyen.repository.AdyenV2Repository
import org.json.JSONObject

class CreditCardPaymentMethod internal constructor(
  override val id: String,
  override val label: String,
  override val iconUrl: String,
  override val available: Boolean,
  override val wallet: WalletData,
  override val productInfo: ProductInfoData,
  override val purchaseRequest: PurchaseRequest,
) : PaymentMethod<PaymentDetails> {

  suspend fun init(): JSONObject {
    val paymentMethodDetails = adyenRepository.getPaymentMethodDetails(
      walletAddress = wallet.address,
      priceValue = productInfo.priceValue,
      priceCurrency = productInfo.priceCurrency
    )

    return paymentMethodDetails.json
  }

  override fun createTransaction(paymentDetails: PaymentDetails): Transaction {
    TODO("Not yet implemented")
  }

}
