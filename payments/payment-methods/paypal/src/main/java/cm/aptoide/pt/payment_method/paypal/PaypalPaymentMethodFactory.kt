package cm.aptoide.pt.payment_method.paypal

import cm.aptoide.pt.payment_manager.manager.PurchaseRequest
import cm.aptoide.pt.payment_manager.payment.PaymentMethodFactory
import cm.aptoide.pt.payment_manager.repository.broker.domain.PaymentMethodData
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.wallet.WalletData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaypalPaymentMethodFactory @Inject internal constructor() : PaymentMethodFactory<Unit> {

  private companion object {
    private const val PAYPAL = "paypal"
  }

  override fun create(
    wallet: WalletData,
    developerWallet: String,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaypalPaymentMethod? {
    if (paymentMethodData.id != PAYPAL) return null

    return PaypalPaymentMethod(
      id = paymentMethodData.id,
      label = paymentMethodData.label,
      iconUrl = paymentMethodData.iconUrl,
      available = paymentMethodData.available,
      productInfo = productInfo,
      developerWallet = developerWallet,
      wallet = wallet,
      purchaseRequest = purchaseRequest,
    )
  }
}
