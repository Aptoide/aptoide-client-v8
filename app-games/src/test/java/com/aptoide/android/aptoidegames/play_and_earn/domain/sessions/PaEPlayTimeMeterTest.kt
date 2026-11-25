package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.test.gherkin.scenario
import cm.aptoide.pt.usage_stats.PackageUsageState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class PaEPlayTimeMeterTest {

  private var now = 5_000_000L
  private var screenFails = false
  private val screen = object : PaEScreenState {
    var interactive = true
    var locked = false
    override val isInteractive: Boolean
      get() = if (screenFails) error("system service gone") else interactive
    override val isKeyguardLocked: Boolean get() = locked
  }
  private val meter = PaEPlayTimeMeter(
    clock = { now },
    screen = screen,
    maxCreditPerPollMillis = 15_000L,
  )

  /** Advances the clock by [millis] and polls with [visible] as the app alone on screen. */
  private fun pollAfter(millis: Long, visible: String? = GAME): PaEPlayMeasurement {
    now += millis
    return meter.measure(visible)
  }

  @Test
  fun `Normal play earns the real time between polls`() = scenario {
    m Given "the game alone on screen at every poll, with irregular gaps"
    val first = meter.measure(GAME)

    m When "three more polls happen"
    val played = listOf(pollAfter(10_000), pollAfter(10_300), pollAfter(9_800))

    m Then "the first poll counts nothing and the others count exactly the time elapsed"
    assertEquals(0L, first.playedMillis)
    assertEquals(listOf(10_000L, 10_300L, 9_800L), played.map { it.playedMillisFor(GAME) })
  }

  @Test
  fun `A locked screen earns nothing, nor the interval after unlocking`() = scenario {
    m Given "the game being played"
    meter.measure(GAME)

    m When "the phone is locked at the next poll, then unlocked for two polls"
    screen.locked = true
    val locked = pollAfter(10_000)
    screen.locked = false
    val firstUnlocked = pollAfter(10_000)
    val secondUnlocked = pollAfter(10_000)

    m Then "only the interval with the game played at both ends counts"
    assertFalse(locked.screenInUse)
    assertNull(locked.playingPackage)
    assertEquals(listOf(0L, 0L, 10_000L), listOf(locked, firstUnlocked, secondUnlocked).map {
      it.playedMillisFor(GAME)
    })
  }

  @Test
  fun `A screen that is off earns nothing`() = scenario {
    m Given "the game being played"
    meter.measure(GAME)

    m When "the screen is off at the next poll, even if the events still show the game"
    screen.interactive = false
    val off = pollAfter(10_000)

    m Then "nothing counts"
    assertFalse(off.screenInUse)
    assertEquals(0L, off.playedMillisFor(GAME))
  }

  @Test
  fun `A screen state that can't be read counts as not in use`() = scenario {
    m Given "the game being played"
    meter.measure(GAME)

    m When "reading the screen state throws at the next poll"
    screenFails = true
    val failed = pollAfter(10_000)

    m Then "nothing counts and nothing crashes"
    assertFalse(failed.screenInUse)
    assertEquals(0L, failed.playedMillisFor(GAME))
  }

  @Test
  fun `Another app in front or a shared screen earns nothing`() = scenario {
    m Given "the game being played"
    meter.measure(GAME)

    m When "another app is in front, then the screen is shared (no app alone), then the game again"
    val otherApp = pollAfter(10_000, visible = OTHER)
    val shared = pollAfter(10_000, visible = null)
    val back = pollAfter(10_000)
    val playing = pollAfter(10_000)

    m Then "the game earns only from the second poll with it alone on screen"
    assertEquals(listOf(0L, 0L, 0L, 10_000L), listOf(otherApp, shared, back, playing).map {
      it.playedMillisFor(GAME)
    })
    assertEquals(0L, otherApp.playedMillisFor(OTHER))
  }

  @Test
  fun `A long gap between polls counts at most one poll interval plus margin`() = scenario {
    m Given "the game being played, with a poll whose work was instant"
    meter.measure(GAME)
    meter.onPollWorkDone()

    m When "the poll loop was suspended for ten minutes with the game in front at both ends"
    val afterGap = pollAfter(600_000)

    m Then "only the cap counts"
    assertEquals(15_000L, afterGap.playedMillisFor(GAME))
  }

  @Test
  fun `A slow poll does not eat play time`() = scenario {
    m Given "the game being played, and that poll's heartbeat took 20 s on a slow network"
    meter.measure(GAME)
    now += 20_000
    meter.onPollWorkDone()

    m When "the next poll comes 1 s after that work ended"
    val next = pollAfter(1_000)

    m Then "the real 21 s count, though above the 15 s cap"
    assertEquals(21_000L, next.playedMillisFor(GAME))
  }

  @Test
  fun `A slow poll followed by a freeze credits only the cap plus that work`() = scenario {
    m Given "the game being played, and that poll's work took 2 s"
    meter.measure(GAME)
    now += 2_000
    meter.onPollWorkDone()

    m When "the loop then froze for ten minutes"
    val afterGap = pollAfter(600_000)

    m Then "the cap plus the 2 s of work"
    assertEquals(17_000L, afterGap.playedMillisFor(GAME))
  }

  @Test
  fun `The work allowance applies to the next interval only`() = scenario {
    m Given "a 20 s poll followed by a normal one"
    meter.measure(GAME)
    now += 20_000
    meter.onPollWorkDone()
    pollAfter(1_000)
    meter.onPollWorkDone()

    m When "the loop then froze for ten minutes"
    val afterGap = pollAfter(600_000)

    m Then "only the plain cap counts"
    assertEquals(15_000L, afterGap.playedMillisFor(GAME))
  }

  @Test
  fun `A poll that failed before measuring does not widen the cap`() = scenario {
    m Given "a normal poll, then one that failed before measuring 10 s later"
    meter.measure(GAME)
    meter.onPollWorkDone()
    now += 10_000
    meter.onPollWorkDone()

    m When "the loop then froze for ten minutes"
    val afterGap = pollAfter(600_000)

    m Then "only the plain cap counts"
    assertEquals(15_000L, afterGap.playedMillisFor(GAME))
  }

  @Test
  fun `Time is never negative`() = scenario {
    m Given "the game being played"
    meter.measure(GAME)

    m When "the clock reads earlier than the previous poll"
    val backwards = pollAfter(-3_000)

    m Then "nothing counts"
    assertEquals(0L, backwards.playedMillisFor(GAME))
  }

  @Test
  fun `A screen-off between two polls drops that interval`() = scenario {
    m Given "the game being played"
    meter.measure(GAME)

    m When "the screen turns off and on again between two polls"
    now += 4_000
    meter.onScreenOff()
    val afterFlash = pollAfter(6_000)
    val next = pollAfter(10_000)

    m Then "the interrupted interval counts nothing, the next one counts again"
    assertEquals(0L, afterFlash.playedMillisFor(GAME))
    assertEquals(10_000L, next.playedMillisFor(GAME))
  }

  @Test
  fun `Switching games starts counting the new game from its second poll`() = scenario {
    m Given "one game being played"
    meter.measure(GAME)
    pollAfter(10_000)

    m When "another game takes over"
    val switched = pollAfter(10_000, visible = OTHER)
    val playingOther = pollAfter(10_000, visible = OTHER)

    m Then "the switch interval goes to nobody"
    assertEquals(0L, switched.playedMillisFor(OTHER))
    assertEquals(0L, switched.playedMillisFor(GAME))
    assertEquals(10_000L, playingOther.playedMillisFor(OTHER))
    assertEquals(0L, playingOther.playedMillisFor(GAME))
  }

  @Test
  fun `Reset forgets the previous poll`() = scenario {
    m Given "the game being played"
    meter.measure(GAME)

    m When "the meter is reset, as when the poll loop restarts"
    meter.reset()
    val first = pollAfter(10_000)

    m Then "the first poll after the reset counts nothing"
    assertEquals(0L, first.playedMillisFor(GAME))
  }

  @Test
  fun `A game sharing the screen reaches the meter as not played`() = scenario {
    m Given "the game being played"
    meter.measure(PackageUsageState.ForegroundPackage(GAME).soleVisiblePackage())

    m When "the next poll sees it in split-screen with another resumed app"
    val shared = PackageUsageState.ForegroundPackage(GAME, sharesScreen = true)
    val split = pollAfter(10_000, visible = shared.soleVisiblePackage())

    m Then "nothing counts"
    assertNull(split.playingPackage)
    assertEquals(0L, split.playedMillisFor(GAME))
  }

  @Test
  fun `A locked screen with the game still resumed reaches the meter as not played`() =
    scenario {
      m Given "the game being played"
      val alone = PackageUsageState.ForegroundPackage(GAME)
      meter.measure(alone.soleVisiblePackage())

      m When "the events still show the game alone, but the phone is locked"
      screen.locked = true
      val locked = pollAfter(10_000, visible = alone.soleVisiblePackage())

      m Then "nothing counts"
      assertNull(locked.playingPackage)
      assertEquals(0L, locked.playedMillisFor(GAME))
    }

  @Test
  fun `Only a game alone in the foreground is a play candidate`() = scenario {
    m Given "every usage state"
    val states = listOf(
      PackageUsageState.ForegroundPackage(GAME),
      PackageUsageState.ForegroundPackage(GAME, sharesScreen = true),
      PackageUsageState.NoForegroundPackage,
      PackageUsageState.Error,
    )

    m When "each is mapped to what the meter is told"
    val candidates = states.map { it.soleVisiblePackage() }

    m Then "only the unshared foreground package is passed on"
    assertEquals(listOf(GAME, null, null, null), candidates)
  }

  private companion object {
    const val GAME = "com.game"
    const val OTHER = "com.other"
  }
}
