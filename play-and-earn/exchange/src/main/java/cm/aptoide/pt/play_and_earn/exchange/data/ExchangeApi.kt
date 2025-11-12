package cm.aptoide.pt.play_and_earn.exchange.data

import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeResponseJson
import retrofit2.http.POST
import retrofit2.http.Query

internal interface ExchangeApi {
  @POST("/api/exchange/")
  suspend fun exchangeUnits(
    @Query("country_code") countryCode: String
  ): ExchangeResponseJson
}
