package com.aptoide.android.aptoidegames.play_and_earn.domain

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.play_and_earn.sessions.data.PaEAccountStatusRepository
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestrictionState.NOT_RESTRICTED
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestrictionState.RESTRICTED
import com.aptoide.android.aptoidegames.play_and_earn.domain.PaEAccountRestrictionState.UNKNOWN
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

// AND-951: one in-memory answer to "is this account restricted?", for every Play & Earn screen.
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaEAccountRestrictionTest {

  private class FakeAccountStatus : PaEAccountStatusRepository {
    var calls = 0
    var answer = false

    // When set, the next answer waits for it.
    var pending: CompletableDeferred<Boolean>? = null

    override suspend fun isRestricted(): Boolean {
      calls++
      return pending?.also { pending = null }?.await() ?: answer
    }
  }

  private val accountStatus = FakeAccountStatus()
  private val refusals = MutableSharedFlow<PaERefusal>(extraBufferCapacity = 8)
  private var signedIn = true
  private var now = 0L

  @Test
  fun `A restricted answer restricts the account`() = coScenario { scope ->
    m Given "the backend says the signed-in account is restricted"
    accountStatus.answer = true
    val restriction = scope.restriction()

    m When "a Play & Earn screen opens"
    restriction.refresh()

    m Then "the account is restricted"
    assertEquals(RESTRICTED, restriction.state.value)
  }

  @Test
  fun `A not restricted answer opens the account`() = coScenario { scope ->
    m Given "the backend says the signed-in account is fine"
    accountStatus.answer = false
    val restriction = scope.restriction()

    m When "a Play & Earn screen opens"
    restriction.refresh()

    m Then "the account is not restricted"
    assertEquals(NOT_RESTRICTED, restriction.state.value)
  }

  @Test
  fun `Nothing is known while the first answer is on its way`() = coScenario { scope ->
    m Given "a status call that has not answered yet"
    accountStatus.pending = CompletableDeferred()
    val restriction = scope.restriction()

    m When "a Play & Earn screen opens"
    restriction.refresh()

    m Then "the state is unknown: the screens show their content meanwhile"
    assertEquals(UNKNOWN, restriction.state.value)
  }

  @Test
  fun `An account refusal from any call restricts the account`() = coScenario { scope ->
    m Given "an account nobody asked the status of yet"
    val restriction = scope.restriction()

    m When "a Play & Earn call is refused because the account is restricted"
    refusals.emit(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m Then "the account is restricted without asking the status"
    assertEquals(RESTRICTED, restriction.state.value)
    assertEquals(0, accountStatus.calls)
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

    m Then "the account is still not restricted"
    assertEquals(NOT_RESTRICTED, restriction.state.value)
  }

  @Test
  fun `A later not restricted answer lifts the restriction`() = coScenario { scope ->
    m Given "an account restricted a minute ago"
    accountStatus.answer = true
    val restriction = scope.restriction()
    restriction.refresh()

    m When "support un-bans it and a Play & Earn screen opens a minute later"
    accountStatus.answer = false
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
    accountStatus.pending = CompletableDeferred()
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
    signedIn = false
    val restriction = scope.restriction()

    m When "a Play & Earn screen opens and a stray account refusal arrives"
    restriction.refresh()
    refusals.emit(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m Then "nothing is asked and nothing is restricted"
    assertEquals(0, accountStatus.calls)
    assertEquals(UNKNOWN, restriction.state.value)
  }

  @Test
  fun `Signing in after browsing signed out asks at once`() = coScenario { scope ->
    m Given "a Play & Earn screen opened while signed out"
    signedIn = false
    accountStatus.answer = true
    val restriction = scope.restriction()
    restriction.refresh()

    m When "the user signs in and opens a Play & Earn screen within the minute"
    signedIn = true
    restriction.forget()
    restriction.refresh()

    m Then "the backend is asked and the restriction applies"
    assertEquals(1, accountStatus.calls)
    assertEquals(RESTRICTED, restriction.state.value)
  }

  @Test
  fun `An account change forgets the restriction`() = coScenario { scope ->
    m Given "a restricted account"
    accountStatus.answer = true
    val restriction = scope.restriction()
    restriction.refresh()

    m When "the user signs out or another account signs in"
    restriction.forget()

    m Then "nothing is known about the next account"
    assertEquals(UNKNOWN, restriction.state.value)
  }

  @Test
  fun `The next account is asked at once after an account change`() = coScenario { scope ->
    m Given "a restricted account asked a moment ago"
    accountStatus.answer = true
    val restriction = scope.restriction()
    restriction.refresh()

    m When "another account signs in and opens a Play & Earn screen within the minute"
    restriction.forget()
    accountStatus.answer = false
    restriction.refresh()

    m Then "the backend is asked about the new account, which is fine"
    assertEquals(2, accountStatus.calls)
    assertEquals(NOT_RESTRICTED, restriction.state.value)
  }

  @Test
  fun `A late answer for the last account does not mark the new one`() = coScenario { scope ->
    m Given "a status call for the last account still on its way"
    val lastAnswer = CompletableDeferred<Boolean>()
    accountStatus.pending = lastAnswer
    val restriction = scope.restriction()
    restriction.refresh()

    m When "the account changes and only then the last account's answer arrives: restricted"
    restriction.forget()
    lastAnswer.complete(true)

    m Then "the new account is not marked restricted"
    assertEquals(UNKNOWN, restriction.state.value)
  }

  private fun TestScope.restriction() = PaEAccountRestriction(
    accountStatus = accountStatus,
    isSignedIn = { signedIn },
    refusals = refusals,
    nowMillis = { now },
    scope = CoroutineScope(
      backgroundScope.coroutineContext + UnconfinedTestDispatcher(testScheduler)
    ),
  )

  private companion object {
    const val REFRESH_INTERVAL_MS = 60_000L
  }
}
