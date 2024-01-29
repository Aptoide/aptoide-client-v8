package com.appcoins.payment_method.adyen

import com.adyen.checkout.components.model.payments.request.PaymentMethodDetails
import com.appcoins.payment_manager.manager.PurchaseRequest
import com.appcoins.payment_manager.payment.PaymentMethod
import com.appcoins.payment_manager.payment.PaymentMethodFactory
import com.appcoins.payment_manager.repository.broker.domain.PaymentMethodData
import com.appcoins.payment_manager.wallet.WalletData
import com.appcoins.payment_method.adyen.repository.AdyenV2Repository
import com.appcoins.product_inventory.domain.ProductInfoData
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
    developerWallet: String,
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
      developerWallet = developerWallet,
      wallet = wallet,
      purchaseRequest = purchaseRequest,
      adyenRepository = adyenRepository
    )
  }
}
