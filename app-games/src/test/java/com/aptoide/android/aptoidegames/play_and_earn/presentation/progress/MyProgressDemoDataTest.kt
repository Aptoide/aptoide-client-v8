package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Hackathon 2026: the demo data has to hold together on stage.
internal class MyProgressDemoDataTest {

  private val dice = "com.aptoide.diceroll.sdk"

  @Test
  fun `A game's demo leaderboard is sixty players, best first`() = scenario {
    m Given "one game, this week"
    m When "its players are generated"
    val players = demoLeaderboardPlayers(dice, LeaderboardPeriod.WEEK)

    m Then "sixty distinct players in order, the best around 3200 Units and nobody is the player"
    assertEquals(60, players.size)
    assertEquals(60, players.map { it.name }.distinct().size)
    assertEquals(players.map { it.units }.sortedDescending(), players.map { it.units })
    assertTrue(players.first().units in 3000..3400)
    assertTrue(players.last().units > 0)
    assertTrue(players.none { it.isMe })
    assertTrue(players.all { it.color in 0 until DEMO_AVATAR_COLORS })
  }

  @Test
  fun `Each game and each period has its own players`() = scenario {
    m Given "three games this week, and one of them over the three periods"
    val games = listOf(dice, "com.mobile.legends", DEMO_DUCK_PACKAGE)
      .map { demoLeaderboardPlayers(it, LeaderboardPeriod.WEEK) }
    val periods = LeaderboardPeriod.entries.map { demoLeaderboardPlayers(dice, it) }

    m When "they are compared"
    m Then "no two are the same, the longer the period the more Units, and each is repeatable"
    assertEquals(3, games.map { list -> list.map { it.name to it.units } }.distinct().size)
    assertEquals(3, periods.map { list -> list.map { it.name to it.units } }.distinct().size)
    assertTrue(periods[0].first().units < periods[1].first().units)
    assertTrue(periods[1].first().units < periods[2].first().units)
    assertEquals(games[0], demoLeaderboardPlayers(dice, LeaderboardPeriod.WEEK))
    assertNotEquals(games[0], games[1])
  }

  @Test
  fun `In Duck Survival the player starts below the top ten and climbs by claiming`() = scenario {
    m Given "Duck Survival this week, where the player has earned 340 Units before"
    val duck = demoDuckSurvival()
    val players = demoLeaderboardPlayers(duck.packageName, LeaderboardPeriod.WEEK)

    m When "nothing is claimed, and then the two ready missions are"
    val before = leaderboardView(
      players, leaderboardUnits(duck, LeaderboardPeriod.WEEK, emptySet()), "You",
    )
    val claimed = duck.missions.filter { it.completed }.map { it.id }.toSet()
    val after = leaderboardView(
      players, leaderboardUnits(duck, LeaderboardPeriod.WEEK, claimed), "You",
    )

    m Then "they start outside the top ten with their own row, and end higher up"
    assertTrue(before.isMeBelowTop)
    assertEquals(340L, before.me?.player?.units)
    assertEquals(540L, after.me?.player?.units)
    assertTrue(after.me!!.rank < before.me!!.rank)
    assertEquals(LeaderboardPeriod.entries.toSet(), duck.unitsEarned.keys)
  }

  @Test
  fun `Duck Survival shows the three kinds of mission on day four`() = scenario {
    m Given "the demo game"
    val duck = demoDuckSurvival()

    m When "its streak and missions are read"
    val tiles = streakTiles(duck.streak)

    m Then "day 4 is today's, every day gives something, and all three sections have missions"
    assertEquals(4, tiles.single { it.state == StreakTileState.TODAY }.day)
    assertTrue(tiles.all { it.reward != null })
    assertTrue((tiles[6].reward as StreakReward.Item).glow)
    assertEquals(MissionKind.entries.toSet(), duck.missions.map { it.kind }.toSet())
    assertTrue(duck.missions.all { it.isDemo })
    assertTrue(duck.isDemo)
    assertEquals(DEMO_DUCK_PACKAGE, DEMO_GAME_ORDER.last())
    assertEquals(2, duck.readyCount(emptySet()))
    assertFalse(duck.missions.any { it.completed && it.current < it.target })
  }
}
