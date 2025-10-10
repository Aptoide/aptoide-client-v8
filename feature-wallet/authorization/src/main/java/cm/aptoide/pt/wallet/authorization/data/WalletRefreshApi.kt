package cm.aptoide.pt.wallet.authorization.data

import cm.aptoide.pt.wallet.authorization.data.model.RefreshUserWalletResponse
import retrofit2.http.GET
import retrofit2.http.Header

internal interface WalletRefreshApi {

  @GET("/appc/user_wallet")
  suspend fun refreshUserWallet(
    @Header("Authorization") token: String
  ): RefreshUserWalletResponse
}
