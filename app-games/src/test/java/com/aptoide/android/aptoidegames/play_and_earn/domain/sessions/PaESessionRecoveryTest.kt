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

  private companion object {
    const val GAME = "com.game"
    const val OTHER = "com.other"
  }
}
