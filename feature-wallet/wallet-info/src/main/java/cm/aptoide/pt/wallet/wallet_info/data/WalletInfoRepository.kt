package cm.aptoide.pt.wallet.wallet_info.data

import cm.aptoide.pt.appcoins.domain.WalletInfo

interface WalletInfoRepository {
  suspend fun getWalletInfo(
    wallet: String
  ) : WalletInfo
}
