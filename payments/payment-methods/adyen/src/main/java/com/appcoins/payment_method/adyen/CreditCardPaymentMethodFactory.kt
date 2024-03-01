package com.appcoins.payment_method.adyen

import com.adyen.checkout.components.model.payments.request.PaymentMethodDetails
import com.appcoins.payment_method.adyen.repository.AdyenV2Repository
import com.appcoins.payments.arch.PaymentMethodData
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.WalletData

internal class CreditCardPaymentMethodFactory(
  private val adyenRepository: AdyenV2Repository,
) : PaymentMethodFactory<Pair<String, PaymentMethodDetails>> {

  override val id = "credit_card"

  override suspend fun create(
    wallet: WalletData,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ) = CreditCardPaymentMethod(
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
