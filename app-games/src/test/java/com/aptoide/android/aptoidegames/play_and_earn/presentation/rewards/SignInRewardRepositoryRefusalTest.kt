package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException
import cm.aptoide.pt.play_and_earn.events.data.EventAlreadySubmittedException
import cm.aptoide.pt.play_and_earn.events.data.EventException
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.play_and_earn.data.PaESignInBonusRefusal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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

// AND-953: a sign-in bonus the backend refuses for good ends on every offer surface, says why
// once, and is not asked for again for a day; any other failure keeps it claimable, as before.
@OptIn(ExperimentalCoroutinesApi::class)
internal class SignInRewardRepositoryRefusalTest {

  private val events = FakeEventsRepository()
  private val dataStore = FlakyPreferencesDataStore()
  private var now = 1_000_000L
  private val bonusRefusal = PaESignInBonusRefusal(dataStore = dataStore, nowMillis = { now })
  private val refusedEvents = mutableListOf<PaERefusalCode>()

  private fun TestScope.repository() = signInRewardRepository(events, bonusRefusal).also {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      it.refusedEvent.collect { code -> refusedEvents += code }
    }
  }

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
  fun `A refused bonus ends, says why once and is not asked for again`(code: PaERefusalCode) =
    coScenario { scope ->
      m Given "an offered bonus the backend refuses with ${code.name}"
      val repository = scope.repository()
      val reward = repository.unclaimed.reward
      events.results += refusedFor(code)

      m When "the user claims it, then claims it again"
      repository.claimReward(reward)
      repository.claimReward(reward)

      m Then "the offer ends after one request, the reason is told once, and it is remembered"
      assertEquals(RewardState.Refused, repository.rewardState.value)
      assertEquals(1, events.sent)
      assertEquals(listOf(code), refusedEvents)
      assertTrue(bonusRefusal.isRefused())
    }

  @Test
  fun `A remembered refusal is not offered after a restart, silently`() = coScenario { scope ->
    m Given "a bonus refused an hour ago"
    bonusRefusal.record()
    now += TimeUnit.HOURS.toMillis(1)

    m When "the app starts again and the user reaches an offer surface"
    val repository = scope.repository()
    repository.refresh()

    m Then "the bonus is not offered, and nothing is told"
    assertEquals(RewardState.Refused, repository.rewardState.value)
    assertEquals(emptyList<PaERefusalCode>(), refusedEvents)
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

    m Then "it is still not offered, nothing is sent, and the reason was told only once"
    assertEquals(RewardState.Refused, repository.rewardState.value)
    assertEquals(1, events.sent)
    assertEquals(listOf(PaERefusalCode.ACCOUNT_RESTRICTED), refusedEvents)
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

  // ------------------------------------------------------------------------ the account changes

  @Test
  fun `Forgetting the refusal offers the bonus again`() = coScenario { scope ->
    m Given "a bonus refused by the backend"
    val repository = scope.repository()
    events.results += refusedFor(PaERefusalCode.ACCOUNT_RESTRICTED)
    repository.claimReward(repository.unclaimed.reward)

    m When "the user signs in or out"
    repository.forgetRefusal()

    m Then "the bonus is offered again at once, and the stored refusal is gone"
    assertTrue(repository.rewardState.value is RewardState.Unclaimed)
    assertFalse(bonusRefusal.isRefused())
  }

  @Test
  fun `Forgetting the refusal offers the bonus again even when the store cannot be cleared`() =
    coScenario { scope ->
      m Given "a bonus refused by the backend, and a store that cannot be written"
      val repository = scope.repository()
      events.results += refusedFor(PaERefusalCode.ACCOUNT_RESTRICTED)
      repository.claimReward(repository.unclaimed.reward)
      dataStore.failWrites = true

      m When "the user signs in or out"
      repository.forgetRefusal()

      m Then "the bonus is offered again, and nothing crashed"
      assertTrue(repository.rewardState.value is RewardState.Unclaimed)
    }

  @Test
  fun `A refusal answered after the account changed does not end the bonus`() =
    coScenario { scope ->
      m Given "a claim in flight that the backend will refuse"
      val repository = scope.repository()
      val gate = CompletableDeferred<Unit>()
      events.gate = gate
      events.results += refusedFor(PaERefusalCode.ACCOUNT_RESTRICTED)
      repository.claimReward(repository.unclaimed.reward)

      m When "the user signs out before the answer arrives"
      repository.forgetRefusal()
      gate.complete(Unit)

      m Then "the bonus stays offered for the next account, unremembered and untold"
      assertTrue(repository.rewardState.value is RewardState.Unclaimed)
      assertFalse(bonusRefusal.isRefused())
      assertEquals(emptyList<PaERefusalCode>(), refusedEvents)
    }

  // ------------------------------------------------------------------------- a broken store

  @Test
  fun `A refusal that cannot be remembered still ends the bonus this session`() =
    coScenario { scope ->
      m Given "an offered bonus, a store that cannot be written, and a refusing backend"
      val repository = scope.repository()
      dataStore.failWrites = true
      events.results += refusedFor(PaERefusalCode.REGION_NOT_SUPPORTED)

      m When "the user claims it"
      repository.claimReward(repository.unclaimed.reward)

      m Then "nothing crashed, the offer ended, and the reason was told"
      assertEquals(RewardState.Refused, repository.rewardState.value)
      assertEquals(listOf(PaERefusalCode.REGION_NOT_SUPPORTED), refusedEvents)
    }

  @Test
  fun `A store that cannot be read refuses nothing`() = coScenario { scope ->
    m Given "a store that cannot be read"
    dataStore.failReads = true

    m When "the app starts and the user claims the bonus"
    val repository = scope.repository()
    repository.claimReward(repository.unclaimed.reward)

    m Then "the bonus was offered, and the claim is sent and granted"
    assertEquals(1, events.sent)
    assertEquals(RewardState.Claimed, repository.rewardState.value)
  }

  // ------------------------------------------------------------------- everything else as before

  @Test
  fun `An already granted bonus is claimed, as before`() = coScenario { scope ->
    m Given "an offered bonus the backend already granted (409)"
    val repository = scope.repository()
    events.results += Result.failure(EventAlreadySubmittedException("Event already submitted"))

    m When "the user claims it"
    repository.claimReward(repository.unclaimed.reward)

    m Then "it is claimed, no refusal is remembered, and nothing is told"
    assertEquals(RewardState.Claimed, repository.rewardState.value)
    assertFalse(bonusRefusal.isRefused())
    assertEquals(emptyList<PaERefusalCode>(), refusedEvents)
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

    m Then "it stayed offered, the retry was sent, and nothing was remembered or told"
    assertTrue(afterFailure is RewardState.Unclaimed, afterFailure.toString())
    assertEquals(2, events.sent)
    assertFalse(bonusRefusal.isRefused())
    assertEquals(emptyList<PaERefusalCode>(), refusedEvents)
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
