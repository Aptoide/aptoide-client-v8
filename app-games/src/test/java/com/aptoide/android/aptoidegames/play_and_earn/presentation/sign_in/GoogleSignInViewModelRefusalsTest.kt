package com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in

import androidx.credentials.CredentialManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.test.gherkin.coScenario
import cm.aptoide.pt.wallet.authorization.data.WalletAuthRepository
import cm.aptoide.pt.wallet.authorization.domain.UserWalletData
import com.aptoide.android.aptoidegames.play_and_earn.data.InMemoryPreferencesDataStore
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingRedeems
import com.aptoide.android.aptoidegames.play_and_earn.data.PaESignInBonusRefusal
import com.aptoide.android.aptoidegames.play_and_earn.data.UserAccountPreferencesRepository
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaESessionRefusals
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.FakeEventsRepository
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.RewardState
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.SignInRewardRepository
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.signInRewardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.Proxy

// AND-950: refusals belong to the account; signing out forgets them. So does the pending
// redeem (AND-952) and a refused sign-in bonus (AND-953).
@OptIn(ExperimentalCoroutinesApi::class)
internal class GoogleSignInViewModelRefusalsTest {

  private val emptyDataStore = object : DataStore<Preferences> {
    override val data: Flow<Preferences> = flowOf(emptyPreferences())
    override suspend fun updateData(
      transform: suspend (t: Preferences) -> Preferences
    ): Preferences = transform(emptyPreferences())
  }

  // Every call (clearCredentialState included) completes at once with Unit.
  private val credentialManager = Proxy.newProxyInstance(
    CredentialManager::class.java.classLoader,
    arrayOf(CredentialManager::class.java)
  ) { _, _, _ -> Unit } as CredentialManager

  private val walletAuthRepository = object : WalletAuthRepository {
    override suspend fun authorizeGoogleUser(token: String) =
      Result.failure<UserWalletData>(UnsupportedOperationException())

    override suspend fun refreshUserWallet(refreshToken: String) =
      Result.failure<UserWalletData>(UnsupportedOperationException())

    override suspend fun clearAuthorizationData() = Unit
  }

  private val refusals = PaESessionRefusals()
  private val pendingRedeems = PaEPendingRedeems(
    dataStore = InMemoryPreferencesDataStore(),
    nowMillis = { 0L },
    newRequestId = { "id-${++idCount}" },
  )
  private var idCount = 0
  private val signInBonusRefusal = PaESignInBonusRefusal(
    dataStore = InMemoryPreferencesDataStore(),
    nowMillis = { 0L },
  )
  private val bonusEvents = FakeEventsRepository()

  @BeforeEach
  fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

  @AfterEach
  fun tearDown() = Dispatchers.resetMain()

  @Test
  fun `Signing out forgets the refusals`() = coScenario { scope ->
    m Given "a restricted account and a game refused for the region"
    refusals.record(GAME, PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED))
    refusals.record(OTHER_GAME, PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))
    val viewModel = scope.viewModel()

    m When "the user signs out"
    viewModel.signOut()

    m Then "no Rewards tab shows a refusal and nothing blocks the next account"
    assertNull(refusals.observe(GAME).first())
    assertNull(refusals.observe(OTHER_GAME).first())
    assertNull(refusals.blockingRefusalOf(GAME))
  }

  @Test
  fun `Signing out forgets the pending redeem`() = coScenario { scope ->
    m Given "a redeem whose answer is not final yet"
    pendingRedeems.idFor(EMAIL, PaERewardType.ROBUX)
    val viewModel = scope.viewModel()

    m When "the user signs out"
    viewModel.signOut()

    m Then "the same redeem by the next account is a new one (AND-952)"
    assertEquals("id-2", pendingRedeems.idFor(EMAIL, PaERewardType.ROBUX))
  }

  @Test
  fun `Signing out forgets the refused sign-in bonus`() = coScenario { scope ->
    m Given "a sign-in bonus the backend refused for this account"
    val rewards = scope.signInRewardRepository(bonusEvents, signInBonusRefusal)
    bonusEvents.results += Result.failure(
      PaERefusalException(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))
    )
    rewards.claimReward((rewards.rewardState.value as RewardState.Unclaimed).reward)
    val viewModel = scope.viewModel(rewards)

    m When "the user signs out"
    viewModel.signOut()

    m Then "the bonus is offered to the next account (AND-953)"
    assertFalse(signInBonusRefusal.isRefused())
    assertTrue(rewards.rewardState.value is RewardState.Unclaimed)
  }

  private fun TestScope.viewModel(
    rewards: SignInRewardRepository = signInRewardRepository(bonusEvents, signInBonusRefusal),
  ) = GoogleSignInViewModel(
    credentialManager = credentialManager,
    userAuthRepository = walletAuthRepository,
    userAccountPreferencesRepository = UserAccountPreferencesRepository(emptyDataStore),
    exceptionHandler = object : ExceptionHandler {
      override fun recordException(exception: Throwable) = Unit
    },
    paESessionRefusals = refusals,
    pendingRedeems = pendingRedeems,
    signInRewardRepository = rewards,
  )

  private companion object {
    const val GAME = "com.game"
    const val OTHER_GAME = "com.other.game"
    const val EMAIL = "user@example.com"
  }
}
