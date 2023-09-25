package cm.aptoide.pt.feature_payment.manager

import android.net.Uri
import cm.aptoide.pt.feature_payment.parser.OSPUriParser
import cm.aptoide.pt.feature_payment.repository.broker.BrokerRepository
import cm.aptoide.pt.feature_payment.repository.product.ProductRepository
import cm.aptoide.pt.feature_payment.wallet.WalletProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentManagerImpl @Inject constructor(
  private val onStepPaymentParser: OSPUriParser,
  private val productRepository: ProductRepository,
  private val walletProvider: WalletProvider,
  private val brokerRepository: BrokerRepository,
) : PaymentManager {

}

interface PaymentManager {
}
