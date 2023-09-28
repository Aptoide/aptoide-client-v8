package cm.aptoide.pt.payment_manager.payment.credit_card

import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import cm.aptoide.pt.payment_manager.payment.PaymentMethod
import cm.aptoide.pt.payment_manager.payment.PaymentMethodFactory
import cm.aptoide.pt.payment_manager.payment.model.PaymentDetails
import cm.aptoide.pt.payment_manager.repository.broker.domain.PaymentMethodData
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.wallet.domain.WalletData

class CreditCardPaymentMethodFactory : PaymentMethodFactory<PaymentDetails> {

  private companion object {
    private const val CREDIT_CARD = "credit_card"
  }

  override fun create(
    wallet: WalletData,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaymentMethod<PaymentDetails>? {
    if (paymentMethodData.id != CREDIT_CARD) return null

    return CreditCardPaymentMethod(
      productInfo = productInfo,
      wallet = wallet,
      purchaseRequest = purchaseRequest,
      paymentMethodData = paymentMethodData
    )
  }
}
