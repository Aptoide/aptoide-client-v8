package cm.aptoide.pt.payment_manager.wallet

import cm.aptoide.pt.payment_manager.wallet.domain.WalletData

interface WalletProvider {

  /**
   * Gets wallet from BE.
   */
  suspend fun getWallet(): WalletData
}
