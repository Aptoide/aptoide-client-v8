package com.appcoins.payments.methods.paypal

import com.appcoins.payments.arch.PaymentMethodData
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.WalletData
import com.appcoins.payments.methods.paypal.repository.PaypalRepository

internal class PaypalPaymentMethodFactory(
  private val repository: PaypalRepository,
) : PaymentMethodFactory<Unit> {

  override val knownIds: Set<String> = setOf("paypal_v2")

  override suspend fun create(
    wallet: WalletData,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ) = when (paymentMethodData.id) {
    "paypal_v2" ->
      PaypalPaymentMethod(
        id = paymentMethodData.id,
        label = paymentMethodData.label,
        iconUrl = paymentMethodData.iconUrl,
        available = paymentMethodData.available,
        productInfo = productInfo,
        wallet = wallet,
        purchaseRequest = purchaseRequest,
        paypalRepository = repository,
      )

    else -> null
  }
}
