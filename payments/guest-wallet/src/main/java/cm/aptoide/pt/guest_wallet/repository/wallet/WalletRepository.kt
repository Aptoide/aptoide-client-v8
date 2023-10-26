package cm.aptoide.pt.guest_wallet.repository.wallet

import cm.aptoide.pt.guest_wallet.repository.wallet.exception.InvalidWalletException
import cm.aptoide.pt.guest_wallet.repository.wallet.model.WalletResponse
import cm.aptoide.pt.payment_manager.wallet.WalletData
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
