package cm.aptoide.pt.play_and_earn.exchange.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class ExchangeResponseJson(
  val detail: ExchangeDetailJson?,
  val status: String?,
  // v2 (exchangeUnitsV2) signals success by returning a transaction id; v1 used status == "success".
  @SerializedName("transaction_id") val transactionId: String?
) {
  fun isSuccess() = status == "success" || !transactionId.isNullOrBlank()
}

@Keep
internal data class ExchangeDetailJson(
  val message: String?,
  val status: String?
)
