package com.appcoins.payment_method.adyen

import com.adyen.checkout.components.model.payments.request.PaymentMethodDetails
import com.appcoins.payment_method.adyen.repository.AdyenV2Repository
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.PaymentMethodData
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.WalletData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditCardPaymentMethodFactory @Inject internal constructor(
  private val adyenRepository: AdyenV2Repository,
) : PaymentMethodFactory<Pair<String, PaymentMethodDetails>> {

  private companion object {
    private const val CREDIT_CARD = "credit_card"
  }

  override suspend fun create(
    wallet: WalletData,
    developerWallet: String,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaymentMethod<out Pair<String, PaymentMethodDetails>>? {
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
