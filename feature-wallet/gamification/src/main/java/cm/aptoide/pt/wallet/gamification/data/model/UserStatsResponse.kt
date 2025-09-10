package cm.aptoide.pt.wallet.gamification.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class UserStatsResponse(
  val promotions: List<GamificationResponse>,
  @SerializedName("wallet_origin") val walletOrigin: WalletOrigin
)

@Keep
internal enum class WalletOrigin {
  UNKNOWN,
  APTOIDE,
  PARTNER,
  PARTNER_NO_BONUS
}
