package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import androidx.lifecycle.SavedStateHandle
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeRepository
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeUnitsUseCase
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// AND-952: one request id per redeem, kept until the backend's answer is final, so a retry
// after a lost answer never redeems twice.
@OptIn(ExperimentalCoroutinesApi::class)
internal class ExchangeByEmailViewModelTest {

  private data class Call(val email: String, val redeemType: RedeemType, val requestId: String)

  private class FakeExchangeRepository : ExchangeRepository {
    val calls = mutableListOf<Call>()
    val results = ArrayDeque<Result<Unit>>()
    var pending: CompletableDeferred<Result<Unit>>? = null

    @Deprecated("Use exchangeUnits(email, redeemType, requestId) instead.")
    override suspend fun exchangeUnits(): Result<Unit> = throw UnsupportedOperationException()

    override suspend fun exchangeUnits(
      email: String,
      redeemType: RedeemType,
      requestId: String,
    ): Result<Unit> {
      calls += Call(email, redeemType, requestId)
      return pending?.await() ?: results.removeFirstOrNull() ?: Result.success(Unit)
    }

    override suspend fun getExchangeRate() =
      Result.failure<ExchangeRate>(UnsupportedOperationException())
  }

  private class RecordingExceptionHandler : ExceptionHandler {
    val recorded = mutableListOf<Throwable>()
    override fun recordException(exception: Throwable) {
      recorded += exception
    }
  }

  private val repository = FakeExchangeRepository()
  private val exceptionHandler = RecordingExceptionHandler()
  private val savedStateHandle = SavedStateHandle()
  private var idCount = 0

  @BeforeEach
  fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

  @AfterEach
  fun tearDown() = Dispatchers.resetMain()

  @Test
  fun `Retry after a network error reuses the request id`() = coScenario {
    m Given "a redeem whose answer was lost on the network"
    val viewModel = viewModel()
    repository.results += Result.failure(IOException("offline"))
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)

    m When "the user retries"
    viewModel.retry()

