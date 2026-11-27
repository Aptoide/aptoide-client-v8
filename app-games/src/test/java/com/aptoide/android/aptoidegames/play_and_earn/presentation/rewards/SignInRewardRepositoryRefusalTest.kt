package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException
import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.play_and_earn.events.data.EventAlreadySubmittedException
import cm.aptoide.pt.play_and_earn.events.data.EventException
import cm.aptoide.pt.play_and_earn.events.data.EventsRepository
import cm.aptoide.pt.play_and_earn.events.domain.EventType
import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeRepository
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.GetExchangeRateUseCase
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
import com.aptoide.android.aptoidegames.play_and_earn.data.InMemoryPreferencesDataStore
import com.aptoide.android.aptoidegames.play_and_earn.data.PaESignInBonusRefusal
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import java.io.IOException
import java.util.concurrent.TimeUnit

// AND-953: a sign-in bonus the backend refuses for good ends on every offer surface, and is not
// asked for again for a day; any other failure keeps it claimable, as before.
@OptIn(ExperimentalCoroutinesApi::class)
internal class SignInRewardRepositoryRefusalTest {

  private class FakeEventsRepository : EventsRepository {
    val results = ArrayDeque<Result<Unit>>()
    var sent = 0

    override suspend fun submitEvent(eventType: EventType): Result<Unit> {
      sent++
      return results.removeFirstOrNull() ?: Result.success(Unit)
    }
  }

  private class FakeMissionsRepository : PaEMissionsRepository {
    override suspend fun getCampaignMissions(packageName: String, forceRefresh: Boolean) =
      Result.success(PaEMissions(checkpoints = emptyList(), missions = emptyList()))

    override suspend fun getEventMissions() = Result.success(
      listOf(
        PaEMission(
          title = "Sign in",
          description = null,
          icon = null,
          type = PaEMissionType.EVENT,
          arguments = JsonObject().apply { addProperty("event_type", "FIRST_SIGN_IN") },
          units = 100,
          progress = null,
        )
      )
    )

    override fun observeCampaignMissions(packageName: String): Flow<Result<PaEMissions>> =
      emptyFlow()

    override suspend fun getCachedMissions(packageName: String): PaEMissions? = null

    override suspend fun markMissionAsCompleted(packageName: String, missionTitle: String) = Unit
  }

  // No rate: the reward shows the default amount.
  private class NoRateExchangeRepository : ExchangeRepository {
    override suspend fun exchangeUnits(email: String, redeemType: RedeemType, requestId: String) =
      Result.failure<Unit>(UnsupportedOperationException())

    override suspend fun getExchangeRate() =
      Result.failure<ExchangeRate>(UnsupportedOperationException())
  }

  private val events = FakeEventsRepository()
  private val dataStore = InMemoryPreferencesDataStore()
  private var now = 1_000_000L
  private val bonusRefusal = PaESignInBonusRefusal(dataStore = dataStore, nowMillis = { now })

  private fun TestScope.repository() = SignInRewardRepository(
    eventsRepository = events,
    paeMissionsRepository = FakeMissionsRepository(),
    getExchangeRateUseCase = GetExchangeRateUseCase(NoRateExchangeRepository()),
    walletUnitsRefresher = WalletUnitsRefresher(),
    bonusRefusal = bonusRefusal,
    scope = CoroutineScope(
      backgroundScope.coroutineContext + UnconfinedTestDispatcher(testScheduler)
    ),
  )

  private val SignInRewardRepository.unclaimed
    get() = rewardState.value as RewardState.Unclaimed

