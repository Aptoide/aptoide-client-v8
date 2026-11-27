package com.aptoide.android.aptoidegames.play_and_earn.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import cm.aptoide.pt.appcoins.domain.WalletInfo
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalEvents
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeRepository
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeUnitsUseCase
import cm.aptoide.pt.play_and_earn.exchange.domain.GetExchangeRateUseCase
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import cm.aptoide.pt.play_and_earn.exchange.presentation.ExchangeRateViewModel
import cm.aptoide.pt.play_and_earn.sessions.data.PaEAccountStatus
import cm.aptoide.pt.play_and_earn.sessions.data.PaEAccountStatusRepository
import cm.aptoide.pt.test.gherkin.coScenario
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import cm.aptoide.pt.wallet.wallet_info.data.WalletInfoRepository
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
import com.aptoide.android.aptoidegames.play_and_earn.data.InMemoryPreferencesDataStore
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingRedeems
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow.ExchangeByEmailViewModel
import com.aptoide.android.aptoidegames.play_and_earn.presentation.units.UnitsBalanceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
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

// AND-951: an account refusal from any app-side Play & Earn call restricts the account, with
// nothing wired in the caller: it goes through the crash reporting filter, which tells it. The
// session, heartbeat and sign-in bonus paths are covered in their own modules.
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaEAccountRestrictionPathsTest {

  private class RefusingExchangeRepository : ExchangeRepository {
    override suspend fun exchangeUnits(email: String, redeemType: RedeemType, requestId: String) =
      Result.failure<Unit>(restricted())

    override suspend fun getExchangeRate() = Result.failure<ExchangeRate>(restricted())
  }

  private class RefusingWalletInfoRepository : WalletInfoRepository {
    override suspend fun getWalletInfo(wallet: String): WalletInfo = throw restricted()
  }

  private val exceptionHandler = object : ExceptionHandler {
    override fun recordException(exception: Throwable) = Unit
  }

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
  fun `A restricted balance restricts the account`() = coScenario { scope ->
    m Given "an account the backend has restricted, the restriction listening"
    val restriction = scope.restriction()

    m When "the units balance loads"
    UnitsBalanceViewModel(
      walletInfoRepository = RefusingWalletInfoRepository(),
      walletCoreDataSource = WalletCoreDataSource(walletDataStore),
      exceptionHandler = exceptionHandler,
    )

    m Then "the account is restricted"
    assertEquals(PaEAccountRestrictionState.RESTRICTED, restriction.state.value)
  }

  @Test
  fun `A restricted redeem restricts the account`() = coScenario { scope ->
    m Given "an account the backend has restricted, the restriction listening"
    val restriction = scope.restriction()

    m When "the user redeems by email"
    ExchangeByEmailViewModel(
      exchangeUnitsUseCase = ExchangeUnitsUseCase(RefusingExchangeRepository()),
      exceptionHandler = exceptionHandler,
      walletUnitsRefresher = WalletUnitsRefresher(),
      pendingRedeems = PaEPendingRedeems(InMemoryPreferencesDataStore()),
      savedStateHandle = SavedStateHandle(),
    ).exchange("user@example.com", PaERewardType.ROBUX)

    m Then "the account is restricted"
    assertEquals(PaEAccountRestrictionState.RESTRICTED, restriction.state.value)
  }

  @Test
  fun `A restricted exchange rate restricts the account`() = coScenario { scope ->
    m Given "an account the backend has restricted, the restriction listening"
    val restriction = scope.restriction()

    m When "the exchange rate loads"
    ExchangeRateViewModel(
      getExchangeRateUseCase = GetExchangeRateUseCase(RefusingExchangeRepository()),
      exceptionHandler = exceptionHandler,
    )

    m Then "the account is restricted"
    assertEquals(PaEAccountRestrictionState.RESTRICTED, restriction.state.value)
  }

  private fun TestScope.restriction() = PaEAccountRestriction(
    accountStatus = object : PaEAccountStatusRepository {
      // Cannot tell: a refusal's word stands.
      override suspend fun getStatus() = PaEAccountStatus.UNKNOWN
    },
    isSignedIn = { true },
    refusals = PaERefusalEvents.refusals,
    nowMillis = { 0L },
    scope = CoroutineScope(
      backgroundScope.coroutineContext + UnconfinedTestDispatcher(testScheduler)
    ),
  )

  private companion object {
    fun restricted() = HttpException(
      Response.error<Any>(
        403,
        """{"detail":{"code":"ACCOUNT_RESTRICTED"}}"""
          .toResponseBody("application/json".toMediaType())
      )
    )
  }
}
