package cm.aptoide.pt.play_and_earn.exchange.data.model

import androidx.annotation.Keep

@Keep
internal data class ExchangeResponseJson(
  val detail: ExchangeDetailJson?,
  val status: String?
) {
  fun isSuccess() = status == "success"
}

@Keep
internal data class ExchangeDetailJson(
  val message: String?,
  val status: String?
)
