package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Hackathon 2026: a game's leaderboard ranks players by the Units they earned from that game's
// missions. These are the rules of where the player stands in it and what they are told.
internal class MyProgressLeaderboardTest {

  // Twenty players: 2000, 1900 ... 100 Units.
  private val others = (0 until 20).map { index ->
    LeaderboardPlayer(name = "P${index + 1}", units = 2000L - index * 100, color = index % 5)
  }

  private fun board(myUnits: Long) = leaderboardView(others, myUnits, myName = "You")

  @Test
  fun `The top three stand on the podium and the next seven are listed`() = scenario {
    m Given "twenty players and a player with too few Units for the top ten"
    m When "the leaderboard is laid out"
    val view = board(myUnits = 340)

    m Then "ranks 1 to 3 are the podium, 4 to 10 the list, in order"
    assertEquals(listOf(1, 2, 3), view.podium.map { it.rank })
    assertEquals(listOf("P1", "P2", "P3"), view.podium.map { it.player.name })
    assertEquals((4..10).toList(), view.rows.map { it.rank })
    assertEquals("P4", view.rows.first().player.name)
    assertTrue((view.podium + view.rows).none { it.player.isMe })
  }

  @Test
  fun `Below the top ten the player has their own row and the way into it`() = scenario {
    m Given "a player with 340 Units, between the players with 400 and 300"
    m When "the leaderboard is laid out"
    val view = board(myUnits = 340)

    m Then "they are 18th, outside the top, and told what the tenth has more than them"
    assertEquals(18, view.me.rank)
    assertTrue(view.me.player.isMe)
    assertFalse(view.isMeInTop)
    // The tenth has 1100: one more than that puts the player in.
    assertEquals(LeaderboardTip.ReachTop(units = 1100 - 340 + 1), view.tip)
  }

  @Test
  fun `Inside the top ten the player's row is in the list, with the next place up`() = scenario {
    m Given "a player with 1450 Units"
    m When "the leaderboard is laid out"
    val view = board(myUnits = 1450)

    m Then "they are 7th, one of the listed rows, and told how far the 6th is"
    assertEquals(7, view.me.rank)
    assertTrue(view.isMeInTop)
    assertEquals(view.me, view.rows.single { it.player.isMe })
    assertEquals(7, view.rows.size)
    assertEquals(LeaderboardTip.ReachRank(units = 1500 - 1450 + 1, rank = 6), view.tip)
  }

  @Test
  fun `A player on the podium is shown there`() = scenario {
    m Given "a player with 1950 Units"
    m When "the leaderboard is laid out"
    val view = board(myUnits = 1950)

    m Then "they are second on the podium and told how far first place is"
    assertEquals(2, view.me.rank)
    assertTrue(view.podium[1].player.isMe)
    assertEquals(LeaderboardTip.ReachRank(units = 51, rank = 1), view.tip)
  }

  @Test
  fun `The best player is told so`() = scenario {
    m Given "a player with more Units than everyone"
    m When "the leaderboard is laid out"
    val view = board(myUnits = 5000)

    m Then "they are first and there is nothing left to reach"
    assertEquals(1, view.me.rank)
    assertEquals(LeaderboardTip.First, view.tip)
  }

  @Test
  fun `A tie goes to the player`() = scenario {
    m Given "a player with exactly the Units of the fifth"
    m When "the leaderboard is laid out"
    val view = board(myUnits = 1600)

    m Then "they take fifth place, so earning one more than the next player always passes them"
    assertEquals(5, view.me.rank)
    assertEquals("P5", view.rows[2].player.name)
  }

  @Test
  fun `Claiming a game's missions moves the player up that game's leaderboard`() = scenario {
    m Given "a game with two completed missions of 100 and 150 Units and one in progress"
    fun card(id: String, units: Int, completed: Boolean) = MissionCard(
      id = id, kind = MissionKind.PLAY_TIME, title = id, description = null, units = units,
      current = if (completed) 1 else 0, target = 1, unit = PaEMissionProgressType.COUNT,
      completed = completed,
    )
    val game = GameProgress(
      packageName = "com.dice",
      name = "Dice Roll",
      icon = null,
      streak = GameWeekStreak(daysPlayed = 0, playedToday = false, rewards = emptyMap()),
      missions = listOf(card("a", 100, true), card("b", 150, true), card("c", 500, false)),
    )

    m When "nothing is claimed, then one, then both"
    m Then "only what was claimed is added to what the player had"
    assertEquals(340L, leaderboardUnits(game, emptySet(), base = 340))
    assertEquals(440L, leaderboardUnits(game, setOf("a"), base = 340))
    assertEquals(590L, leaderboardUnits(game, setOf("a", "b", "c"), base = 340))
    assertTrue(board(590).me.rank < board(340).me.rank)
  }
}
