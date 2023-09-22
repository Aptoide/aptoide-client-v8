package cm.aptoide.pt.feature_payment.wallet

import cm.aptoide.pt.feature_payment.wallet.domain.WalletData

interface WalletProvider {

  /**
   * Gets wallet from BE.
   */
  suspend fun getWallet(): WalletData
}
