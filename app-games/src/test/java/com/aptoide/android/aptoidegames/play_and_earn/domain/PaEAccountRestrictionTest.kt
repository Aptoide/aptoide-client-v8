package com.aptoide.android.aptoidegames.play_and_earn.domain

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.play_and_earn.sessions.data.PaEAccountStatus
import cm.aptoide.pt.play_and_earn.sessions.data.PaEAccountStatusRepository
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestriction.Companion.REFRESH_INTERVAL_MS
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestriction.Companion.RETRY_AFTER_FAILURE_MS
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestrictionState.NOT_RESTRICTED
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestrictionState.RESTRICTED
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestrictionState.UNKNOWN
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

// AND-951: one in-memory answer to "is this account restricted?", for every Play & Earn screen.
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaEAccountRestrictionTest {

  private class FakeAccountStatus : PaEAccountStatusRepository {
    var calls = 0
    var answer = PaEAccountStatus.NOT_RESTRICTED

    // Answers waiting to be given, in call order; each one ignores cancellation, like an answer
    // that is already on its way back when its caller gives up on it.
    val pending = ArrayDeque<CompletableDeferred<PaEAccountStatus>>()

    override suspend fun getStatus(): PaEAccountStatus {
      calls++
      val next = pending.removeFirstOrNull() ?: return answer
      return withContext(NonCancellable) { next.await() }
    }

    fun holdNextAnswer() = CompletableDeferred<PaEAccountStatus>().also { pending += it }
  }

  private val accountStatus = FakeAccountStatus()
  private val refusals = MutableSharedFlow<PaERefusal>(extraBufferCapacity = 8)
  private var signedIn: () -> Boolean = { true }
  private var now = 0L

  @Test
  fun `A restricted answer restricts the account`() = coScenario { scope ->
    m Given "the backend says the signed-in account is restricted"
    accountStatus.answer = PaEAccountStatus.RESTRICTED
    val restriction = scope.restriction()

    m When "a Play & Earn screen opens"
    restriction.refresh()

    m Then "the account is restricted, and that is settled"
    assertEquals(RESTRICTED, restriction.state.value)
    assertTrue(restriction.settled.value)
  }

  @Test
  fun `A not restricted answer opens the account`() = coScenario { scope ->
    m Given "the backend says the signed-in account is fine"
    val restriction = scope.restriction()

    m When "a Play & Earn screen opens"
    restriction.refresh()

    m Then "the account is not restricted"
    assertEquals(NOT_RESTRICTED, restriction.state.value)
  }

  @Test
  fun `Nothing is known while the first answer is on its way`() = coScenario { scope ->
    m Given "a status call that has not answered yet"
    accountStatus.holdNextAnswer()
    val restriction = scope.restriction()

    m When "a Play & Earn screen opens"
    restriction.refresh()

    m Then "the state is unknown and not settled: a held screen waits"
    assertEquals(UNKNOWN, restriction.state.value)
    assertFalse(restriction.settled.value)
  }

  @Test
  fun `A failed first call leaves the account unknown but settled`() = coScenario { scope ->
    m Given "a status call that cannot tell"
    accountStatus.answer = PaEAccountStatus.UNKNOWN
    val restriction = scope.restriction()

    m When "a Play & Earn screen opens"
    restriction.refresh()

    m Then "nothing is known, and a held screen stops waiting"
    assertEquals(UNKNOWN, restriction.state.value)
    assertTrue(restriction.settled.value)
  }

  @Test
  fun `A failed call keeps a restriction`() = coScenario { scope ->
    m Given "an account restricted a minute ago"
    accountStatus.answer = PaEAccountStatus.RESTRICTED
    val restriction = scope.restriction()
    restriction.refresh()

    m When "the next status call a minute later cannot tell (flaky network)"
    accountStatus.answer = PaEAccountStatus.UNKNOWN
    now += REFRESH_INTERVAL_MS
    restriction.refresh()

    m Then "the account stays restricted"
    assertEquals(RESTRICTED, restriction.state.value)
  }

  @Test
  fun `A failed call keeps an account in good standing`() = coScenario { scope ->
    m Given "an account the backend said is fine a minute ago"
    val restriction = scope.restriction()
    restriction.refresh()

    m When "the next status call a minute later cannot tell"
    accountStatus.answer = PaEAccountStatus.UNKNOWN
    now += REFRESH_INTERVAL_MS
    restriction.refresh()

    m Then "the account stays not restricted"
    assertEquals(NOT_RESTRICTED, restriction.state.value)
  }

  @Test
  fun `A failed call is tried again after a short while, not a minute`() = coScenario { scope ->
    m Given "a status call that could not tell"
    accountStatus.answer = PaEAccountStatus.UNKNOWN
    val restriction = scope.restriction()
    restriction.refresh()

    m When "screens open just before and just after the short retry delay"
    now += RETRY_AFTER_FAILURE_MS - 1
    restriction.refresh()
    now += 1
    restriction.refresh()

    m Then "the backend was asked twice: the second try came after the short delay"
    assertEquals(2, accountStatus.calls)
  }

  @Test
  fun `An account refusal from any call restricts the account`() = coScenario { scope ->
    m Given "an account nobody asked the status of yet, the backend saying it is restricted"
    accountStatus.answer = PaEAccountStatus.RESTRICTED
    val restriction = scope.restriction()

    m When "a Play & Earn call is refused because the account is restricted"
    refusals.emit(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m Then "the account is restricted"
    assertEquals(RESTRICTED, restriction.state.value)
  }

  @Test
  fun `An account refusal is confirmed with the backend at once`() = coScenario { scope ->
    m Given "an account asked about a moment ago, so a screen would not ask again yet"
    val restriction = scope.restriction()
    restriction.refresh()

    m When "a Play & Earn call is refused because the account is restricted"
    accountStatus.answer = PaEAccountStatus.RESTRICTED
    refusals.emit(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m Then "the backend is asked again at once, and its answer holds"
    assertEquals(2, accountStatus.calls)
    assertEquals(RESTRICTED, restriction.state.value)
  }

  @Test
  fun `A refusal the backend cannot confirm still restricts`() = coScenario { scope ->
    m Given "a status call that cannot tell"
    accountStatus.answer = PaEAccountStatus.UNKNOWN
    val restriction = scope.restriction()

    m When "a Play & Earn call is refused because the account is restricted"
    refusals.emit(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m Then "the refusal's word stands"
    assertEquals(RESTRICTED, restriction.state.value)
  }

  @Test
  fun `A stale refusal for the last account is undone by the backend`() = coScenario { scope ->
    m Given "the last account's refusal still on its way when another account signs in"
    val restriction = scope.restriction()
    restriction.forget()

    m When "the refusal arrives for the new account, which the backend says is fine"
    refusals.emit(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m Then "the backend is asked at once and the new account is not restricted"
    assertEquals(1, accountStatus.calls)
    assertEquals(NOT_RESTRICTED, restriction.state.value)
  }

  @Test
  fun `A refusal while a call is on its way wins over that call's answer`() =
    coScenario { scope ->
      m Given "a status call asked before the ban, still on its way"
      val beforeBan = accountStatus.holdNextAnswer()
      val restriction = scope.restriction()
      restriction.refresh()

      m When "a refusal arrives, then the old call answers 'not restricted'"
      accountStatus.answer = PaEAccountStatus.RESTRICTED
      refusals.emit(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))
      beforeBan.complete(PaEAccountStatus.NOT_RESTRICTED)

      m Then "a fresh call was made, and the old answer did not lift the restriction"
      assertEquals(2, accountStatus.calls)
      assertEquals(RESTRICTED, restriction.state.value)
    }

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    names = ["ACCOUNT_RESTRICTED"],
    mode = EnumSource.Mode.EXCLUDE,
  )
  fun `Any other refusal leaves the account alone`(code: PaERefusalCode) = coScenario { scope ->
    m Given "an account the backend says is fine"
    val restriction = scope.restriction()
    restriction.refresh()

    m When "a Play & Earn call is refused with $code"
    refusals.emit(PaERefusal(code))

    m Then "the account is still not restricted, and nothing more was asked"
    assertEquals(NOT_RESTRICTED, restriction.state.value)
    assertEquals(1, accountStatus.calls)
  }

  @Test
  fun `A later not restricted answer lifts the restriction`() = coScenario { scope ->
    m Given "an account restricted a minute ago"
    accountStatus.answer = PaEAccountStatus.RESTRICTED
    val restriction = scope.restriction()
    restriction.refresh()

    m When "support un-bans it and a Play & Earn screen opens a minute later"
    accountStatus.answer = PaEAccountStatus.NOT_RESTRICTED
    now += REFRESH_INTERVAL_MS
    restriction.refresh()

    m Then "the account gets in again"
    assertEquals(NOT_RESTRICTED, restriction.state.value)
  }

  @Test
  fun `Screens opened within a minute ask once`() = coScenario { scope ->
    m Given "the status asked when a Play & Earn screen opened"
    val restriction = scope.restriction()
    restriction.refresh()

    m When "the user opens other Play & Earn screens within the minute"
    now += REFRESH_INTERVAL_MS - 1
    restriction.refresh()
    restriction.refresh()

    m Then "the backend was asked once"
    assertEquals(1, accountStatus.calls)
  }

  @Test
  fun `A screen opened after the minute asks again`() = coScenario { scope ->
    m Given "the status asked when a Play & Earn screen opened"
    val restriction = scope.restriction()
    restriction.refresh()

    m When "the user opens a Play & Earn screen a minute later"
    now += REFRESH_INTERVAL_MS
    restriction.refresh()

    m Then "the backend is asked again"
    assertEquals(2, accountStatus.calls)
  }

  @Test
  fun `A screen opened while the status is on its way asks nothing more`() = coScenario { scope ->
    m Given "a status call that has not answered yet"
    accountStatus.holdNextAnswer()
    val restriction = scope.restriction()
    restriction.refresh()

    m When "another Play & Earn screen opens"
    restriction.refresh()

    m Then "the backend was asked once"
    assertEquals(1, accountStatus.calls)
  }

  @Test
  fun `A signed-out user is never asked about`() = coScenario { scope ->
    m Given "nobody signed in"
    signedIn = { false }
    val restriction = scope.restriction()

    m When "a Play & Earn screen opens and a stray account refusal arrives"
    restriction.refresh()
    refusals.emit(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m Then "nothing is asked and nothing is restricted"
    assertEquals(0, accountStatus.calls)
    assertEquals(UNKNOWN, restriction.state.value)
  }

  @Test
  fun `An unreadable sign-in is treated as signed out`() = coScenario { scope ->
    m Given "the stored wallet cannot be read"
    signedIn = { throw IllegalStateException("DataStore broken") }
    val restriction = scope.restriction()

    m When "a Play & Earn screen opens and an account refusal arrives"
    restriction.refresh()
    refusals.emit(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m Then "nothing crashes, nothing is asked and nothing is restricted"
    assertEquals(0, accountStatus.calls)
    assertEquals(UNKNOWN, restriction.state.value)
  }

  @Test
  fun `Refusals are still heard after one could not be handled`() = coScenario { scope ->
    m Given "a refusal arriving while the stored wallet cannot be read"
    var broken = true
    signedIn = { if (broken) throw IllegalStateException("DataStore broken") else true }
    val restriction = scope.restriction()
    refusals.emit(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m When "the wallet can be read again and another account refusal arrives"
    broken = false
    accountStatus.answer = PaEAccountStatus.RESTRICTED
    refusals.emit(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m Then "the account is restricted"
    assertEquals(RESTRICTED, restriction.state.value)
  }

  @Test
  fun `Signing in after browsing signed out asks at once`() = coScenario { scope ->
    m Given "a Play & Earn screen opened while signed out"
    signedIn = { false }
    accountStatus.answer = PaEAccountStatus.RESTRICTED
    val restriction = scope.restriction()
    restriction.refresh()

    m When "the user signs in and opens a Play & Earn screen within the minute"
    signedIn = { true }
    restriction.forget()
    restriction.refresh()

    m Then "the backend is asked and the restriction applies"
    assertEquals(1, accountStatus.calls)
    assertEquals(RESTRICTED, restriction.state.value)
  }

  @Test
  fun `An account change forgets the restriction`() = coScenario { scope ->
    m Given "a restricted account"
    accountStatus.answer = PaEAccountStatus.RESTRICTED
    val restriction = scope.restriction()
    restriction.refresh()

    m When "the user signs out or another account signs in"
    restriction.forget()

    m Then "nothing is known about the next account, and a held screen waits for it"
    assertEquals(UNKNOWN, restriction.state.value)
    assertFalse(restriction.settled.value)
  }

  @Test
  fun `The next account is asked at once after an account change`() = coScenario { scope ->
    m Given "a restricted account asked a moment ago"
    accountStatus.answer = PaEAccountStatus.RESTRICTED
    val restriction = scope.restriction()
    restriction.refresh()

    m When "another account signs in and opens a Play & Earn screen within the minute"
    restriction.forget()
    accountStatus.answer = PaEAccountStatus.NOT_RESTRICTED
    restriction.refresh()

    m Then "the backend is asked about the new account, which is fine"
    assertEquals(2, accountStatus.calls)
    assertEquals(NOT_RESTRICTED, restriction.state.value)
  }

  @Test
  fun `A late answer for the last account does not mark the new one`() = coScenario { scope ->
    m Given "a status call for the last account still on its way"
    val lastAnswer = accountStatus.holdNextAnswer()
    val restriction = scope.restriction()
    restriction.refresh()

    m When "the account changes and only then the last account's answer arrives: restricted"
    restriction.forget()
    lastAnswer.complete(PaEAccountStatus.RESTRICTED)

    m Then "the new account is not marked restricted"
    assertEquals(UNKNOWN, restriction.state.value)
  }

  private fun TestScope.restriction() = PaEAccountRestriction(
    accountStatus = accountStatus,
    isSignedIn = { signedIn() },
    refusals = refusals,
    nowMillis = { now },
    scope = CoroutineScope(
      backgroundScope.coroutineContext + UnconfinedTestDispatcher(testScheduler)
    ),
  )
}
