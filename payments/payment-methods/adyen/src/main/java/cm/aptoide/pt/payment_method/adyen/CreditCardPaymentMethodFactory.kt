package cm.aptoide.pt.payment_method.adyen

import cm.aptoide.pt.payment_manager.manager.PurchaseRequest
import cm.aptoide.pt.payment_manager.payment.PaymentMethod
import cm.aptoide.pt.payment_manager.payment.PaymentMethodFactory
import cm.aptoide.pt.payment_manager.repository.broker.domain.PaymentMethodData
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.wallet.WalletData
import cm.aptoide.pt.payment_method.adyen.repository.AdyenV2Repository
import com.adyen.checkout.components.model.payments.request.PaymentMethodDetails
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditCardPaymentMethodFactory @Inject internal constructor(
  private val adyenRepository: AdyenV2Repository,
) : PaymentMethodFactory<Pair<String, PaymentMethodDetails>> {

  private companion object {
    private const val CREDIT_CARD = "credit_card"
  }

  override fun create(
    wallet: WalletData,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaymentMethod<Pair<String, PaymentMethodDetails>>? {
    if (paymentMethodData.id != CREDIT_CARD) return null

    return CreditCardPaymentMethod(
      id = paymentMethodData.id,
      label = paymentMethodData.label,
      iconUrl = paymentMethodData.iconUrl,
      available = paymentMethodData.available,
      productInfo = productInfo,
      wallet = wallet,
      purchaseRequest = purchaseRequest,
      adyenRepository = adyenRepository
    )
  }
}
