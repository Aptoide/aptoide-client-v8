package cm.aptoide.pt.payment_manager.payment

import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import cm.aptoide.pt.payment_manager.repository.broker.domain.PaymentMethodData
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.wallet.domain.WalletData

interface PaymentMethodFactory<T> {
  fun create(
    wallet: WalletData,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaymentMethod<T>?
}
