package com.appcoins.payments.arch

interface PaymentMethodFactory<T> {
  fun create(
    wallet: WalletData,
    developerWallet: String,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaymentMethod<T>?
}
