package com.appcoins.payments.guest_wallet

import com.appcoins.payments.arch.WalletData
import com.appcoins.payments.arch.WalletProvider
import com.appcoins.payments.guest_wallet.repository.WalletRepository
import com.appcoins.payments.guest_wallet.unique_id.UniqueIDProvider

internal class RealWalletProvider(
  private val uniqueIDProvider: UniqueIDProvider,
  private val walletRepository: WalletRepository,
) : WalletProvider {

  override suspend fun getOrCreateWallet(): WalletData =
    (uniqueIDProvider.getUniqueId() ?: uniqueIDProvider.createUniqueId())
      .let { walletRepository.getWallet(it) }

  override suspend fun getWallet(): WalletData? = uniqueIDProvider.getUniqueId()
    ?.let { walletRepository.getWallet(it) }
}
