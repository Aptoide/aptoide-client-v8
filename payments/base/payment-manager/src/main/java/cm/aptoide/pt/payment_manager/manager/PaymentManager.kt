package cm.aptoide.pt.payment_manager.manager

import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import cm.aptoide.pt.payment_manager.payment.PaymentMethod
import cm.aptoide.pt.payment_manager.payment.PaymentMethodFactory
import cm.aptoide.pt.payment_manager.repository.broker.BrokerRepository
import cm.aptoide.pt.payment_manager.repository.product.ProductRepository
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.wallet.WalletProvider
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentManagerImpl @Inject constructor(
  private val productRepository: ProductRepository,
  private val walletProvider: WalletProvider,
  private val brokerRepository: BrokerRepository,
  private val paymentMethodFactory: Array<PaymentMethodFactory<*>>,
) : PaymentManager {

  private val cachedPaymentMethods = HashMap<String, PaymentMethod<*>>()
  override suspend fun getPaymentMethod(name: String): PaymentMethod<*>? =
    cachedPaymentMethods[name]

  override suspend fun loadPaymentMethods(purchaseRequest: PurchaseRequest): Pair<ProductInfoData, List<PaymentMethod<*>>> {
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
    ).items.mapNotNull { paymentMethodData ->
      paymentMethodFactory.firstNotNullOfOrNull {
        it.create(
          productInfo = productInfo,
          wallet = wallet,
          paymentMethodData = paymentMethodData,
          purchaseRequest = purchaseRequest
        )
      }?.also { cachedPaymentMethods[paymentMethodData.id] = it }
    }

    return productInfo to paymentMethods
  }
}

interface PaymentManager {
  suspend fun getPaymentMethod(name: String): PaymentMethod<*>?

  suspend fun loadPaymentMethods(purchaseRequest: PurchaseRequest): Pair<ProductInfoData, List<PaymentMethod<*>>>
}
