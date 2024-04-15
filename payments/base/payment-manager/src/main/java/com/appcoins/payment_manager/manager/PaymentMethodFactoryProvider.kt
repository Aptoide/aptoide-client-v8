package com.appcoins.payment_manager.manager

import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.PaymentMethodData
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.WalletData

internal class PaymentMethodFactoryProvider(
  private val paymentMethodFactories: List<PaymentMethodFactory<out Any>>,
  private val getAllowedIds: GetAllowedIds,
) : PaymentMethodFactory<Any> {

  override suspend fun create(
    wallet: WalletData,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaymentMethod<out Any>? = paymentMethodFactories
    .takeIf { paymentMethodData.id in getAllowedIds() }
    ?.firstNotNullOfOrNull {
      it.create(
        productInfo = productInfo,
        wallet = wallet,
        paymentMethodData = paymentMethodData,
        purchaseRequest = purchaseRequest
      )
    }
}

