package cm.aptoide.pt.wallet.gamification.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal open class PromotionsResponse(
  val id: String,
  val priority: Int,
  @SerializedName("gamification_status") val gamificationStatus: GamificationStatus?,
) {
  enum class Status {
    ACTIVE,
    INACTIVE
  }
}
