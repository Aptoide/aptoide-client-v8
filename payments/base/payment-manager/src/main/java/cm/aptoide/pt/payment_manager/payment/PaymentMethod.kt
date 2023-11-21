package cm.aptoide.pt.payment_manager.payment

import android.net.Uri
import cm.aptoide.pt.payment_manager.manager.PurchaseRequest
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.transaction.Transaction
import cm.aptoide.pt.payment_manager.wallet.WalletData

interface PaymentMethod<T> {
  val id: String
  val label: String
  val iconUrl: String
  val available: Boolean
  val developerWallet: String
  val wallet: WalletData
  val productInfo: ProductInfoData
  val purchaseRequest: PurchaseRequest

  suspend fun createTransaction(
    paymentDetails: T,
    storePaymentMethod: Boolean = false,
  ): Transaction
}

val emptyProductInfo = ProductInfoData(
  sku = "Sku",
  title = "Title",
  description = "Description",
  priceValue = "Price Value",
  priceCurrency = "Price Currency",
  priceInDollars = "Price In Dollars"
)

val emptyPurchaseRequest = PurchaseRequest(
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
  oemPackage = "PurchaseRequest oempackage",
  ospUri = Uri.EMPTY,
  metadata = "metadata",
  to = "To",
  productToken = "Product Token",
  skills = false
)

val emptyProductInfoData = ProductInfoData(
  sku = "ProductInfoData sku",
  title = "ProductInfoData title",
  description = "ProductInfoData description",
  priceValue = "ProductInfoData price value",
  priceCurrency = "",
  priceInDollars = "ProductInfoData price value in dollars"
)

val emptyWalletData = WalletData(
  address = "wallet address",
  ewt = "wallet ewt",
  signature = "wallet signature"
)

val emptyPaymentMethod = object : PaymentMethod<String> {
  override val id = "PaymentMethodData id"
  override val label = "PaymentMethodData label"
  override val iconUrl = "PaymentMethodData icon url"
  override val available = true
  override val developerWallet = "Developer Wallet"
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
