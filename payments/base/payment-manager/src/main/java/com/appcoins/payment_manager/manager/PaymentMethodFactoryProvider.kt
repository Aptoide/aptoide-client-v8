package com.appcoins.payment_manager.manager

import com.appcoins.payment_method.adyen.di.AdyenModule
import com.appcoins.payment_method.paypal.di.PayPalModule
import com.appcoins.payments.arch.GetAllowedIds
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.PaymentMethodData
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.arch.PaymentsInitializer
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.WalletData

internal object PaymentMethodFactoryProvider : PaymentMethodFactory<Any> {

  override val id = ""

  private val paymentMethodFactories by lazy {
    listOf(
      PayPalModule.paypalPaymentMethodFactory,
      AdyenModule.creditCardPaymentMethodFactory
    ).associateBy { it.id }
  }

  private val getAllowedIds: GetAllowedIds by lazy {
    PaymentsInitializer.getAllowedIds ?: object : GetAllowedIds {
      override suspend fun invoke(): Set<String> = paymentMethodFactories.keys
    }
  }

  override suspend fun create(
    wallet: WalletData,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaymentMethod<out Any>? = paymentMethodData.id
    .takeIf { it in getAllowedIds() }
    ?.let(paymentMethodFactories::get)
    ?.create(
      productInfo = productInfo,
      wallet = wallet,
      paymentMethodData = paymentMethodData,
      purchaseRequest = purchaseRequest
    )
}

