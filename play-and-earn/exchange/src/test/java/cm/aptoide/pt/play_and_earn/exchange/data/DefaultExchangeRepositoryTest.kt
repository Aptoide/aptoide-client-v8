package cm.aptoide.pt.play_and_earn.exchange.data

import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeRateJson
import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeResponseJson
import cm.aptoide.pt.play_and_earn.exchange.data.model.ExchangeUnitsRequestJson
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import cm.aptoide.pt.test.gherkin.coScenario
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// AND-952: the redeem carries the id that lets the backend answer a repeated request once.
internal class DefaultExchangeRepositoryTest {

  private class RecordingExchangeApi : ExchangeApi {
    val requests = mutableListOf<ExchangeUnitsRequestJson>()
    var failure: Throwable? = null

    override suspend fun exchangeUnitsV2(request: ExchangeUnitsRequestJson): ExchangeResponseJson {
      requests += request
      failure?.let { throw it }
      return ExchangeResponseJson(detail = null, status = null, transactionId = "tx-1")
    }

    override suspend fun getExchangeRate(countryCode: String): ExchangeRateJson =
      throw UnsupportedOperationException()
  }

  private val api = RecordingExchangeApi()
  private val repository = DefaultExchangeRepository(api, Dispatchers.Unconfined)

  @Test
  fun `The redeem request carries its request id`() = coScenario {
    m Given "a redeem with its own request id"
    val requestId = "0b6c3e5e-2f7a-4c55-9d3e-2a1f4b8c9d10"

    m When "the units are exchanged"
    val result = repository.exchangeUnits("user@example.com", RedeemType.ROBLOX_ROBUX, requestId)

    m Then "the API gets the id with the email and the type"
    assertTrue(result.isSuccess)
    assertEquals(
      listOf(ExchangeUnitsRequestJson("user@example.com", RedeemType.ROBLOX_ROBUX, requestId)),
      api.requests
    )
  }

  @Test
  fun `The request id is sent as request_id`() = coScenario {
    m Given "a redeem request"
    val request = ExchangeUnitsRequestJson("user@example.com", RedeemType.FREE_FIRE_DIAMOND, "id-1")

    m When "it is written as JSON"
    val json = Gson().toJson(request)

    m Then "the id is under the key the backend reads"
    assertEquals(
      """{"email":"user@example.com","redeem_type":"FREE_FIRE_DIAMOND","request_id":"id-1"}""",
      json
    )
  }

  @Test
  fun `A cancelled redeem stays cancelled`() = coScenario {
    m Given "the redeem's coroutine being cancelled during the call"
    api.failure = CancellationException("screen left")

    m When "the units are exchanged"
    val thrown = runCatching {
      repository.exchangeUnits("user@example.com", RedeemType.ROBLOX_ROBUX, "id-1")
    }.exceptionOrNull()

    m Then "the cancellation goes up instead of becoming a failed redeem"
    assertTrue(thrown is CancellationException)
  }
}
