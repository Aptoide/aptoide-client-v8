package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import cm.aptoide.pt.campaigns.domain.PaEGameStreak
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionPeriod
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEUserMission
import cm.aptoide.pt.test.gherkin.scenario
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Hackathon 2026: the rules of the Missions screen (My progress), as the designer's prototype
// behaves, on top of a server that pays a mission the moment it completes.
internal class MyProgressModelTest {

  private fun gameMission(
    title: String,
    type: PaEMissionType = PaEMissionType.PLAY_TIME,
    category: String? = null,
    eventName: String? = null,
    current: Int? = 0,
    target: Int = 600,
    status: PaEMissionStatus? = PaEMissionStatus.IN_PROGRESS,
  ) = PaEMission(
    title = title,
    description = "desc",
    icon = null,
    type = type,
    arguments = JsonObject().apply {
      category?.let { addProperty("category", it) }
      eventName?.let { addProperty("event_name", it) }
    },
    units = 50,
    progress = PaEMissionProgress(
      current = current,
      target = target,
      type = if (type == PaEMissionType.PLAY_TIME) {
        PaEMissionProgressType.SECONDS
      } else {
        PaEMissionProgressType.COUNT
      },
      status = status,
    ),
  )

  private fun userMission(
    title: String,
    type: PaEMissionType = PaEMissionType.STREAK,
    target: Int = 7,
    units: Int = 100,
    periodStart: String? = "2026-11-16",
    completed: Boolean = false,
  ) = PaEUserMission(
    title = title,
    description = null,
    icon = null,
    type = type,
    units = units,
    period = PaEMissionPeriod.WEEKLY,
    periodStart = periodStart,
    packageName = "com.dice",
    progress = PaEMissionProgress(
      current = if (completed) target else 1,
      target = target,
      type = PaEMissionProgressType.COUNT,
      status = if (completed) PaEMissionStatus.COMPLETED else PaEMissionStatus.IN_PROGRESS,
    ),
    isNext = false,
  )

  private fun card(id: String, completed: Boolean, units: Int = 50) = MissionCard(
    id = id,
    kind = MissionKind.PLAY_TIME,
    title = id,
    description = null,
    units = units,
    current = if (completed) 10 else 3,
    target = 10,
    unit = PaEMissionProgressType.COUNT,
    completed = completed,
  )

  private fun game(name: String, vararg cards: MissionCard) = GameProgress(
    packageName = name,
    name = name,
    icon = null,
    streak = GameWeekStreak(daysPlayed = 0, playedToday = false, rewards = emptyMap()),
    missions = cards.toList(),
  )

  @Test
  fun `A game's missions are play time, purchases or in-game`() = scenario {
    m Given "a play-time mission, purchase events, other events, a checkpoint and a streak"
    m When "each is classified"
    m Then "an event is a purchase when it says so, an in-game mission otherwise"
    assertEquals(MissionKind.PLAY_TIME, gameMission("Play 10 min").kind())
    assertEquals(
      MissionKind.PURCHASE,
      gameMission("Make 1 purchase", PaEMissionType.EVENT, category = "purchase").kind(),
    )
    assertEquals(
      MissionKind.PURCHASE,
      gameMission("Make 3 purchases", PaEMissionType.EVENT, category = "PURCHASE").kind(),
    )
    assertEquals(
      MissionKind.PURCHASE,
      gameMission("Buy attempts", PaEMissionType.EVENT, eventName = "af_purchase").kind(),
    )
    assertEquals(
      MissionKind.IN_GAME,
      gameMission("Reach level 10", PaEMissionType.EVENT, eventName = "af_level_achieved").kind(),
    )
    assertEquals(
      MissionKind.IN_GAME,
      gameMission("Reach level 10", PaEMissionType.EVENT, category = "level").kind(),
    )
    assertNull(gameMission("Checkpoint", PaEMissionType.CHECKPOINT).kind())
    assertNull(gameMission("Streak", PaEMissionType.STREAK).kind())
  }

  @Test
  fun `A mission with no section or no goal has no card`() = scenario {
    m Given "a checkpoint and a play-time mission without a goal"
    m When "the cards are built"
    m Then "neither shows, and a normal one does with its progress"
    assertNull(gameMission("Checkpoint", PaEMissionType.CHECKPOINT).toCard("com.dice"))
    assertNull(gameMission("Play", target = 0).toCard("com.dice"))
    val shown = gameMission("Play 10 min", current = 420).toCard("com.dice")
    assertEquals(420, shown?.current)
    assertEquals(600, shown?.target)
    assertEquals(0.7f, shown?.fraction)
    assertFalse(shown?.completed == true)
  }

  @Test
  fun `A completed mission waits for Claim, then stays claimed`() = scenario {
    m Given "a mission in progress and a completed one"
    val open = card("open", completed = false)
    val done = card("done", completed = true)

    m When "nothing is claimed, and then the completed one is"
    m Then "it goes from ready to claimed; the open one cannot be claimed"
    assertEquals(CardState.IN_PROGRESS, open.state(emptySet()))
    assertEquals(CardState.READY, done.state(emptySet()))
    assertEquals(CardState.CLAIMED, done.state(setOf("done")))
    assertEquals(CardState.IN_PROGRESS, open.state(setOf("open")))
  }

