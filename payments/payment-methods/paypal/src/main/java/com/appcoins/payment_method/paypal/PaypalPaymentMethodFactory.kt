package com.appcoins.payment_method.paypal

import com.appcoins.payment_method.paypal.repository.PaypalRepository
import com.appcoins.payments.arch.PaymentMethodData
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.WalletData

internal class PaypalPaymentMethodFactory(
  private val repository: PaypalRepository,
) : PaymentMethodFactory<Unit> {

  override val id = "paypal_v2"

  override suspend fun create(
    wallet: WalletData,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ) = PaypalPaymentMethod(
    id = paymentMethodData.id,
    label = paymentMethodData.label,
    iconUrl = paymentMethodData.iconUrl,
    available = paymentMethodData.available,
    productInfo = productInfo,
    wallet = wallet,
    purchaseRequest = purchaseRequest,
    paypalRepository = repository,
  )
}
