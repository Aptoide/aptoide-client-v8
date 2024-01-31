package com.appcoins.payments.arch

interface WalletProvider {

  /**
   * Gets wallet from BE.
   */
  suspend fun getWallet(): WalletData
}
