package cm.aptoide.pt.wallet.wallet_info.data

import cm.aptoide.pt.wallet.wallet_info.data.model.WalletInfoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface WalletInfoApi {
  @GET("/transaction/1.20230807/wallet/{address}/info")
  suspend fun getWalletInfo(
    @Path("address") address: String,
    @Query("currency") currency: String?
  ): WalletInfoResponse
}
