package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PaESessionRecoveryTest {

  @Test
  fun `A P&E game still in front without a session gets a new one`() = scenario {
    m Given "the server dropped the session of the game still in front"
    val active = emptyList<String>()

    m When "the poll decides"
    val recreate = needsSessionRecreation(GAME, isPaEGame = true, activeSessionPackages = active)

    m Then "a session is created again"
    assertEquals(true, recreate)
  }

  @Test
  fun `A game with a session, or an app outside P&E, gets none`() = scenario {
    m Given "a game with a live session, and an app that is not a P&E game"
    val withSession = listOf(GAME)

    m When "the poll decides for each"
    val decisions = listOf(
      needsSessionRecreation(GAME, isPaEGame = true, activeSessionPackages = withSession),
      needsSessionRecreation(OTHER, isPaEGame = false, activeSessionPackages = emptyList()),
    )

    m Then "nothing is created"
    assertEquals(listOf(false, false), decisions)
  }

  // AND-964: with no session (a refused or failed start) the service must keep seeing the game
  // in front past the usage query's 60-second default window, or it never asks again.
  @Test
  fun `The foreground query starts when the session opened, else when the game came to front`() =
    scenario {
      m Given "a session opened at 1000, and a game held in front since 2000 without one"
      m When "the query start is chosen with a session, without one, and with neither"
      val starts = listOf(
        foregroundQueryStart(sessionOpenedAtMillis = 1_000L, inFrontSinceMillis = 2_000L),
        foregroundQueryStart(sessionOpenedAtMillis = null, inFrontSinceMillis = 2_000L),
        foregroundQueryStart(sessionOpenedAtMillis = null, inFrontSinceMillis = null),
      )

      m Then "the session's time, then the time in front, then the default window (null)"
      assertEquals(listOf(1_000L, 2_000L, null), starts)
    }

  @Test
  fun `The time in front is kept while the same package stays in front`() = scenario {
    m Given "a game seen in front since 2000"
    val since = 2_000L

    m When "a later poll sees the same game in front"
    val next = nextInFrontSince(
      previousPackage = GAME,
      foregroundPackage = GAME,
      previousSinceMillis = since,
      nowMillis = 90_000L,
    )

    m Then "it is still 2000, so the game's resume stays inside the query"
    assertEquals(2_000L, next)
  }

  @Test
  fun `The time in front starts over for a new package, or after the foreground was lost`() =
    scenario {
      m Given "a game seen in front since 2000"
      m When "another package comes to front, or the same one after the foreground was lost"
      val times = listOf(
        nextInFrontSince(
          previousPackage = GAME,
          foregroundPackage = OTHER,
          previousSinceMillis = 2_000L,
          nowMillis = 90_000L,
        ),
        nextInFrontSince(
          previousPackage = GAME,
          foregroundPackage = GAME,
          previousSinceMillis = null,
          nowMillis = 95_000L,
        ),
      )

      m Then "each starts at the poll that saw it"
      assertEquals(listOf(90_000L, 95_000L), times)
    }

  private companion object {
    const val GAME = "com.game"
    const val OTHER = "com.other"
  }
}
