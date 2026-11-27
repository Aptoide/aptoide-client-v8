package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.test.gherkin.coScenario
import cm.aptoide.pt.test.gherkin.scenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

// AND-950: the one record of the refusals that keep Play & Earn games from playing.
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaESessionRefusalsTest {

  private var now = 1_000_000L
  private val refusals = PaESessionRefusals { now }

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    names = ["ACCOUNT_RESTRICTED", "DEVICE_NOT_SUPPORTED", "UPDATE_REQUIRED", "VPN_NOT_ALLOWED"]
  )
  fun `An account, device, update or VPN refusal holds for every game`(code: PaERefusalCode) =
    coScenario {
      m Given "one game refused with $code"
      refusals.record(GAME, PaERefusal(code))

      m When "another game's Rewards tab and session start read the refusals"
      val shown = refusals.observe(OTHER_GAME).first()
      val blocking = refusals.blockingRefusalOf(OTHER_GAME)

      m Then "the other game is refused too, and every game is"
      assertEquals(PaERefusal(code), shown)
      assertEquals(PaERefusal(code), blocking)
      assertEquals(PaERefusal(code), refusals.everyGameRefusal())
    }

  @Test
  fun `A region refusal holds for its game only`() = coScenario {
    m Given "one game refused for the user's region"
    refusals.record(GAME, PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED))

    m When "both games read the refusals"
    val own = refusals.observe(GAME).first()
    val other = refusals.observe(OTHER_GAME).first()

    m Then "only that game is refused, and not every game"
    assertEquals(PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED), own)
    assertNull(other)
    assertNull(refusals.everyGameRefusal())
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    mode = EnumSource.Mode.EXCLUDE,
    names = [
      "ACCOUNT_RESTRICTED", "REGION_NOT_SUPPORTED", "DEVICE_NOT_SUPPORTED", "UPDATE_REQUIRED",
      "VPN_NOT_ALLOWED",
    ]
  )
  fun `A refusal that does not block play is not kept`(code: PaERefusalCode) = coScenario {
    m Given "a game refused with $code"
    m When "it is recorded"
    refusals.record(GAME, PaERefusal(code))

    m Then "nothing is kept or shown"
    assertNull(refusals.observe(GAME).first())
    assertNull(refusals.blockingRefusalOf(GAME))
  }

  @Test
  fun `A refusal stops blocking after 10 minutes, but stays shown`() = coScenario {
    m Given "a game refused for its region and a device refusal, both now"
    refusals.record(GAME, PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED))
    refusals.record(OTHER_GAME, PaERefusal(PaERefusalCode.DEVICE_NOT_SUPPORTED))
    val justBefore = now + PaESessionRefusals.ASK_AGAIN_AFTER_MILLIS - 1

    m When "just under, then exactly 10 minutes pass"
    now = justBefore
    val blockingBefore = refusals.blockingRefusalOf(GAME)
    now += 1
    val blockingAfter = refusals.blockingRefusalOf(GAME)

    m Then "the backend may be asked again; the Rewards tab keeps the last answer"
    assertEquals(PaERefusal(PaERefusalCode.DEVICE_NOT_SUPPORTED), blockingBefore)
    assertNull(blockingAfter)
    assertNull(refusals.everyGameRefusal())
    assertEquals(PaERefusal(PaERefusalCode.DEVICE_NOT_SUPPORTED), refusals.observe(GAME).first())
  }

  @Test
  fun `A VPN refusal stops blocking after a minute, but stays shown`() = coScenario {
    m Given "every game refused because the phone reports a VPN"
    refusals.record(GAME, PaERefusal(PaERefusalCode.VPN_NOT_ALLOWED))
    val justBefore = now + PaESessionRefusals.VPN_ASK_AGAIN_AFTER_MILLIS - 1

    m When "just under, then exactly a minute passes"
    now = justBefore
    val blockingBefore = refusals.blockingRefusalOf(GAME)
    now += 1
    val blockingAfter = refusals.blockingRefusalOf(GAME)

    m Then "the backend may be asked again after a minute, not ten; the tab keeps the answer"
    assertEquals(60_000L, PaESessionRefusals.VPN_ASK_AGAIN_AFTER_MILLIS)
    assertEquals(PaERefusal(PaERefusalCode.VPN_NOT_ALLOWED), blockingBefore)
    assertNull(blockingAfter)
    assertNull(refusals.everyGameRefusal())
    assertEquals(PaERefusal(PaERefusalCode.VPN_NOT_ALLOWED), refusals.observe(GAME).first())
  }

  @Test
  fun `Only a VPN refusal waits for the user`() = scenario {
    m Given "every refusal code"
    val codes = PaERefusalCode.entries

    m When "each is asked whether the user lifts it in a moment"
    val waiting = codes.filter { it.waitsForTheUser }

    m Then "only the VPN does: tracking goes on and asks again soon"
    assertEquals(listOf(PaERefusalCode.VPN_NOT_ALLOWED), waiting)
  }

  @Test
  fun `A started session clears a VPN refusal`() = coScenario {
    m Given "every game refused for a VPN"
    refusals.record(GAME, PaERefusal(PaERefusalCode.VPN_NOT_ALLOWED))

    m When "the VPN is off and the backend accepts a session"
    refusals.onSessionStarted(GAME)

    m Then "no game is refused or shows it any more"
    assertNull(refusals.everyGameRefusal())
    assertNull(refusals.observe(OTHER_GAME).first())
  }

  @Test
  fun `A started session clears its game and every-game refusals`() = coScenario {
    m Given "two games refused for their region and an update refusal"
    refusals.record(GAME, PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED))
    refusals.record(OTHER_GAME, PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED))
    refusals.record(GAME, PaERefusal(PaERefusalCode.UPDATE_REQUIRED))

    m When "the backend accepts one game's session"
    refusals.onSessionStarted(GAME)

    m Then "that game and every game are clear; the other game's region still holds"
    assertNull(refusals.observe(GAME).first())
    assertNull(refusals.everyGameRefusal())
    assertEquals(
      PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED),
      refusals.observe(OTHER_GAME).first()
    )
  }

  @Test
  fun `Clearing forgets everything`() = scenario {
    m Given "a region refusal and a restricted account"
    refusals.record(GAME, PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED))
    refusals.record(OTHER_GAME, PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m When "the refusals are cleared"
    refusals.clear()

    m Then "nothing blocks any game"
    assertNull(refusals.blockingRefusalOf(GAME))
    assertNull(refusals.blockingRefusalOf(OTHER_GAME))
  }

  private companion object {
    const val GAME = "com.game"
    const val OTHER_GAME = "com.other.game"
  }
}
