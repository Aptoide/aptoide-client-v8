package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.campaigns.domain.PaEMissionPeriod
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEUserMission
import cm.aptoide.pt.campaigns.domain.PaEUserProgress
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionEvent
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Hackathon 2026: a completed user mission (the player's own, not the game's) is announced over
// the game being played, once, with the user mission to do next.
internal class PaEUserMissionAnnouncementsTest {

  private val minute = 60_000L

  private fun completed(
    title: String,
    scope: String? = "user",
    type: String = "mission_completed",
    units: Int = 10,
  ) = SessionEvent(
    type = type,
    missionTitle = title,
    packageName = "com.game",
    earnedUnits = units,
    scope = scope,
  )

  private fun userMission(
    title: String,
    current: Int,
    target: Int,
    units: Int = 20,
  ) = PaEUserMission(
    title = title,
    description = null,
    icon = null,
    type = PaEMissionType.PLAY_TIME,
    units = units,
    period = PaEMissionPeriod.WEEKLY,
    progress = PaEMissionProgress(
      current = current,
      target = target,
      type = PaEMissionProgressType.SECONDS,
      status = if (current >= target) {
        PaEMissionStatus.COMPLETED
      } else {
        PaEMissionStatus.IN_PROGRESS
      },
    ),
    isNext = false,
  )

  private fun progress(vararg missions: PaEUserMission) = PaEUserProgress(
    lifetimePlaySeconds = 0,
    lifetimeUnits = 0,
    missions = missions.toList(),
    streaks = emptyList(),
  )

  @Test
  fun `Only the player's own missions are picked from a heartbeat`() = scenario {
    m Given "a heartbeat with a user mission, a game mission and another event type"
    val events = listOf(
      completed("Play 5 minutes this week"),
      completed("Buy", scope = null),
      completed("Level 2", type = "level_up"),
    )

    m When "the user missions are read"
    val newOnes = PaEUserMissionAnnouncements().newOnes(events, nowMs = 0)

    m Then "only the user mission is announced"
    assertEquals(listOf("Play 5 minutes this week"), newOnes.map { it.missionTitle })
  }

  @Test
  fun `A game's confirmations leave the user missions out`() = scenario {
    m Given "a user mission event for the game being played"
    val events = listOf(completed("Play 5 minutes this week"))

    m When "the game's confirmations are read"
    val confirmed = PaEMissionConfirmations.from(events, "com.game", null, emptySet())

    m Then "it is not one of the game's"
    assertTrue(confirmed.isEmpty())
  }

  @Test
  fun `A replayed event is announced once, whatever the session`() = scenario {
    m Given "the server replaying the same completion on later heartbeats"
    val announcements = PaEUserMissionAnnouncements()
    val events = listOf(completed("Play 5 minutes this week"), completed("Play 5 minutes this week"))

    m When "three heartbeats arrive within the replay"
    val first = announcements.newOnes(events, nowMs = 0)
    val second = announcements.newOnes(events, nowMs = 1 * minute)
    val third = announcements.newOnes(events, nowMs = 4 * minute)

    m Then "only the first one announces it"
    assertEquals(1, first.size)
    assertTrue(second.isEmpty())
    assertTrue(third.isEmpty())
  }

  @Test
  fun `The same mission is announced again in a later period`() = scenario {
    m Given "a weekly mission announced once"
    val announcements = PaEUserMissionAnnouncements()
    val events = listOf(completed("Play 5 minutes this week"))
    announcements.newOnes(events, nowMs = 0)

    m When "it is completed again long after the replay is over"
    val nextWeek = announcements.newOnes(events, nowMs = 7 * 24 * 60 * minute)

    m Then "it is announced again"
    assertEquals(1, nextWeek.size)
  }

  @Test
  fun `The announcement shows the mission and the next one to do`() = scenario {
    m Given "the player's progress after completing the 5 minute mission"
    val event = completed("Play 5 minutes this week", units = 10)
    val progress = progress(
      userMission("Play 10 minutes this week", current = 300, target = 600, units = 20),
      userMission("Play 1 hour this month", current = 300, target = 3600, units = 50),
      userMission("Play 5 minutes this week", current = 300, target = 300, units = 10),
    )

    m When "the announcement is built"
    val announcement = completedUserMission(event, progress, listOf(event.missionTitle))

    m Then "it is a user mission over the game, with the first open mission as next"
    assertTrue(announcement.isUserMission)
    assertEquals("com.game", announcement.packageName)
    assertEquals("Play 5 minutes this week", announcement.mission.title)
    assertEquals(10, announcement.mission.units)
    assertEquals("Play 10 minutes this week", announcement.nextMission?.title)
    assertEquals(20, announcement.nextMission?.units)
    assertEquals(600, announcement.nextMission?.progress?.target)
  }

  @Test
  fun `A mission just completed is never its own next one`() = scenario {
    m Given "a progress that still shows the two completed missions as open"
    val first = completed("Play 5 minutes this week")
    val second = completed("Play 10 minutes this week")
    val progress = progress(
      userMission("Play 5 minutes this week", current = 290, target = 300),
      userMission("Play 10 minutes this week", current = 590, target = 600),
      userMission("Play 1 hour this month", current = 590, target = 3600),
    )
    val titles = listOf(first.missionTitle, second.missionTitle)

    m When "both announcements are built"
    val announcements = listOf(first, second).map { completedUserMission(it, progress, titles) }

    m Then "both point to the monthly mission"
    assertEquals(
      listOf("Play 1 hour this month", "Play 1 hour this month"),
      announcements.map { it.nextMission?.title },
    )
  }

  @Test
  fun `Without the progress the event alone is announced`() = scenario {
    m Given "a completion whose progress could not be loaded"
    val event = completed("Play 5 minutes this week", units = 10)

    m When "the announcement is built"
    val announcement = completedUserMission(event, progress = null, listOf(event.missionTitle))

    m Then "the mission and its reward show, with no next mission"
    assertEquals("Play 5 minutes this week", announcement.mission.title)
    assertEquals(10, announcement.mission.units)
    assertNull(announcement.nextMission)
    assertTrue(announcement.isUserMission)
  }

  @Test
  fun `With every user mission done there is no next one`() = scenario {
    m Given "the last open mission completing"
    val event = completed("Play 5 minutes this week")
    val progress = progress(userMission("Play 5 minutes this week", current = 300, target = 300))

    m When "the announcement is built"
    val announcement = completedUserMission(event, progress, listOf(event.missionTitle))

    m Then "nothing is promised next"
    assertNull(announcement.nextMission)
    assertFalse(announcement.mission.title.isBlank())
  }
}
