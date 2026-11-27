package cm.aptoide.pt.play_and_earn.exchange.data.model

import androidx.annotation.Keep
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import com.google.gson.annotations.SerializedName

@Keep
internal data class ExchangeUnitsRequestJson(
  val email: String,
  @SerializedName("redeem_type") val redeemType: RedeemType,
  // AND-952: the backend answers a repeated id once, so a retry never redeems twice.
  @SerializedName("request_id") val requestId: String,
)
