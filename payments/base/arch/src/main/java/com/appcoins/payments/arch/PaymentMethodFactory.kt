package com.appcoins.payments.arch

interface PaymentMethodFactory<T> {
  val id: String

  suspend fun create(
    wallet: WalletData,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaymentMethod<out T>?
}
