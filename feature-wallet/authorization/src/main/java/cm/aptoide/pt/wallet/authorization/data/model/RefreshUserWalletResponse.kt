package cm.aptoide.pt.wallet.authorization.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RefreshUserWalletResponse(
  val address: String,
  @SerializedName("auth_token") val authToken: String,
  @SerializedName("refresh_token") val refreshToken: String
)
