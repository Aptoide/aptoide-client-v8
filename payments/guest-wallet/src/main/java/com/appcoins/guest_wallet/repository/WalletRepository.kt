package com.appcoins.guest_wallet.repository

import com.appcoins.guest_wallet.repository.exception.InvalidWalletException
import com.appcoins.guest_wallet.repository.model.WalletResponse
import com.appcoins.payments.arch.WalletData
import com.appcoins.payments.network.RestClient
import com.appcoins.payments.network.get
import com.google.gson.Gson

internal class WalletRepositoryImpl(
  private val restClient: RestClient,
) : WalletRepository {

  @Throws(InvalidWalletException::class)
  override suspend fun getWallet(id: String): WalletData {
    val response = restClient.get(
      path = "appc/guest_wallet",
      query = mapOf("id" to id)
    )?.let { Gson().fromJson(it, WalletResponse::class.java) }!!
    return WalletData(
      address = response.address ?: throw InvalidWalletException("Invalid wallet address"),
      ewt = response.ewt ?: throw InvalidWalletException("Invalid wallet ewt"),
      signature = response.signature ?: throw InvalidWalletException("Invalid wallet signature")
    )
  }
}

interface WalletRepository {
  suspend fun getWallet(id: String): WalletData
}
