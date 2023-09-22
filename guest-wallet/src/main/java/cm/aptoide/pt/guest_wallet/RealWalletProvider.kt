package cm.aptoide.pt.guest_wallet

import cm.aptoide.pt.feature_payment.wallet.WalletProvider
import cm.aptoide.pt.feature_payment.wallet.domain.WalletData
import cm.aptoide.pt.guest_wallet.repository.wallet.WalletRepository
import cm.aptoide.pt.guest_wallet.unique_id.UniqueIDProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RealWalletProvider @Inject constructor(
  private val uniqueIDProvider: UniqueIDProvider,
  private val walletRepository: WalletRepository,
) : WalletProvider {

  override suspend fun getWallet(): WalletData {
    val uniqueId = uniqueIDProvider.getUniqueId()
    return walletRepository.getWallet(uniqueId)
  }
}
