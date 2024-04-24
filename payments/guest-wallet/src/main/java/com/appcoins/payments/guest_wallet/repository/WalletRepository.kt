package com.appcoins.payments.guest_wallet.repository

import com.appcoins.payments.arch.WalletData
import com.appcoins.payments.arch.jsonToWalletData
import com.appcoins.payments.guest_wallet.repository.exception.InvalidWalletException
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.get

internal class WalletRepositoryImpl(
  private val restClient: RestClient,
) : WalletRepository {

  @Throws(InvalidWalletException::class)
  override suspend fun getWallet(id: String): WalletData = restClient.get(
    path = "appc/guest_wallet",
    query = mapOf("id" to id)
  )?.jsonToWalletData()!!
}

interface WalletRepository {
  suspend fun getWallet(id: String): WalletData
}
