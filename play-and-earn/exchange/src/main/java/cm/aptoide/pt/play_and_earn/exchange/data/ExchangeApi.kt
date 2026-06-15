package cm.aptoide.pt.play_and_earn.exchange.data

import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeRateJson
import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeResponseJson
import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeUnitsRequestJson
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface ExchangeApi {
  @Deprecated("Use exchangeUnitsV2 instead.")
  @POST("/api/exchange/")
  suspend fun exchangeUnits(
    @Query("country_code") countryCode: String
  ): ExchangeResponseJson

  @POST("/api/exchange/v2/")
  suspend fun exchangeUnitsV2(
    @Body request: ExchangeUnitsRequestJson
  ): ExchangeResponseJson

  @GET("api/exchange/rate/{country_code}")
  suspend fun getExchangeRate(
    @Path("country_code") countryCode: String
  ): ExchangeRateJson
}
