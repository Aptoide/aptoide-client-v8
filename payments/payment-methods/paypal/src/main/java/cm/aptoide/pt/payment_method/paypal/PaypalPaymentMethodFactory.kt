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

  override fun create(
    wallet: WalletData,
    developerWallet: String,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaypalPaymentMethod? {
    TODO("Not yet implemented")
  }
}
