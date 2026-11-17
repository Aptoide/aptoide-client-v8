package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.model.PaEGameStreakJson
import cm.aptoide.pt.campaigns.data.model.PaEGameUnitsJson
import cm.aptoide.pt.campaigns.data.model.PaELeaderboardEntryJson
import cm.aptoide.pt.campaigns.data.model.PaELeaderboardJson
import cm.aptoide.pt.campaigns.data.model.PaEUserMissionJson
import cm.aptoide.pt.campaigns.data.model.PaEUserMissionProgressJson
import cm.aptoide.pt.campaigns.data.model.PaEUserProgressJson
import cm.aptoide.pt.campaigns.domain.PaEMissionPeriod
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Hackathon 2026 (T6): the "My progress" screen reads the player's totals, user missions, weekly
// streaks and the weekly leaderboard. A newer backend must never be able to empty the screen.
internal class DefaultPaEProgressRepositoryTest {

  private class FakeApi(
    var progress: PaEUserProgressJson? = null,
    var leaderboard: PaELeaderboardJson? = null,
  ) : PaEProgressApi {
    var askedLimit: Int? = null

    override suspend fun getMyProgress(): PaEUserProgressJson =
      progress ?: throw IllegalStateException("offline")

    override suspend fun getWeeklyLeaderboard(limit: Int): PaELeaderboardJson {
      askedLimit = limit
      return leaderboard ?: throw IllegalStateException("offline")
    }
  }

  private fun repository(api: PaEProgressApi) =
    DefaultPaEProgressRepository(api, Dispatchers.Unconfined)

  private fun missionJson(
    title: String? = "Play 10 minutes this week",
    type: String? = "PLAY_TIME",
    period: String? = "WEEKLY",
    current: Int? = 420,
    target: Int? = 600,
    progressType: String? = "seconds",
    status: String? = "in_progress",
    isNext: Boolean? = false,
    packageName: String? = null,
  ) = PaEUserMissionJson(
    title = title,
    description = null,
    icon = "",
    type = type,
    units = 20,
    period = period,
    periodStart = "2026-11-16",
    packageName = packageName,
    progress = PaEUserMissionProgressJson(current, target, progressType, status),
    isNext = isNext,
  )

  private fun progressJson(
    missions: List<PaEUserMissionJson>? = emptyList(),
    streaks: List<PaEGameStreakJson>? = emptyList(),
  ) = PaEUserProgressJson(
    lifetimePlaySeconds = 5400,
    lifetimeUnits = 320,
    missions = missions,
    streaks = streaks,
  )

  @Test
  fun `the player's totals and missions reach the screen`() = coScenario {
    m Given "a player with totals, a weekly mission in progress and a monthly one not started"
    val api = FakeApi(
      progress = progressJson(
        missions = listOf(
          missionJson(isNext = true),
          missionJson(
            title = "Play every day this week",
            type = "STREAK",
            current = 2,
            target = 7,
            progressType = "count",
          ),
          missionJson(
            title = "Play 1 hour this month",
            period = "MONTHLY",
            current = null,
            target = 3600,
            status = "pending",
          ),
        )
      )
    )

    m When "the progress is loaded"
    val progress = repository(api).getMyProgress().getOrThrow()

    m Then "the totals and each mission are as the backend said"
    assertEquals(5400L, progress.lifetimePlaySeconds)
    assertEquals(320L, progress.lifetimeUnits)
    assertEquals(3, progress.missions.size)
    val weekly = progress.missions[0]
    assertEquals(PaEMissionType.PLAY_TIME, weekly.type)
    assertEquals(PaEMissionPeriod.WEEKLY, weekly.period)
    assertEquals(20, weekly.units)
    assertEquals(420, weekly.progress.current)
    assertEquals(600, weekly.progress.target)
    assertEquals(PaEMissionProgressType.SECONDS, weekly.progress.type)
    assertEquals(PaEMissionStatus.IN_PROGRESS, weekly.progress.status)
    assertTrue(weekly.isNext)
    assertNull(weekly.icon)
    val streak = progress.missions[1]
    assertEquals(PaEMissionType.STREAK, streak.type)
    assertEquals(PaEMissionProgressType.COUNT, streak.progress.type)
    val monthly = progress.missions[2]
    assertEquals(PaEMissionPeriod.MONTHLY, monthly.period)
    assertEquals(0, monthly.progress.current)
    assertEquals(PaEMissionStatus.PENDING, monthly.progress.status)
    assertFalse(monthly.isCompleted)
  }

  @Test
  fun `a game's streak mission says which game it belongs to`() = coScenario {
    m Given "a weekly streak of one game next to a mission across every game"
    val api = FakeApi(
      progress = progressJson(
        missions = listOf(
          missionJson(
            title = "Play Dice Roll 7 days this week",
            type = "STREAK",
            current = 2,
            target = 7,
            progressType = "count",
            packageName = "com.dice.roll",
          ),
          missionJson(packageName = " "),
        )
      )
    )

    m When "the progress is loaded"
    val missions = repository(api).getMyProgress().getOrThrow().missions

    m Then "the game's mission carries its package and its period's first day"
    assertEquals("com.dice.roll", missions[0].packageName)
    assertEquals("2026-11-16", missions[0].periodStart)
    assertNull(missions[1].packageName)
  }

  @Test
  fun `a completed mission is never the next one`() = coScenario {
    m Given "a completed mission the backend still flags as next"
    val api = FakeApi(
      progress = progressJson(
        missions = listOf(missionJson(current = 600, status = "completed", isNext = true))
      )
    )

    m When "the progress is loaded"
    val mission = repository(api).getMyProgress().getOrThrow().missions.single()

    m Then "it shows as completed and not as next"
    assertTrue(mission.isCompleted)
    assertFalse(mission.isNext)
  }

