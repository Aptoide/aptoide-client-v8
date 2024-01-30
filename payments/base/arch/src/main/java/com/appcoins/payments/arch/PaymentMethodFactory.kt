package com.appcoins.payments.arch

interface PaymentMethodFactory<T> {
  suspend fun create(
    wallet: WalletData,
    developerWallet: String,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaymentMethod<out T>?
}
