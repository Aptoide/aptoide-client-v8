package cm.aptoide.pt.payment_manager.manager

import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import cm.aptoide.pt.payment_manager.payment.PaymentMethod
import cm.aptoide.pt.payment_manager.payment.UnimplementedPaymentMethod
import cm.aptoide.pt.payment_manager.repository.broker.BrokerRepository
import cm.aptoide.pt.payment_manager.repository.product.ProductRepository
import cm.aptoide.pt.payment_manager.wallet.WalletProvider
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentManagerImpl @Inject constructor(
  private val productRepository: ProductRepository,
  private val walletProvider: WalletProvider,
  private val brokerRepository: BrokerRepository,
) : PaymentManager {

  private val cachedPaymentMethods = HashMap<String, PaymentMethod>()
  override suspend fun getPaymentMethod(name: String): PaymentMethod? =
    cachedPaymentMethods[name]

  override suspend fun loadPaymentMethods(purchaseRequest: PurchaseRequest): List<PaymentMethod> {
    val productInfo = productRepository.getProductInfo(
      name = purchaseRequest.domain,
      sku = purchaseRequest.product,
      currency = purchaseRequest.currency,
      country = Locale.getDefault().country
    )

    val wallet = walletProvider.getWallet()

    val paymentMethods = brokerRepository.getPaymentMethods(
      domain = purchaseRequest.domain,
      priceCurrency = productInfo.priceCurrency,
      priceValue = productInfo.priceValue
    ).items.map { paymentMethodData ->
      UnimplementedPaymentMethod(
        productInfo = productInfo,
        wallet = wallet,
        paymentMethodData = paymentMethodData
      ).also { cachedPaymentMethods[paymentMethodData.id] = it }
    }

    return paymentMethods
  }
}

interface PaymentManager {
  suspend fun getPaymentMethod(name: String): PaymentMethod?

  suspend fun loadPaymentMethods(purchaseRequest: PurchaseRequest): List<PaymentMethod>
}