  @Test
  fun `Ready missions come first and claimed ones last`() = scenario {
    m Given "a claimed, an open and a ready mission, in that order"
    val cards = listOf(card("claimed", true), card("open", false), card("ready", true))

    m When "they are sorted for display"
    val sorted = cards.sortedForDisplay(claimed = setOf("claimed"))

    m Then "ready, then in progress, then claimed"
    assertEquals(listOf("ready", "open", "claimed"), sorted.map { it.id })
  }

  @Test
  fun `The same mission in another game is claimed separately`() = scenario {
    m Given "one mission title in two games"
    val mission = gameMission("Play 10 min")

    m When "their cards are built"
    m Then "each has its own identity"
    assertNotEquals(mission.toCard("com.dice")?.id, mission.toCard("com.duck")?.id)
  }

  @Test
  fun `A game's badge counts what waits to be claimed`() = scenario {
    m Given "a game with two completed missions and one open"
    val dice = game("dice", card("a", true), card("b", true), card("c", false))

    m When "one of them is claimed"
    m Then "the badge goes from 2 to 1"
    assertEquals(2, dice.readyCount(emptySet()))
    assertEquals(1, dice.readyCount(setOf("a")))
  }

  @Test
  fun `Games are listed in the presentation's order, then by name`() = scenario {
    m Given "five games and an order for three of them"
    val games = listOf("zeta", "duck", "alpha", "dice", "legends").map { game(it) }

    m When "they are ordered"
    val ordered = games.orderedForDisplay(first = listOf("legends", "dice", "duck"))

    m Then "the three come first as asked, the others after them by name"
    assertEquals(listOf("legends", "dice", "duck", "alpha", "zeta"), ordered.map { it.name })
    assertEquals(
      listOf("alpha", "dice", "duck", "legends", "zeta"),
      games.orderedForDisplay(first = emptyList()).map { it.name },
    )
  }

  @Test
  fun `A demo game stands in for the real one of the same package`() = scenario {
    m Given "a real game and a demo game sharing a package"
    val real = game("duck", card("real", completed = false))
    val demo = game("duck").copy(isDemo = true)

    m When "they are ordered"
    val ordered = listOf(real, demo).orderedForDisplay(first = emptyList())

    m Then "only the demo one is listed"
    assertEquals(listOf(true), ordered.map { it.isDemo })
  }

  @Test
  fun `The game with most to claim opens first`() = scenario {
    m Given "three games, the second with the most to claim"
    val games = listOf(
      game("cell", card("c1", false)),
      game("dice", card("d1", true), card("d2", true)),
      game("duck", card("k1", true)),
    )

    m When "the default game is picked"
    m Then "it is the second; with nothing to claim anywhere, the first; with no games, none"
    assertEquals("dice", defaultGame(games, emptySet()))
    assertEquals("cell", defaultGame(games, setOf("d1", "d2", "k1")))
    assertNull(defaultGame(emptyList(), emptySet()))
  }

  @Test
  fun `The balance holds back what is still to be claimed`() = scenario {
    m Given "a wallet of 1000 that already includes two completed missions of 50 and 150"
    val cards = listOf(card("a", true, units = 50), card("b", true, units = 150), card("c", false))

    m When "they are claimed one by one"
    m Then "the balance shown grows by each reward up to the wallet's"
    assertEquals(800L, shownBalance(1000, cards, emptySet()))
    assertEquals(850L, shownBalance(1000, cards, setOf("a")))
    assertEquals(1000L, shownBalance(1000, cards, setOf("a", "b")))
  }

  @Test
  fun `The balance shown never goes below zero and may be unknown`() = scenario {
    m Given "a wallet smaller than what waits, and a wallet that could not be read"
    val cards = listOf(card("a", true, units = 500), card("a", true, units = 500))

    m When "the balance is computed"
    m Then "it stops at zero (a mission listed twice counts once), or stays unknown"
    assertEquals(0L, shownBalance(100, cards, emptySet()))
    assertEquals(500L, shownBalance(1000, cards, emptySet()))
    assertNull(shownBalance(null, cards, emptySet()))
  }

  @Test
  fun `A streak mission per day puts a reward on every tile`() = scenario {
    m Given "seven streak missions for a game, one for each day"
    val daily = listOf(25, 40, 50, 60, 75, 100, 150).mapIndexed { index, units ->
      userMission("Play ${index + 1} days", target = index + 1, units = units)
    }

    m When "the streak and its tiles are built"
    val tiles = streakTiles(gameWeekStreak(week = null, streakMissions = daily, todayIndex = 0))

    m Then "each day shows what it pays"
    assertEquals(
      listOf(25, 40, 50, 60, 75, 100, 150),
      tiles.map { (it.reward as StreakReward.Units).units },
    )
  }

