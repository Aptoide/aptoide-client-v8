package com.appcoins.payments.arch

import kotlinx.coroutines.flow.Flow

interface PaymentManager {
  val productInfo: Flow<ProductInfoData?>

  fun getPaymentMethod(name: String): PaymentMethod<*>?

  suspend fun loadPaymentMethods(purchaseRequest: PurchaseRequest): List<PaymentMethod<*>>
}
