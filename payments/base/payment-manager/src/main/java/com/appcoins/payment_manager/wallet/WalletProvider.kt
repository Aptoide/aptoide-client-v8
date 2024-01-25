package com.appcoins.payment_manager.wallet

interface WalletProvider {

  /**
   * Gets wallet from BE.
   */
  suspend fun getWallet(): WalletData
}
