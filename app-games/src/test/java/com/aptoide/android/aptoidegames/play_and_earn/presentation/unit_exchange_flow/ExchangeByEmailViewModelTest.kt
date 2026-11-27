package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import androidx.lifecycle.SavedStateHandle
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeRepository
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeUnitsUseCase
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
import com.aptoide.android.aptoidegames.play_and_earn.data.InMemoryPreferencesDataStore
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingRedeems
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.time.Instant

// AND-952: one request id per redeem, kept (by PaEPendingRedeems) until the backend's answer is
// final, so a retry after a lost answer never redeems twice.
@OptIn(ExperimentalCoroutinesApi::class)
internal class ExchangeByEmailViewModelTest {

  private data class Call(val email: String, val redeemType: RedeemType, val requestId: String)

  private class FakeExchangeRepository : ExchangeRepository {
    val calls = mutableListOf<Call>()
    val results = ArrayDeque<Result<Unit>>()
    var pending: CompletableDeferred<Result<Unit>>? = null

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
  private var idCount = 0
  private val pendingRedeems = PaEPendingRedeems(
    dataStore = InMemoryPreferencesDataStore(),
    nowMillis = { 0L },
    newRequestId = { "id-${++idCount}" },
  )

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

  // AND-964: refused before anything was deducted; the backend marks that redeem FAILED. Its id
  // is done with, like a limit's, so the retry after the VPN is off is a new redeem.
  @Test
  fun `A VPN refusal shows its error, and the retry gets a new request id and goes through`() =
    coScenario {
      m Given "the backend refusing the redeem because the phone reports a VPN"
      val viewModel = viewModel()
      repository.results += Result.failure(httpError(403, "VPN_NOT_ALLOWED"))
      viewModel.exchange(EMAIL, PaERewardType.ROBUX)
      val refused = viewModel.uiState.value

      m When "the user turns the VPN off and taps Retry"
      viewModel.retry()

      m Then "the VPN error showed, then the same redeem went through with a new id"
      assertEquals(ExchangeEmailUiState.Error(ExchangeErrorKind.Vpn), refused)
      assertEquals(
        listOf(
          Call(EMAIL, RedeemType.ROBLOX_ROBUX, "id-1"),
          Call(EMAIL, RedeemType.ROBLOX_ROBUX, "id-2"),
        ),
        repository.calls
      )
      assertEquals(ExchangeEmailUiState.Success, viewModel.uiState.value)
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
  fun `A redeem whose answer was lost keeps its id after leaving the screen`() = coScenario {
    m Given "a redeem the server took but whose answer was lost"
    repository.results += Result.failure(IOException("timeout"))
    viewModel(SavedStateHandle()).exchange(EMAIL, PaERewardType.ROBUX)

    m When "the user goes back, opens Redeem again and submits the same email"
    viewModel(SavedStateHandle()).exchange(EMAIL, PaERewardType.ROBUX)

    m Then "the redeem is replayed with its first id, not redeemed twice"
    assertEquals(listOf("id-1", "id-1"), repository.calls.map { it.requestId })
  }

  @Test
  fun `The redeem to retry survives a recreated view model`() = coScenario {
    m Given "a redeem that failed on the network"
    val savedStateHandle = SavedStateHandle()
    repository.results += Result.failure(IOException("offline"))
    viewModel(savedStateHandle).exchange(EMAIL, PaERewardType.ROBUX)

    m When "the process dies and the screen comes back with its saved state to retry"
    viewModel(savedStateHandle).retry()

    m Then "the same redeem is sent again with the same id"
    assertEquals(List(2) { Call(EMAIL, RedeemType.ROBLOX_ROBUX, "id-1") }, repository.calls)
  }

  @Test
  fun `Retry with nothing to retry goes back to the form`() = coScenario {
    m Given "a screen that never sent a redeem"
    val viewModel = viewModel()

    m When "retry is called"
    viewModel.retry()

    m Then "nothing is sent and the form shows"
    assertEquals(emptyList<Call>(), repository.calls)
    assertEquals(ExchangeEmailUiState.Idle, viewModel.uiState.value)
  }

  @Test
  fun `Retry with an unknown saved reward goes back to the form`() = coScenario {
    m Given "saved state naming a reward this version does not know"
    val savedStateHandle = SavedStateHandle(
      mapOf("exchange_email" to EMAIL, "exchange_reward_type" to "GOLD")
    )
    val viewModel = viewModel(savedStateHandle)

    m When "retry is called"
    viewModel.retry()

    m Then "nothing is sent and the form shows"
    assertEquals(emptyList<Call>(), repository.calls)
    assertEquals(ExchangeEmailUiState.Idle, viewModel.uiState.value)
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
  fun `A redeem that went through shows success even if the balance refresh fails`() =
    coScenario {
      m Given "the units balance refresh failing"
      val failure = IllegalStateException("refresh failed")
      val viewModel = ExchangeByEmailViewModel(
        exchangeUnitsUseCase = ExchangeUnitsUseCase(repository),
        exceptionHandler = exceptionHandler,
        refreshWalletUnits = { throw failure },
        pendingRedeems = pendingRedeems,
        savedStateHandle = SavedStateHandle(),
      )

      m When "the redeem succeeds"
      viewModel.exchange(EMAIL, PaERewardType.ROBUX)

      m Then "the screen says so, the failure is reported and the id is done with"
      assertEquals(ExchangeEmailUiState.Success, viewModel.uiState.value)
      assertEquals(listOf<Throwable>(failure), exceptionHandler.recorded)
      assertEquals("id-2", pendingRedeems.idFor(EMAIL, PaERewardType.ROBUX))
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
      httpError(403, "VPN_NOT_ALLOWED") to ExchangeErrorKind.Vpn,
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
  fun `The limits carry when they end and the other errors do not`() = coScenario {
    m Given "refusals that all say when to try again"
    val retryAt = "2026-12-01T14:30:00Z"
    val failures = listOf(
      httpError(429, "REDEEM_LIMIT", retryAt),
      httpError(429, "TOO_MANY_ATTEMPTS", retryAt),
      httpError(409, "REDEEM_UNDER_REVIEW", retryAt),
      httpError(403, "ACCOUNT_RESTRICTED", retryAt),
      httpError(409, "REDEEM_IN_PROGRESS", retryAt),
    )

    m When "the user redeems once for each"
    val shown = failures.map { failure ->
      repository.results += Result.failure(failure)
      viewModel().apply { exchange(EMAIL, PaERewardType.ROBUX) }.uiState.value
    }

    m Then "only the redeem limit and too many attempts keep the time"
    val end = Instant.parse(retryAt)
    assertEquals(
      listOf(
        ExchangeEmailUiState.Error(ExchangeErrorKind.Limit, end),
        ExchangeEmailUiState.Error(ExchangeErrorKind.TooManyAttempts, end),
        ExchangeEmailUiState.Error(ExchangeErrorKind.BeingChecked, null),
        ExchangeEmailUiState.Error(ExchangeErrorKind.Restricted, null),
        ExchangeEmailUiState.Error(ExchangeErrorKind.Generic, null),
      ),
      shown
    )
  }

  @Test
  fun `Only the generic and the VPN errors can be retried`() = coScenario {
    m Given "every kind of redeem error"
    val kinds = ExchangeErrorKind.entries

    m When "the screen asks which of them offer Retry"
    val retryable = kinds.filter { it.canRetry }

    m Then "only those do: the VPN goes once turned off, retrying the others cannot help"
    assertEquals(listOf(ExchangeErrorKind.Generic, ExchangeErrorKind.Vpn), retryable)
  }

  @Test
  fun `Refusals are not reported and real failures are`() = coScenario {
    m Given "the backend refusing a redeem, then for a VPN, then the network failing"
    val viewModel = viewModel()
    val failure = IOException("offline")
    repository.results += Result.failure(httpError(504, "REDEEM_OUTCOME_UNKNOWN"))
    repository.results += Result.failure(httpError(403, "VPN_NOT_ALLOWED"))
    repository.results += Result.failure(failure)

    m When "the user redeems three times"
    repeat(3) { viewModel.exchange(EMAIL, PaERewardType.ROBUX) }

    m Then "only the network failure goes to Crashlytics"
    assertEquals(listOf<Throwable>(failure), exceptionHandler.recorded)
  }

  private fun viewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) =
    ExchangeByEmailViewModel(
      exchangeUnitsUseCase = ExchangeUnitsUseCase(repository),
      exceptionHandler = exceptionHandler,
      walletUnitsRefresher = WalletUnitsRefresher(),
      pendingRedeems = pendingRedeems,
      savedStateHandle = savedStateHandle,
    )

  private fun httpError(status: Int, code: String, retryAt: String? = null) = HttpException(
    Response.error<Any>(
      status,
      (retryAt?.let { """{"detail":{"code":"$code","retry_at":"$it"}}""" }
        ?: """{"detail":{"code":"$code"}}""")
        .toResponseBody("application/json".toMediaType())
    )
  )

  private companion object {
    const val EMAIL = "user@example.com"
  }
}
