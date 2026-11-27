package cm.aptoide.pt.play_and_earn.exchange.presentation

import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeRepository
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.GetExchangeRateUseCase
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// AND-950: a refusal on the exchange rate is logged, not reported. The redeem's own tests are
// with its view model, in :app-games.
@OptIn(ExperimentalCoroutinesApi::class)
internal class ExchangeRateViewModelRefusalTest {

  private class FakeExchangeRepository : ExchangeRepository {
    var failure: Throwable = IOException("offline")

    override suspend fun exchangeUnits(email: String, redeemType: RedeemType, requestId: String) =
      Result.failure<Unit>(failure)

    override suspend fun getExchangeRate() = Result.failure<ExchangeRate>(failure)
  }

  private class RecordingExceptionHandler : ExceptionHandler {
    val recorded = mutableListOf<Throwable>()
    override fun recordException(exception: Throwable) {
      recorded += exception
    }
  }

  private val repository = FakeExchangeRepository()
  private val exceptionHandler = RecordingExceptionHandler()

  @BeforeEach
  fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

  @AfterEach
  fun tearDown() = Dispatchers.resetMain()

  @Test
  fun `A refused exchange rate is not reported`() = coScenario {
    m Given "the backend refusing the exchange rate for the user's region"
    repository.failure = httpError(403, "REGION_NOT_SUPPORTED")

    m When "the exchange rate is read"
    val viewModel = ExchangeRateViewModel(GetExchangeRateUseCase(repository), exceptionHandler)

    m Then "nothing goes to Crashlytics and there is no rate, as before"
    assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
    assertNull(viewModel.exchangeRate.value)
  }

  @Test
  fun `A failed exchange rate is reported`() = coScenario {
    m Given "a server error on the exchange rate"
    val failure = httpError(500, "REGION_NOT_SUPPORTED")
    repository.failure = failure

    m When "the exchange rate is read"
    val viewModel = ExchangeRateViewModel(GetExchangeRateUseCase(repository), exceptionHandler)

    m Then "it goes to Crashlytics as before, and there is no rate"
    assertEquals(listOf<Throwable>(failure), exceptionHandler.recorded)
    assertNull(viewModel.exchangeRate.value)
  }

  private fun httpError(status: Int, code: String) = HttpException(
    Response.error<Any>(
      status,
      """{"detail":{"code":"$code"}}""".toResponseBody("application/json".toMediaType())
    )
  )
}
