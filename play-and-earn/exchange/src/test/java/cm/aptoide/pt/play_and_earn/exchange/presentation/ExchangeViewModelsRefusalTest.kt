package cm.aptoide.pt.play_and_earn.exchange.presentation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.aptoide.pt.appcoins.domain.FiatValue
import cm.aptoide.pt.appcoins.domain.WalletInfo
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeRepository
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeUnitsUseCase
import cm.aptoide.pt.play_and_earn.exchange.domain.GetExchangeRateUseCase
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import cm.aptoide.pt.test.gherkin.coScenario
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import cm.aptoide.pt.wallet.wallet_info.data.WalletInfoRepository
import cm.aptoide.pt.wallet.wallet_info.domain.GetWalletUnitsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.math.BigDecimal

// AND-950: a refusal on a redeem or the exchange rate is logged, not reported; the screen shows
// the same error as before either way (its own messages are AND-952).
@OptIn(ExperimentalCoroutinesApi::class)
internal class ExchangeViewModelsRefusalTest {

  private class FakeExchangeRepository : ExchangeRepository {
    var failure: Throwable = IOException("offline")

    @Deprecated("Use exchangeUnits(email, redeemType) instead.")
    override suspend fun exchangeUnits(): Result<Unit> = Result.failure(failure)

    override suspend fun exchangeUnits(email: String, redeemType: RedeemType) =
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

  private val walletDataStore = object : DataStore<Preferences> {
    override val data: Flow<Preferences> =
      flowOf(preferencesOf(stringPreferencesKey("current_wallet_address") to "0xwallet"))

    override suspend fun updateData(
      transform: suspend (t: Preferences) -> Preferences
    ): Preferences = throw UnsupportedOperationException()
  }

  private val walletInfoRepository = object : WalletInfoRepository {
    override suspend fun getWalletInfo(wallet: String) = WalletInfo(
      wallet = wallet,
      walletBalance = FiatValue(BigDecimal.ZERO, "USD"),
      unitsBalance = 500L,
      blocked = false,
      verified = true,
    )
  }

  @BeforeEach
  fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

  @AfterEach
  fun tearDown() = Dispatchers.resetMain()

  @Test
  fun `A refused exchange is not reported and shows the same error`() = coScenario {
    m Given "the backend refusing the exchange with a redeem limit"
    val refusal = httpError(429, "REDEEM_LIMIT")
    repository.failure = refusal

    m When "the user exchanges their units"
    val state = exchange()

    m Then "nothing goes to Crashlytics and the error is the one shown before"
    assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
    assertTrue(state is ExchangeUiState.Error)
    assertEquals(refusal.message, (state as ExchangeUiState.Error).message)
  }

  @Test
  fun `A failed exchange is reported and shows the same error`() = coScenario {
    m Given "the exchange failing on the network"
    val failure = IOException("offline")
    repository.failure = failure

    m When "the user exchanges their units"
    val state = exchange()

    m Then "it goes to Crashlytics as before, with the same error"
    assertEquals(listOf<Throwable>(failure), exceptionHandler.recorded)
    assertEquals("offline", (state as ExchangeUiState.Error).message)
  }

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

  private fun exchange(): ExchangeUiState {
    val viewModel = ExchangeViewModel(
      exchangeUnitsUseCase = ExchangeUnitsUseCase(repository),
      getWalletUnitsUseCase = GetWalletUnitsUseCase(
        walletInfoRepository,
        WalletCoreDataSource(walletDataStore)
      ),
      exceptionHandler = exceptionHandler,
    )
    (viewModel.uiState.value as ExchangeUiState.Idle).exchangeUnits()
    return viewModel.uiState.value
  }

  private fun httpError(status: Int, code: String) = HttpException(
    Response.error<Any>(
      status,
      """{"detail":{"code":"$code"}}""".toResponseBody("application/json".toMediaType())
    )
  )
}
