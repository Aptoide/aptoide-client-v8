package com.aptoide.android.aptoidegames.play_and_earn.presentation.units

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import cm.aptoide.pt.appcoins.domain.WalletInfo
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeRepository
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeUnitsUseCase
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import cm.aptoide.pt.test.gherkin.coScenario
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import cm.aptoide.pt.wallet.wallet_info.data.WalletInfoRepository
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
import com.aptoide.android.aptoidegames.play_and_earn.data.InMemoryPreferencesDataStore
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingRedeems
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow.ExchangeByEmailViewModel
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow.ExchangeEmailUiState
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow.ExchangeErrorKind
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// AND-950: a refusal on the units balance or a redeem by email is logged, not reported; the
// balance shows the same error as before; a redeem refusal shows its own message (AND-952).
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaEUnitsViewModelsRefusalTest {

  private class RecordingExceptionHandler : ExceptionHandler {
    val recorded = mutableListOf<Throwable>()
    override fun recordException(exception: Throwable) {
      recorded += exception
    }
  }

  private class FailingExchangeRepository(var failure: Throwable) : ExchangeRepository {
    @Deprecated("Use exchangeUnits(email, redeemType, requestId) instead.")
    override suspend fun exchangeUnits(): Result<Unit> = Result.failure(failure)

    override suspend fun exchangeUnits(email: String, redeemType: RedeemType, requestId: String) =
      Result.failure<Unit>(failure)

    override suspend fun getExchangeRate() = Result.failure<ExchangeRate>(failure)
  }

  private class FailingWalletInfoRepository(var failure: Throwable) : WalletInfoRepository {
    override suspend fun getWalletInfo(wallet: String): WalletInfo = throw failure
  }

  private val exceptionHandler = RecordingExceptionHandler()

  private val walletDataStore = object : DataStore<Preferences> {
    override val data: Flow<Preferences> =
      flowOf(preferencesOf(stringPreferencesKey("current_wallet_address") to "0xwallet"))

    override suspend fun updateData(
      transform: suspend (t: Preferences) -> Preferences
    ): Preferences = throw UnsupportedOperationException()
  }

  @BeforeEach
  fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

  @AfterEach
  fun tearDown() = Dispatchers.resetMain()

  @Test
  fun `A refused balance is not reported and shows the same error`() = coScenario {
    m Given "the backend refusing the balance for a restricted account"
    val refusal = httpError(403, "ACCOUNT_RESTRICTED")

    m When "the units balance loads"
    val state = balance(refusal)

    m Then "nothing goes to Crashlytics and the error is the one shown before"
    assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
    assertEquals(refusal.message, (state as UnitsBalanceUiState.Error).message)
  }

  @Test
  fun `A failed balance is reported and shows the same error`() = coScenario {
    m Given "the balance failing on the network"
    val failure = IOException("offline")

    m When "the units balance loads"
    val state = balance(failure)

    m Then "it goes to Crashlytics as before, with the same error"
    assertEquals(listOf<Throwable>(failure), exceptionHandler.recorded)
    assertEquals("offline", (state as UnitsBalanceUiState.Error).message)
  }

  @Test
  fun `A refused redeem by email is not reported and shows the same error`() = coScenario {
    m Given "the backend refusing the redeem: too many attempts"
    val refusal = httpError(429, "TOO_MANY_ATTEMPTS")

    m When "the user redeems by email"
    val state = redeemByEmail(refusal)

    m Then "nothing goes to Crashlytics and the error says so (AND-952)"
    assertEquals(emptyList<Throwable>(), exceptionHandler.recorded)
    assertEquals(ExchangeEmailUiState.Error(ExchangeErrorKind.TooManyAttempts), state)
  }

  @Test
  fun `A failed redeem by email is reported and shows the same error`() = coScenario {
    m Given "the redeem failing on the network"
    val failure = IOException("offline")

    m When "the user redeems by email"
    val state = redeemByEmail(failure)

    m Then "it goes to Crashlytics as before, with the generic error"
    assertEquals(listOf<Throwable>(failure), exceptionHandler.recorded)
    assertEquals(ExchangeEmailUiState.Error(ExchangeErrorKind.Generic), state)
  }

  private fun balance(failure: Throwable): UnitsBalanceUiState = UnitsBalanceViewModel(
    walletInfoRepository = FailingWalletInfoRepository(failure),
    walletCoreDataSource = WalletCoreDataSource(walletDataStore),
    exceptionHandler = exceptionHandler,
  ).uiState.value

  private fun redeemByEmail(failure: Throwable): ExchangeEmailUiState {
    val viewModel = ExchangeByEmailViewModel(
      exchangeUnitsUseCase = ExchangeUnitsUseCase(FailingExchangeRepository(failure)),
      exceptionHandler = exceptionHandler,
      walletUnitsRefresher = WalletUnitsRefresher(),
      pendingRedeems = PaEPendingRedeems(InMemoryPreferencesDataStore()),
      savedStateHandle = SavedStateHandle(),
    )
    viewModel.exchange("user@example.com", PaERewardType.ROBUX)
    return viewModel.uiState.value
  }

  private fun httpError(status: Int, code: String) = HttpException(
    Response.error<Any>(
      status,
      """{"detail":{"code":"$code"}}""".toResponseBody("application/json".toMediaType())
    )
  )
}
