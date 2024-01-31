package com.appcoins.payment_method.paypal

import com.appcoins.payment_method.paypal.repository.PaypalRepository
import com.appcoins.payments.arch.PaymentMethodData
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.WalletData
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