    m Then "the same request is sent with the same id"
    assertEquals(listOf("id-1", "id-1"), repository.calls.map { it.requestId })
    assertEquals(ExchangeEmailUiState.Success, viewModel.uiState.value)
  }

  @Test
  fun `Submitting the same redeem again after a failure reuses the request id`() = coScenario {
    m Given "a redeem that failed on the network"
    val viewModel = viewModel()
    repository.results += Result.failure(IOException("offline"))
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)

    m When "the user submits the same email and reward again"
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)

    m Then "the id is the same"
    assertEquals(listOf("id-1", "id-1"), repository.calls.map { it.requestId })
  }

  @Test
  fun `A redeem after a success gets a new request id`() = coScenario {
    m Given "a redeem that succeeded"
    val viewModel = viewModel()
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)
    viewModel.resetState()

    m When "the user redeems again"
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)

    m Then "the second redeem has its own id"
    assertEquals(listOf("id-1", "id-2"), repository.calls.map { it.requestId })
  }

  @Test
  fun `Changing the email gets a new request id`() = coScenario {
    m Given "a redeem that failed on the network"
    val viewModel = viewModel()
    repository.results += Result.failure(IOException("offline"))
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)

    m When "the user submits another email"
    viewModel.exchange("other@example.com", PaERewardType.ROBUX)

    m Then "the id is new: the old one belongs to the old email"
    assertEquals(listOf("id-1", "id-2"), repository.calls.map { it.requestId })
  }

  @Test
  fun `Changing the reward gets a new request id`() = coScenario {
    m Given "a redeem that failed on the network"
    val viewModel = viewModel()
    repository.results += Result.failure(IOException("offline"))
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)

    m When "the user redeems another reward with the same email"
    viewModel.exchange(EMAIL, PaERewardType.DIAMONDS)

    m Then "the id is new"
    assertEquals(listOf("id-1", "id-2"), repository.calls.map { it.requestId })
    assertEquals(RedeemType.FREE_FIRE_DIAMOND, repository.calls.last().redeemType)
  }

  @Test
  fun `A request mismatch gets a new request id`() = coScenario {
    m Given "the backend saying the id was used with other data"
    val viewModel = viewModel()
    repository.results += Result.failure(httpError(409, "REDEEM_REQUEST_MISMATCH"))
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)

    m When "the user retries"
    viewModel.retry()

    m Then "the retry, and only it, is sent with a new id"
    assertEquals(listOf("id-1", "id-2"), repository.calls.map { it.requestId })
  }

  @Test
  fun `A redeem limit gets a new request id`() = coScenario {
    m Given "the backend refusing the redeem until tomorrow"
    val viewModel = viewModel()
    repository.results += Result.failure(httpError(429, "REDEEM_LIMIT"))
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)

    m When "the user redeems again later"
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)

    m Then "the refused redeem's id is not reused"
    assertEquals(listOf("id-1", "id-2"), repository.calls.map { it.requestId })
  }

  @Test
  fun `Too many attempts and a restricted account get a new request id`() = coScenario {
    m Given "the backend refusing for too many attempts, then for a restricted account"
    val viewModel = viewModel()
    repository.results += Result.failure(httpError(429, "TOO_MANY_ATTEMPTS"))
    repository.results += Result.failure(httpError(403, "ACCOUNT_RESTRICTED"))

    m When "the user redeems three times"
    repeat(3) { viewModel.exchange(EMAIL, PaERewardType.ROBUX) }

    m Then "each redeem after a refusal has a new id"
    assertEquals(listOf("id-1", "id-2", "id-3"), repository.calls.map { it.requestId })
  }

  @Test
  fun `An unknown outcome, a redeem in progress or under review keep the request id`() =
    coScenario {
      m Given "the backend not knowing the outcome, then busy, then reviewing the redeem"
      val viewModel = viewModel()
      repository.results += Result.failure(httpError(504, "REDEEM_OUTCOME_UNKNOWN"))
      repository.results += Result.failure(httpError(409, "REDEEM_IN_PROGRESS"))
      repository.results += Result.failure(httpError(409, "REDEEM_UNDER_REVIEW"))

      m When "the user redeems the same reward four times"
      repeat(4) { viewModel.exchange(EMAIL, PaERewardType.ROBUX) }

      m Then "every request carries the first id"
      assertEquals(List(4) { "id-1" }, repository.calls.map { it.requestId })
    }

  @Test
  fun `The request id survives a recreated view model`() = coScenario {
    m Given "a redeem that failed on the network"
    repository.results += Result.failure(IOException("offline"))
    viewModel().exchange(EMAIL, PaERewardType.ROBUX)

    m When "the process dies and the screen comes back with its saved state"
    val recreated = viewModel()
    recreated.exchange(EMAIL, PaERewardType.ROBUX)

    m Then "the redeem is sent again with the saved id"
    assertEquals(listOf("id-1", "id-1"), repository.calls.map { it.requestId })
  }

  @Test
  fun `A second tap while loading sends one request`() = coScenario {
    m Given "a redeem waiting for its answer"
    val viewModel = viewModel()
    val answer = CompletableDeferred<Result<Unit>>()
    repository.pending = answer
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)

    m When "the user taps again and retries before the answer"
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)
    viewModel.retry()
    answer.complete(Result.success(Unit))

    m Then "only one request went out and the redeem succeeded"
    assertEquals(1, repository.calls.size)
    assertEquals(ExchangeEmailUiState.Success, viewModel.uiState.value)
  }

  @Test
  fun `Different ids are generated by default`() = coScenario {
    m Given "the view model with its real id factory"
    val viewModel = ExchangeByEmailViewModel(
      exchangeUnitsUseCase = ExchangeUnitsUseCase(repository),
      exceptionHandler = exceptionHandler,
      walletUnitsRefresher = WalletUnitsRefresher(),
      savedStateHandle = savedStateHandle,
    )

    m When "two redeems succeed"
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)
    viewModel.resetState()
    viewModel.exchange(EMAIL, PaERewardType.ROBUX)

    m Then "each id is a different UUID the backend accepts"
    val ids = repository.calls.map { it.requestId }
    assertNotEquals(ids[0], ids[1])
    ids.forEach { assertEquals(true, it.matches(Regex("^[A-Za-z0-9_-]{1,64}$")), it) }
  }

  @Test
  fun `Each refusal shows its own error`() = coScenario {
    m Given "the backend refusing a redeem with each of its codes"
    val expected = mapOf(
      httpError(429, "REDEEM_LIMIT") to ExchangeErrorKind.Limit,
      httpError(429, "TOO_MANY_ATTEMPTS") to ExchangeErrorKind.TooManyAttempts,
      httpError(504, "REDEEM_OUTCOME_UNKNOWN") to ExchangeErrorKind.BeingChecked,
      httpError(409, "REDEEM_UNDER_REVIEW") to ExchangeErrorKind.BeingChecked,
      httpError(403, "ACCOUNT_RESTRICTED") to ExchangeErrorKind.Restricted,
      httpError(409, "REDEEM_IN_PROGRESS") to ExchangeErrorKind.Generic,
      httpError(409, "REDEEM_REQUEST_MISMATCH") to ExchangeErrorKind.Generic,
      httpError(403, "REGION_NOT_SUPPORTED") to ExchangeErrorKind.Generic,
      httpError(500, "REDEEM_LIMIT") to ExchangeErrorKind.Generic,
      IOException("offline") to ExchangeErrorKind.Generic,
    )

    m When "the user redeems once for each"
    val shown = expected.keys.map { failure ->
      repository.results += Result.failure(failure)
      viewModel().apply { exchange(EMAIL, PaERewardType.ROBUX) }.uiState.value
    }

    m Then "each one shows the error of its kind"
    assertEquals(expected.values.map { ExchangeEmailUiState.Error(it) }, shown)
  }

  @Test
  fun `Only the generic error can be retried`() = coScenario {
    m Given "every kind of redeem error"
    val kinds = ExchangeErrorKind.entries

    m When "the screen asks which of them offer Retry"
    val retryable = kinds.filter { it.canRetry }

    m Then "only the generic one does: retrying the others cannot help"
    assertEquals(listOf(ExchangeErrorKind.Generic), retryable)
  }

  @Test
  fun `Refusals are not reported and real failures are`() = coScenario {
    m Given "the backend refusing a redeem, then the network failing"
    val viewModel = viewModel()
    val failure = IOException("offline")
    repository.results += Result.failure(httpError(504, "REDEEM_OUTCOME_UNKNOWN"))
    repository.results += Result.failure(failure)

    m When "the user redeems twice"
    repeat(2) { viewModel.exchange(EMAIL, PaERewardType.ROBUX) }

    m Then "only the network failure goes to Crashlytics"
    assertEquals(listOf<Throwable>(failure), exceptionHandler.recorded)
  }

  private fun viewModel() = ExchangeByEmailViewModel(
    exchangeUnitsUseCase = ExchangeUnitsUseCase(repository),
    exceptionHandler = exceptionHandler,
    walletUnitsRefresher = WalletUnitsRefresher(),
    savedStateHandle = savedStateHandle,
    newRequestId = { "id-${++idCount}" },
  )

  private fun httpError(status: Int, code: String) = HttpException(
    Response.error<Any>(
      status,
      """{"detail":{"code":"$code"}}""".toResponseBody("application/json".toMediaType())
    )
  )

  private companion object {
    const val EMAIL = "user@example.com"
  }
}
