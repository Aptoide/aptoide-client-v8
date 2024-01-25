package com.appcoins.payment_method.paypal

import com.appcoins.payment_manager.manager.PurchaseRequest
import com.appcoins.payment_manager.payment.PaymentMethodFactory
import com.appcoins.payment_manager.repository.broker.domain.PaymentMethodData
import com.appcoins.payment_manager.repository.product.domain.ProductInfoData
import com.appcoins.payment_manager.wallet.WalletData
import com.appcoins.payment_method.paypal.repository.PaypalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaypalPaymentMethodFactory @Inject internal constructor(
  private val repository: PaypalRepository,
) : PaymentMethodFactory<Unit> {

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
      paypalRepository = repository,
    )
  }
}
