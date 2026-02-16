package cm.aptoide.pt.wallet.authorization.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class UserAuthorizationResponse(
  val type: String,
  val state: String?,
  val data: AuthorizationResponseData
)

@Keep
internal data class AuthorizationResponseData(
  val address: String,
  @SerializedName(value = "auth_token") val authToken: String,
  @SerializedName(value = "refresh_token") val refreshToken: String,
  val email: String?
)
