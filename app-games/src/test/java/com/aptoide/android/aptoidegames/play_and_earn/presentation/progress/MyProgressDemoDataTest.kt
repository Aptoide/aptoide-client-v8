package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Hackathon 2026: the demo data has to hold together on stage, whatever the presenter's real
// play time is that week.
internal class MyProgressDemoDataTest {

  private val hour = 3600L

  @Test
  fun `The demo leaderboard is ten players, best first`() = scenario {
    m Given "a player who has not played this week"
    m When "the leaderboard is built"
    val board = demoLeaderboard(mySeconds = 0, seed = 202647)

    m Then "ten ranked players with believable, strictly ordered times, and no row for the player"
    assertEquals((1..10).toList(), board.entries.map { it.rank })
    assertEquals(board.entries.map { it.playSeconds }.sortedDescending(),
      board.entries.map { it.playSeconds })
    assertEquals(10, board.entries.map { it.player }.distinct().size)
    assertTrue(board.entries.first().playSeconds in 17 * hour..21 * hour)
    assertTrue(board.entries.last().playSeconds > hour / 2)
    assertTrue(board.entries.none { it.isMe })
    assertNull(board.me)
  }

  @Test
  fun `The leaderboard is the same all week and changes the next`() = scenario {
    m Given "two builds in one week and one in the next"
    m When "they are compared"
    m Then "the week's is stable, the next week's differs"
    assertEquals(demoLeaderboard(0, seed = 202647), demoLeaderboard(0, seed = 202647))
    assertNotEquals(demoLeaderboard(0, seed = 202647), demoLeaderboard(0, seed = 202648))
  }

  @Test
  fun `A player with enough play time is placed inside the top`() = scenario {
    m Given "a player with more time than the fifth of the demo players"
    val others = demoLeaderboard(0, seed = 202647).entries
    val mine = others[4].playSeconds + 60

    m When "the leaderboard is built"
    val board = demoLeaderboard(mySeconds = mine, seed = 202647)

    m Then "they are fifth, the top is still ten, and their own row is that same one"
    assertEquals(10, board.entries.size)
    assertEquals((1..10).toList(), board.entries.map { it.rank })
    assertEquals(5, board.entries.single { it.isMe }.rank)
    assertEquals(board.entries.single { it.isMe }, board.me)
    assertEquals(mine, board.me?.playSeconds)
  }

  @Test
  fun `A player with little play time gets a row below the top`() = scenario {
    m Given "a player with ten minutes this week"
    m When "the leaderboard is built"
    val board = demoLeaderboard(mySeconds = 600, seed = 202647)

    m Then "the top is the ten demo players and the player's row comes after it"
    assertTrue(board.entries.none { it.isMe })
    assertTrue((board.me?.rank ?: 0) > 10)
    assertEquals(11, leaderboardRows(board).size)
    assertTrue(leaderboardRows(board).last().isMe)
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
    assertEquals(2, duck.readyCount(emptySet()))
    assertFalse(duck.missions.any { it.completed && it.current < it.target })
  }
}
