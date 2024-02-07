package com.appcoins.payments.arch

interface PaymentMethod<T> {
  val id: String
  val label: String
  val iconUrl: String
  val available: Boolean
  val wallet: WalletData
  val productInfo: ProductInfoData
  val purchaseRequest: PurchaseRequest

  suspend fun createTransaction(
    paymentDetails: T,
    storePaymentMethod: Boolean = false,
  ): Transaction
}

val emptyPaymentMethod = object : PaymentMethod<String> {
  override val id = "PaymentMethodData id"
  override val label = "PaymentMethodData label"
  override val iconUrl = "PaymentMethodData icon url"
  override val available = true
  override val wallet = emptyWalletData
  override val productInfo = emptyProductInfoData
  override val purchaseRequest = emptyPurchaseRequest

  override suspend fun createTransaction(
    paymentDetails: String,
    storePaymentMethod: Boolean,
  ): Transaction {
    TODO("Don't need to be implemented")
  }
}