  // --------------------------------------------------------------------- refusals end the bonus

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    names = [
      "ACCOUNT_RESTRICTED", "REGION_NOT_SUPPORTED", "DEVICE_NOT_SUPPORTED", "UPDATE_REQUIRED",
    ]
  )
  fun `A refused bonus ends and is not asked for again`(code: PaERefusalCode) =
    coScenario { scope ->
      m Given "an offered bonus the backend refuses with ${code.name}"
      val repository = scope.repository()
      val reward = repository.unclaimed.reward
      events.results += Result.failure(PaERefusalException(PaERefusal(code)))

      m When "the user claims it, then claims it again"
      repository.claimReward(reward)
      repository.claimReward(reward)

      m Then "the offer ends after one request, and the refusal is remembered"
      assertEquals(RewardState.Refused, repository.rewardState.value)
      assertEquals(1, events.sent)
      assertTrue(bonusRefusal.isRefused())
    }

  @Test
  fun `A remembered refusal is not offered after a restart`() = coScenario { scope ->
    m Given "a bonus refused an hour ago"
    bonusRefusal.record()
    now += TimeUnit.HOURS.toMillis(1)

    m When "the app starts again and the user reaches an offer surface"
    val repository = scope.repository()
    repository.refresh()

    m Then "the bonus is not offered"
    assertEquals(RewardState.Refused, repository.rewardState.value)
  }

  @Test
  fun `A refusal still holds at 23 hours 59 minutes`() = coScenario { scope ->
    m Given "a bonus refused by the backend"
    val repository = scope.repository()
    events.results += refusedFor(PaERefusalCode.ACCOUNT_RESTRICTED)
    repository.claimReward(repository.unclaimed.reward)

    m When "23 hours 59 minutes later the user reaches an offer surface and claims"
    now += TimeUnit.HOURS.toMillis(24) - TimeUnit.MINUTES.toMillis(1)
    repository.refresh()
    repository.claimReward(PendingPaEReward(PaERewardType.ROBUX, "$1.00", 100))

    m Then "it is still not offered, and nothing is sent"
    assertEquals(RewardState.Refused, repository.rewardState.value)
    assertEquals(1, events.sent)
  }

  @Test
  fun `A refusal ends after 24 hours and the bonus is offered again`() = coScenario { scope ->
    m Given "a bonus refused by the backend"
    val repository = scope.repository()
    events.results += refusedFor(PaERefusalCode.REGION_NOT_SUPPORTED)
    repository.claimReward(repository.unclaimed.reward)

    m When "24 hours later the user reaches an offer surface and claims"
    now += TimeUnit.HOURS.toMillis(24)
    repository.refresh()
    val offered = repository.rewardState.value is RewardState.Unclaimed
    repository.claimReward(repository.unclaimed.reward)

    m Then "it was offered, and the claim is sent and granted"
    assertTrue(offered)
    assertEquals(2, events.sent)
    assertEquals(RewardState.Claimed, repository.rewardState.value)
  }

  @Test
  fun `Clearing the refusal offers the bonus again`() = coScenario { scope ->
    m Given "a bonus refused by the backend"
    val repository = scope.repository()
    events.results += refusedFor(PaERefusalCode.ACCOUNT_RESTRICTED)
    repository.claimReward(repository.unclaimed.reward)

    m When "the account changes and the refusal is cleared"
    bonusRefusal.clear()

    m Then "the bonus is offered again at once"
    assertTrue(repository.rewardState.value is RewardState.Unclaimed)
  }

  // ------------------------------------------------------------------- everything else as before

  @Test
  fun `An already granted bonus is claimed, as before`() = coScenario { scope ->
    m Given "an offered bonus the backend already granted (409)"
    val repository = scope.repository()
    events.results += Result.failure(EventAlreadySubmittedException("Event already submitted"))

    m When "the user claims it"
    repository.claimReward(repository.unclaimed.reward)

    m Then "it is claimed, and no refusal is remembered"
    assertEquals(RewardState.Claimed, repository.rewardState.value)
    assertFalse(bonusRefusal.isRefused())
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("retryableFailures")
  fun `A failure that is not a final refusal keeps the bonus claimable`(
    name: String,
    failure: Throwable,
  ) = coScenario { scope ->
    m Given "an offered bonus whose claim fails: $name"
    val repository = scope.repository()
    events.results += Result.failure(failure)

    m When "the user claims it, then tries again"
    repository.claimReward(repository.unclaimed.reward)
    val afterFailure = repository.rewardState.value
    repository.claimReward(repository.unclaimed.reward)

    m Then "it stayed offered, the retry was sent, and no refusal is remembered"
    assertTrue(afterFailure is RewardState.Unclaimed, afterFailure.toString())
    assertEquals(2, events.sent)
    assertFalse(bonusRefusal.isRefused())
  }

  private fun refusedFor(code: PaERefusalCode) =
    Result.failure<Unit>(PaERefusalException(PaERefusal(code)))

  companion object {
    // What DefaultEventsRepository returns for each: a network error as is, an HTTP error with a
    // string detail (or none) as an EventException, a refusal that does not block play as such.
    @JvmStatic
    fun retryableFailures() = listOf(
      arrayOf("network error", IOException("offline")),
      arrayOf("500", EventException("HTTP 500 Internal Server Error")),
      arrayOf("503 (the check itself failed)", EventException("HTTP 503 Service Unavailable")),
      arrayOf("403 without a code", EventException("Not available")),
      arrayOf(
        "a refusal that does not block play",
        PaERefusalException(PaERefusal(PaERefusalCode.TOO_MANY_ATTEMPTS)),
      ),
    )
  }
}
