package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.test.gherkin.scenario
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// Hackathon 2026 (T5): a completed mission also announces the game's next one.
internal class PaENextMissionTest {

  private fun mission(
    title: String,
    status: PaEMissionStatus? = null,
    type: PaEMissionType = PaEMissionType.PLAY_TIME,
  ) = PaEMission(
    title = title, description = null, icon = null, type = type,
    arguments = JsonObject(), units = 10,
    progress = status?.let { PaEMissionProgress(1, 10, PaEMissionProgressType.SECONDS, it) },
  )

  private fun missions(vararg missions: PaEMission) = PaEMissions(
    checkpoints = listOf(mission("Checkpoint 1", PaEMissionStatus.IN_PROGRESS)),
    missions = missions.toList(),
  )

  @Test
  fun `The next mission is the one in progress, ahead of the pending ones`() = scenario {
    m Given "a game with a pending mission listed before the one in progress"
    val missions = missions(
      mission("Play 5 minutes", PaEMissionStatus.IN_PROGRESS),
      mission("Play 30 minutes", PaEMissionStatus.PENDING),
      mission("Play 10 minutes", PaEMissionStatus.IN_PROGRESS),
    )

    m When "the first mission completes"
    val next = missions.nextMission(completedTitles = listOf("Play 5 minutes"))

    m Then "the mission in progress comes next"
    assertEquals("Play 10 minutes", next?.title)
  }

  @Test
  fun `Without a mission in progress the first pending one is next`() = scenario {
    m Given "a game whose remaining missions have not started, one without progress data"
    val missions = missions(
      mission("Play 5 minutes", PaEMissionStatus.IN_PROGRESS),
      mission("Play 10 minutes"),
      mission("Play 30 minutes", PaEMissionStatus.PENDING),
    )

    m When "the first mission completes"
    val next = missions.nextMission(completedTitles = listOf("Play 5 minutes"))

    m Then "the server's order decides among them"
    assertEquals("Play 10 minutes", next?.title)
  }

  @Test
  fun `Missions just completed are skipped, their data still says in progress`() = scenario {
    m Given "two missions confirmed by the same heartbeat, a third still open"
    val missions = missions(
      mission("Play 5 minutes", PaEMissionStatus.IN_PROGRESS),
      mission("Play 10 minutes", PaEMissionStatus.IN_PROGRESS),
      mission("Play 30 minutes", PaEMissionStatus.PENDING),
    )

    m When "the next mission is picked"
    val next = missions.nextMission(completedTitles = listOf("Play 5 minutes", "Play 10 minutes"))

    m Then "neither of the two is offered as the next one"
    assertEquals("Play 30 minutes", next?.title)
  }

  @Test
  fun `With every mission completed there is no next mission`() = scenario {
    m Given "a game whose other mission was completed before, and no missions at all"
    val missions = missions(
      mission("Play 5 minutes", PaEMissionStatus.IN_PROGRESS),
      mission("Play 10 minutes", PaEMissionStatus.COMPLETED),
    )
    val none: PaEMissions? = null

    m When "the last open mission completes"
    val next = missions.nextMission(completedTitles = listOf("Play 5 minutes"))
    val nextOfNone = none.nextMission(completedTitles = emptyList())

    m Then "nothing is next: checkpoints are not missions"
    assertNull(next)
    assertNull(nextOfNone)
  }

  @Test
  fun `Without usage tracking a time mission is never next, it cannot move`() = scenario {
    m Given "a game with a time mission and an event mission left"
    val missions = missions(
      mission("Buy", PaEMissionStatus.IN_PROGRESS, PaEMissionType.EVENT),
      mission("Play 10 minutes", PaEMissionStatus.IN_PROGRESS),
      mission("Win", type = PaEMissionType.EVENT),
    )

    m When "an event mission completes while play time is not tracked"
    val next = missions.nextMission(completedTitles = listOf("Buy"), includeTimeBased = false)

    m Then "the next event mission is offered, as the Rewards tab hides the time ones"
    assertEquals("Win", next?.title)
  }
}
