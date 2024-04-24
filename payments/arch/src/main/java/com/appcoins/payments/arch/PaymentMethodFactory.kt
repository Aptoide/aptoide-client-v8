package com.appcoins.payments.arch

interface PaymentMethodFactory<T> {
  val knownIds get() = setOf<String>()

  suspend fun create(
    wallet: WalletData,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaymentMethod<out T>?
}
