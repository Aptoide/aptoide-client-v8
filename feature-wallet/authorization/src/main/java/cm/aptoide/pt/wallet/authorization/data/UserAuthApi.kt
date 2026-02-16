package cm.aptoide.pt.wallet.authorization.data

import cm.aptoide.pt.wallet.authorization.data.model.UserAuthData
import cm.aptoide.pt.wallet.authorization.data.model.UserAuthorizationResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface UserAuthApi {
  @POST("user/authorize")
  suspend fun authorizeUser(
    @Body userAuthData: UserAuthData,
  ): UserAuthorizationResponse
}