  @Test
  fun `a mission this app cannot present is left out`() = coScenario {
    m Given "missions with an unknown type, no title and no goal next to a good one"
    val api = FakeApi(
      progress = progressJson(
        missions = listOf(
          missionJson(type = "QUIZ"),
          missionJson(title = " "),
          missionJson(target = 0),
          missionJson(title = "Kept", period = "YEARLY", status = "paused", progressType = null),
        )
      )
    )

    m When "the progress is loaded"
    val missions = repository(api).getMyProgress().getOrThrow().missions

    m Then "only the good one shows, with what is unknown about it left neutral"
    assertEquals(listOf("Kept"), missions.map { it.title })
    assertNull(missions.single().period)
    assertEquals(PaEMissionStatus.PENDING, missions.single().progress.status)
    assertEquals(PaEMissionProgressType.SECONDS, missions.single().progress.type)
  }

  @Test
  fun `a game's week is always seven days from Monday`() = coScenario {
    m Given "a game played on Monday and Wednesday, one with a short week, one with no package"
    val api = FakeApi(
      progress = progressJson(
        streaks = listOf(
          PaEGameStreakJson("com.a", listOf(true, false, true, false, false, false, false), 2),
          PaEGameStreakJson("com.b", listOf(true), 1),
          PaEGameStreakJson(null, listOf(true), 1),
        )
      )
    )

    m When "the progress is loaded"
    val streaks = repository(api).getMyProgress().getOrThrow().streaks

    m Then "each known game has its seven squares"
    assertEquals(listOf("com.a", "com.b"), streaks.map { it.packageName })
    assertEquals(listOf(true, false, true, false, false, false, false), streaks[0].days)
    assertEquals(2, streaks[0].daysPlayed)
    assertEquals(7, streaks[1].days.size)
    assertEquals(1, streaks[1].daysPlayed)
  }

  @Test
  fun `the units each game has paid are read per period`() = coScenario {
    m Given "a game that paid 40 this week, 190 this month and 1018 ever, and one with no package"
    val api = FakeApi(
      progress = progressJson().copy(
        games = listOf(
          PaEGameUnitsJson("com.dice", unitsWeek = 40, unitsMonth = 190, unitsAllTime = 1018),
          PaEGameUnitsJson(" ", unitsWeek = 5, unitsMonth = 5, unitsAllTime = 5),
          PaEGameUnitsJson("com.legends", unitsWeek = null, unitsMonth = null, unitsAllTime = 75),
        )
      )
    )

    m When "the progress is loaded"
    val units = repository(api).getMyProgress().getOrThrow().gameUnits

    m Then "each known game has its three amounts, a missing one being zero"
    assertEquals(listOf("com.dice", "com.legends"), units.map { it.packageName })
    assertEquals(listOf(40L, 190L, 1018L), units[0].let { listOf(it.week, it.month, it.allTime) })
    assertEquals(listOf(0L, 0L, 75L), units[1].let { listOf(it.week, it.month, it.allTime) })
  }

  @Test
  fun `an empty or absent answer is an empty progress`() = coScenario {
    m Given "a backend that sends no lists and no totals"
    val api = FakeApi(progress = PaEUserProgressJson(null, null, null, null))

    m When "the progress is loaded"
    val progress = repository(api).getMyProgress().getOrThrow()

    m Then "there is nothing to show, and no crash"
    assertEquals(0L, progress.lifetimePlaySeconds)
    assertEquals(0L, progress.lifetimeUnits)
    assertTrue(progress.missions.isEmpty())
    assertTrue(progress.streaks.isEmpty())
    assertTrue(progress.gameUnits.isEmpty())
  }

  @Test
  fun `the leaderboard lists the top and where the player stands`() = coScenario {
    m Given "a top of two and the player in 57th place"
    val me = PaELeaderboardEntryJson(57, "0xfc20…015e", 60, true)
    val api = FakeApi(
      leaderboard = PaELeaderboardJson(
        entries = listOf(
          PaELeaderboardEntryJson(1, "0x1a1a…1a1a", 9000, false),
          PaELeaderboardEntryJson(2, "0x2b2b…2b2b", 8000, null),
          PaELeaderboardEntryJson(null, "broken", 1, false),
        ),
        me = me,
      )
    )

    m When "the leaderboard is loaded for the top 2"
    val leaderboard = repository(api).getWeeklyLeaderboard(limit = 2).getOrThrow()

    m Then "the top is listed and the player's own row comes with it"
    assertEquals(2, api.askedLimit)
    assertEquals(listOf(1, 2), leaderboard.entries.map { it.rank })
    assertEquals("0x1a1a…1a1a", leaderboard.entries[0].player)
    assertEquals(9000L, leaderboard.entries[0].playSeconds)
    assertFalse(leaderboard.entries[1].isMe)
    assertEquals(57, leaderboard.me?.rank)
    assertTrue(leaderboard.me?.isMe == true)
  }

  @Test
  fun `a failed call is a failure, not a crash`() = coScenario {
    m Given "no connection"
    val repository = repository(FakeApi())

    m When "the progress and the leaderboard are loaded"
    val progress = repository.getMyProgress()
    val leaderboard = repository.getWeeklyLeaderboard()

    m Then "both report the failure"
    assertTrue(progress.isFailure)
    assertTrue(leaderboard.isFailure)
  }
}
