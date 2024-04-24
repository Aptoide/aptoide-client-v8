package com.appcoins.payments.arch

interface WalletProvider {

  /**
   * Get the wallet data, creating one if necessary.
   *
   * @returns [WalletData] of existing or newly created wallet
   */
  suspend fun getOrCreateWallet(): WalletData

  /**
   * Get the wallet data.
   *
   * @returns [WalletData] if exists or null otherwise
   */
  suspend fun getWallet(): WalletData?
}
