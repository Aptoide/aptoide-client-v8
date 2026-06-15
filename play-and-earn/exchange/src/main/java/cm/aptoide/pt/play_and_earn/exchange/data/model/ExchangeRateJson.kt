package cm.aptoide.pt.play_and_earn.exchange.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class ExchangeRateJson(
  @SerializedName("country_code") val countryCode: String?,
  val rate: Double?,
  @SerializedName("credits_amount") val creditsAmount: Int?
)
