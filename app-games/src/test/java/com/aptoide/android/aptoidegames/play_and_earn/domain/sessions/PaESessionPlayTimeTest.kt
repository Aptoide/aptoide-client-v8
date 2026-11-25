package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PaESessionPlayTimeTest {

  private val session = PaESession(
    sessionId = "session",
    packageName = "com.game",
    ttlSeconds = 300,
    missions = null,
    heartbeatIntervalSeconds = 15,
  )

  @Test
  fun `A heartbeat claims whole seconds and keeps the remainder for the next one`() = scenario {
    m Given "two measured intervals of 10.4 s"
    session.addPlayTime(10_400)
    session.addPlayTime(10_400)
    val claimed = session.secondsToClaim

    m When "the server accepts the heartbeat, and another 10.4 s is measured"
    session.onPlayTimeSent(claimed)
    session.addPlayTime(10_400)

    m Then "20 s were claimed and the 0.8 s left over is not lost"
    assertEquals(20, claimed)
    assertEquals(11, session.secondsToClaim)
    assertEquals(11_200L, session.unsentPlayMillis)
  }

  @Test
  fun `A failed heartbeat keeps its time for the next one`() = scenario {
    m Given "20 s measured and a heartbeat that failed (nothing marked as sent)"
    session.addPlayTime(20_000)

    m When "another 20 s is measured"
    session.addPlayTime(20_000)

    m Then "the next heartbeat claims both"
    assertEquals(40, session.secondsToClaim)
  }

  @Test
  fun `A heartbeat claims at most 60 s and the rest waits for the next one`() = scenario {
    m Given "a 60 s heartbeat interval: 70 s measured since the last accepted beat"
    repeat(7) { session.addPlayTime(10_000) }
    val claimed = session.secondsToClaim

    m When "the server accepts the 60 s claimed"
    session.onPlayTimeSent(claimed)

    m Then "the 10 s left over are claimed next, not lost"
    assertEquals(60, claimed)
    assertEquals(10, session.secondsToClaim)
  }

  @Test
  fun `Unsent time is kept up to 120 s, beyond that it is dropped`() = scenario {
    m Given "a long run of failed heartbeats"
    repeat(10) { session.addPlayTime(20_000) }

    m When "the unsent time is read"
    val unsent = session.unsentPlayMillis

    m Then "it is capped at 120 s, still claimed 60 s per beat"
    assertEquals(120_000L, unsent)
    assertEquals(60, session.secondsToClaim)
  }

  @Test
  fun `Negative time is ignored`() = scenario {
    m Given "5 s measured"
    session.addPlayTime(5_000)

    m When "a negative amount is added"
    session.addPlayTime(-3_000)

    m Then "nothing is taken away"
    assertEquals(5_000L, session.unsentPlayMillis)
  }
}
