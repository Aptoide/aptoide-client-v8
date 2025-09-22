package cm.aptoide.pt.wallet.authorization.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class UserAuthData(
  @SerializedName(value = "credential") val code: String,
  val type: UserAuthType = UserAuthType.GOOGLE,
  val supported: UserAuthResponseType = UserAuthResponseType.WALLETJWT,
  val accepted: List<String> = listOf("TOS", "PRIVACY", "DISTRIBUTION"),
  val domain: String,
  val channel: String = "ANDROID",
  val consents: List<String> = listOf("email")
)

@Keep
internal enum class UserAuthType {
  GOOGLE
}

@Keep
internal enum class UserAuthResponseType {
  WALLETJWT
}
