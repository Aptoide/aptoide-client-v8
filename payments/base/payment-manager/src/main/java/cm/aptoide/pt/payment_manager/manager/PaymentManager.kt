package cm.aptoide.pt.payment_manager.manager

import cm.aptoide.pt.payment_manager.payment.PaymentMethod
import cm.aptoide.pt.payment_manager.payment.PaymentMethodFactory
import cm.aptoide.pt.payment_manager.repository.broker.BrokerRepository
import cm.aptoide.pt.payment_manager.repository.developer_wallet.DeveloperWalletRepository
import cm.aptoide.pt.payment_manager.repository.product.ProductRepository
import cm.aptoide.pt.payment_manager.repository.product.domain.ProductInfoData
import cm.aptoide.pt.payment_manager.wallet.WalletProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentManagerImpl @Inject constructor(
  private val developerWalletRepository: DeveloperWalletRepository,
  private val productRepository: ProductRepository,
  private val walletProvider: WalletProvider,
  private val brokerRepository: BrokerRepository,
  private val paymentMethodFactory: Array<PaymentMethodFactory<*>>,
) : PaymentManager {

  private val cachedPaymentMethods = HashMap<String, PaymentMethod<*>>()

  override val productInfo: Flow<ProductInfoData?>
    get() = _productInfo

  private val _productInfo = MutableStateFlow<ProductInfoData?>(null)

  override fun getPaymentMethod(name: String): PaymentMethod<*>? =
    cachedPaymentMethods[name]

  override suspend fun loadPaymentMethods(purchaseRequest: PurchaseRequest): List<PaymentMethod<*>> {
    _productInfo.emit(null)
    cachedPaymentMethods.clear()

    val productInfo = productRepository.getProductInfo(
      name = purchaseRequest.domain,
      sku = purchaseRequest.product,
    ).also { _productInfo.emit(it) }

    val wallet = walletProvider.getOrCreateWallet()

    val developerWallet = developerWalletRepository.getDeveloperWallet(purchaseRequest.domain)

    val paymentMethods = brokerRepository.getPaymentMethods(
      domain = purchaseRequest.domain,
      priceCurrency = productInfo.priceCurrency,
      priceValue = productInfo.priceValue
    ).items.mapNotNull { paymentMethodData ->
      paymentMethodFactory.firstNotNullOfOrNull {
        it.create(
          productInfo = productInfo,
          developerWallet = developerWallet,
          wallet = wallet,
          paymentMethodData = paymentMethodData,
          purchaseRequest = purchaseRequest
        )
      }?.also { cachedPaymentMethods[paymentMethodData.id] = it }
    }

    return paymentMethods
  }
}

interface PaymentManager {
  val productInfo: Flow<ProductInfoData?>

  fun getPaymentMethod(name: String): PaymentMethod<*>?

  suspend fun loadPaymentMethods(purchaseRequest: PurchaseRequest): List<PaymentMethod<*>>
}
