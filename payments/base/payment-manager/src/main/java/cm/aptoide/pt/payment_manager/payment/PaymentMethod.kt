package cm.aptoide.pt.payment_manager.payment

import android.net.Uri
import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.wallet.domain.WalletData

interface PaymentMethod<T> {
  val id: String
  val label: String
  val iconUrl: String
  val available: Boolean
  val wallet: WalletData
  val productInfo: ProductInfoData
  val purchaseRequest: PurchaseRequest

  suspend fun createTransaction(paymentDetails: T): Transaction
}

val emptyPaymentMethod = object : PaymentMethod<String> {
  override val id = "PaymentMethodData id"
  override val label = "PaymentMethodData label"
  override val iconUrl = "PaymentMethodData icon url"
  override val available = true
  override val wallet = WalletData(
    address = "wallet address",
    ewt = "wallet ewt",
    signature = "wallet signature"
  )
  override val productInfo = ProductInfoData(
    sku = "ProductInfoData sku",
    title = "ProductInfoData title",
    description = "ProductInfoData description",
    priceValue = "ProductInfoData price value",
    priceCurrency = ""
  )
  override val purchaseRequest = PurchaseRequest(
    scheme = "PurchaseRequest scheme",
    host = "PurchaseRequest host",
    path = "PurchaseRequest path",
    product = "PurchaseRequest product",
    domain = "PurchaseRequest domain",
    callbackUrl = "PurchaseRequest callback url",
    orderReference = "PurchaseRequest order reference",
    signature = "PurchaseRequest signature",
    value = 1.0,
    currency = "PurchaseRequest currency",
    oemId = "PurchaseRequest oemid",
    ospUri = Uri.EMPTY
  )

  override suspend fun createTransaction(paymentDetails: String): Transaction {
    TODO("Don't need to be implemented")
  }
}
