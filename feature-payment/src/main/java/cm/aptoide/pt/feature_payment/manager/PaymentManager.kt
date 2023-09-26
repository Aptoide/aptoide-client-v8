package cm.aptoide.pt.feature_payment.manager

import android.net.Uri
import cm.aptoide.pt.feature_payment.parser.OSPUriParser
import cm.aptoide.pt.feature_payment.payment.PaymentMethod
import cm.aptoide.pt.feature_payment.payment.UnimplementedPaymentMethod
import cm.aptoide.pt.feature_payment.repository.broker.BrokerRepository
import cm.aptoide.pt.feature_payment.repository.product.ProductRepository
import cm.aptoide.pt.feature_payment.wallet.WalletProvider
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentManagerImpl @Inject constructor(
  private val onStepPaymentParser: OSPUriParser,
  private val productRepository: ProductRepository,
  private val walletProvider: WalletProvider,
  private val brokerRepository: BrokerRepository,
) : PaymentManager {

  private val cachedPaymentMethods = HashMap<String, PaymentMethod>()
  override suspend fun getPaymentMethod(name: String): PaymentMethod? =
    cachedPaymentMethods[name]

  override suspend fun loadPaymentMethods(uri: Uri): List<PaymentMethod> {
    val ospUri = onStepPaymentParser.parseUri(uri)

    val productInfo = productRepository.getProductInfo(
      name = ospUri.domain,
      sku = ospUri.product,
      currency = ospUri.currency,
      country = Locale.getDefault().country
    )

    val wallet = walletProvider.getWallet()

    val paymentMethods = brokerRepository.getPaymentMethods(
      domain = ospUri.domain,
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

  suspend fun loadPaymentMethods(uri: Uri): List<PaymentMethod>
}
