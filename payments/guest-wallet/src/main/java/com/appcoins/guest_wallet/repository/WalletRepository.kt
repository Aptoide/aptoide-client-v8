package com.appcoins.guest_wallet.repository

import com.appcoins.guest_wallet.repository.exception.InvalidWalletException
import com.appcoins.guest_wallet.repository.model.WalletResponse
import com.appcoins.payments.arch.WalletData
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepositoryImpl @Inject constructor(
  private val walletApi: WalletApi,
) : WalletRepository {

  @Throws(InvalidWalletException::class)
  override suspend fun getWallet(id: String): WalletData {
    val response = walletApi.getWallet(id)
    return WalletData(
      address = response.address ?: throw InvalidWalletException("Invalid wallet address"),
      ewt = response.ewt ?: throw InvalidWalletException("Invalid wallet ewt"),
      signature = response.signature ?: throw InvalidWalletException("Invalid wallet signature")
    )
  }

  interface WalletApi {

    @GET("appc/guest_wallet")
    suspend fun getWallet(@Query("id") id: String): WalletResponse
  }
}

interface WalletRepository {

  suspend fun getWallet(id: String): WalletData
}
