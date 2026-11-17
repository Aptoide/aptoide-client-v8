package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Hackathon 2026 (T6): the rules behind what the "My progress" screen writes.
internal class MyProgressFormatTest {

  @Test
  fun `Play time is shown in hours and minutes`() = scenario {
    m Given "an hour and a half, exactly two hours, and under an hour"
    m When "each is split"
    m Then "the hours and the minutes left over come out"
    assertEquals(PlayTime(hours = 1, minutes = 30), playTimeOf(5400))
    assertEquals(PlayTime(hours = 2, minutes = 0), playTimeOf(7200))
    assertEquals(PlayTime(hours = 0, minutes = 7), playTimeOf(479))
  }

  @Test
  fun `A few seconds of play time already count as a minute`() = scenario {
    m Given "a player who has just started"
    m When "30 seconds are split"
    m Then "it reads as one minute, and no play time at all as zero"
    assertEquals(PlayTime(hours = 0, minutes = 1), playTimeOf(30))
    assertEquals(PlayTime(hours = 0, minutes = 0), playTimeOf(0))
    assertEquals(PlayTime(hours = 0, minutes = 0), playTimeOf(-5))
  }

  @Test
  fun `My progress is offered to signed-in players where Play and Earn is shown`() = scenario {
    m Given "the four combinations of Play and Earn visible and signed in"
    m When "the visibility is computed"
    m Then "only a signed-in player with Play and Earn gets the entry"
    assertTrue(showPaEMyProgress(playAndEarnVisible = true, signedIn = true))
    assertFalse(showPaEMyProgress(playAndEarnVisible = true, signedIn = false))
    assertFalse(showPaEMyProgress(playAndEarnVisible = false, signedIn = true))
    assertFalse(showPaEMyProgress(playAndEarnVisible = false, signedIn = false))
  }
}