  @Test
  fun `A demo mission adds to the balance only once claimed`() = scenario {
    m Given "a wallet of 500 and a completed demo mission of 100, which the server never paid"
    val demo = card("${DEMO_ID_PREFIX}duck", completed = true, units = 100)

    m When "it waits, and then is claimed"
    m Then "the balance starts level with the wallet and grows by the reward"
    assertEquals(500L, shownBalance(500, listOf(demo), emptySet()))
    assertEquals(600L, shownBalance(500, listOf(demo), setOf(demo.id)))
  }

  @Test
  fun `What was completed before the screen existed starts as claimed`() = scenario {
    m Given "an old completed mission, an open one and a completed demo mission"
    val cards = listOf(
      card("old", completed = true, units = 828),
      card("open", completed = false),
      card("${DEMO_ID_PREFIX}duck", completed = true),
    )

    m When "the first-run baseline is computed"
    val baseline = claimsToBaseline(cards)

    m Then "only the old one is in it, and the balance is then level with the wallet"
    assertEquals(setOf("old"), baseline)
    assertEquals(526L, shownBalance(526, cards, baseline))
  }

  @Test
  fun `A game's streak is its days this week and what each target pays`() = scenario {
    m Given "a game played Monday and Wednesday, with rewards on days 3 and 7"
    val week = PaEGameStreak(
      packageName = "com.dice",
      days = listOf(true, false, true, false, false, false, false),
      daysPlayed = 2,
    )
    val missions = listOf(
      userMission("Play 3 days", target = 3, units = 30),
      userMission("Play 7 days", target = 7, units = 100),
      userMission("Play 1 hour", type = PaEMissionType.PLAY_TIME, target = 3600, units = 5),
    )

    m When "the streak is built on Wednesday and on Thursday"
    val wednesday = gameWeekStreak(week, missions, todayIndex = 2)
    val thursday = gameWeekStreak(week, missions, todayIndex = 3)

    m Then "two days are played, today counts only on Wednesday, and only streaks pay days"
    assertEquals(2, wednesday.daysPlayed)
    assertTrue(wednesday.playedToday)
    assertFalse(thursday.playedToday)
    assertEquals(
      mapOf(3 to StreakReward.Units(30), 7 to StreakReward.Units(100)),
      wednesday.rewards,
    )
  }

  @Test
  fun `A game not played this week starts at day one`() = scenario {
    m Given "no record of the game this week"
    m When "the streak is built"
    val streak = gameWeekStreak(week = null, streakMissions = emptyList(), todayIndex = 4)

    m Then "nothing is played and day 1 is today's"
    assertEquals(0, streak.daysPlayed)
    assertFalse(streak.playedToday)
    assertEquals(
      listOf(StreakTileState.TODAY) + List(6) { StreakTileState.UPCOMING },
      streakTiles(streak).map { it.state },
    )
  }

  @Test
  fun `The tiles fill from day one and today's is the next`() = scenario {
    m Given "three days played, with a reward on day 7"
    val rewards = mapOf<Int, StreakReward>(7 to StreakReward.Units(100))

    m When "today is not played yet, and when it is"
    val waiting = streakTiles(GameWeekStreak(3, playedToday = false, rewards = rewards))
    val played = streakTiles(GameWeekStreak(3, playedToday = true, rewards = rewards))

    m Then "day 4 is today's only while today is not played; day 7 shows its reward"
    assertEquals(
      listOf("PLAYED", "PLAYED", "PLAYED", "TODAY", "UPCOMING", "UPCOMING", "UPCOMING"),
      waiting.map { it.state.name },
    )
    assertEquals(
      listOf("PLAYED", "PLAYED", "PLAYED", "UPCOMING", "UPCOMING", "UPCOMING", "UPCOMING"),
      played.map { it.state.name },
    )
    assertEquals(StreakReward.Units(100), waiting.last().reward)
    assertNull(waiting.first().reward)
  }

  @Test
  fun `A full week is a complete streak`() = scenario {
    m Given "seven days played"
    val streak = GameWeekStreak(7, playedToday = true, rewards = emptyMap())

    m When "the tiles are built"
    m Then "every tile is played and the streak is complete"
    assertTrue(streak.isComplete)
    assertTrue(streakTiles(streak).all { it.state == StreakTileState.PLAYED })
  }

  @Test
  fun `Time reads in minutes under an hour and in hours from there`() = scenario {
    m Given "30 minutes, 1 hour, 90 minutes, 2 hours and a few seconds"
    m When "each is formatted"
    m Then "minutes under an hour; whole or one-decimal hours after"
    assertEquals(Duration.Minutes(30), durationOf(1800))
    assertEquals(Duration.Hours("1"), durationOf(3600))
    assertEquals(Duration.Hours("1.5"), durationOf(5400))
    assertEquals(Duration.Hours("2"), durationOf(7200))
    assertEquals(Duration.Minutes(1), durationOf(20))
    assertEquals(Duration.Minutes(0), durationOf(0))
  }
}
