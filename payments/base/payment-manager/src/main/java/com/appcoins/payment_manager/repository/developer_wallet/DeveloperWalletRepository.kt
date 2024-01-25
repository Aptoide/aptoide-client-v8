package com.appcoins.payment_manager.repository.developer_wallet

import com.appcoins.payment_manager.repository.developer_wallet.model.GetWalletResponse
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DeveloperWalletRepositoryImpl @Inject constructor(
  private val walletDeveloperApi: DeveloperWalletApi,
) : DeveloperWalletRepository {

  override suspend fun getDeveloperWallet(packageName: String): String {
    val productInfo = walletDeveloperApi.getWallet(packageName)

    return productInfo.data.address
  }

  internal interface DeveloperWalletApi {

    @GET("api/7/bds/apks/package/getOwnerWallet")
    suspend fun getWallet(@Query("package_name") packageName: String): GetWalletResponse
  }
}

interface DeveloperWalletRepository {

  suspend fun getDeveloperWallet(packageName: String): String
}
