package com.appcoins.payment_manager.manager

import com.appcoins.payment_manager.repository.broker.BrokerRepository
import com.appcoins.payment_manager.repository.developer_wallet.DeveloperWalletRepository
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.PaymentMethodFactory
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.WalletProvider
import com.appcoins.product_inventory.ProductInventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentManagerImpl @Inject constructor(
  private val developerWalletRepository: DeveloperWalletRepository,
  private val productInventoryRepository: ProductInventoryRepository,
  private val walletProvider: WalletProvider,
  private val brokerRepository: BrokerRepository,
  private val paymentMethodFactory: PaymentMethodFactory<*>,
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

    val productInfo = productInventoryRepository.getProductInfo(
      name = purchaseRequest.domain,
      sku = purchaseRequest.product,
    ).also { _productInfo.emit(it) }

    val wallet = walletProvider.getWallet()

    val developerWallet = developerWalletRepository.getDeveloperWallet(purchaseRequest.domain)

    val paymentMethods = brokerRepository.getPaymentMethods(
      domain = purchaseRequest.domain,
      priceCurrency = productInfo.priceCurrency,
      priceValue = productInfo.priceValue
    ).items.mapNotNull { paymentMethodData ->
      paymentMethodFactory.create(
        productInfo = productInfo,
        developerWallet = developerWallet,
        wallet = wallet,
        paymentMethodData = paymentMethodData,
        purchaseRequest = purchaseRequest
      )
    }
    cachedPaymentMethods.putAll(paymentMethods.associateBy { it.id })

    return paymentMethods
  }
}

interface PaymentManager {
  val productInfo: Flow<ProductInfoData?>

  fun getPaymentMethod(name: String): PaymentMethod<*>?

  suspend fun loadPaymentMethods(purchaseRequest: PurchaseRequest): List<PaymentMethod<*>>
}
