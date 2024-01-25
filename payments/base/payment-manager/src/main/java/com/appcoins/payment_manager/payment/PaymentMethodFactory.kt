package com.appcoins.payment_manager.payment

import com.appcoins.payment_manager.manager.PurchaseRequest
import com.appcoins.payment_manager.repository.broker.domain.PaymentMethodData
import com.appcoins.payment_manager.repository.product.domain.ProductInfoData
import com.appcoins.payment_manager.wallet.WalletData

interface PaymentMethodFactory<T> {
  fun create(
    wallet: WalletData,
    developerWallet: String,
    productInfo: ProductInfoData,
    paymentMethodData: PaymentMethodData,
    purchaseRequest: PurchaseRequest,
  ): PaymentMethod<T>?
}
