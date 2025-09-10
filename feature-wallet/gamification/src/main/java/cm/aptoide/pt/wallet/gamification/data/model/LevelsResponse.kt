package cm.aptoide.pt.wallet.gamification.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

@Keep
internal data class LevelsResponse(
  @SerializedName("result") val levels: List<LevelResponse>,
  val status: Status,
  @SerializedName("update_date") val updateDate: Date?
) {

  @Suppress("unused")
  enum class Status {
    ACTIVE,
    INACTIVE
  }
}

@Keep
internal data class LevelResponse(
  @SerializedName("amount_currency") val amount: BigDecimal,
  val bonus: Double,
  val level: Int
)
