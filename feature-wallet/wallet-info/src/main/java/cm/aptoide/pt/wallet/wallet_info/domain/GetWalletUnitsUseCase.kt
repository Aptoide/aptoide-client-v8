package cm.aptoide.pt.wallet.wallet_info.domain

import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import cm.aptoide.pt.wallet.wallet_info.data.WalletInfoRepository
import javax.inject.Inject

class GetWalletUnitsUseCase @Inject constructor(
  private val walletInfoRepository: WalletInfoRepository,
  private val walletCoreDataSource: WalletCoreDataSource
) {

  suspend operator fun invoke(): Long? {
    val walletAddress = walletCoreDataSource.getCurrentWalletAddress()
    return walletAddress?.let { address ->
      walletInfoRepository.getWalletInfo(address).unitsBalance
    }
  }
}
