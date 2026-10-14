package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionEvent
import cm.aptoide.pt.test.gherkin.scenario
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PaEMissionConfirmationsTest {

  private fun mission(title: String, status: PaEMissionStatus? = null) = PaEMission(
    title = title, description = null, icon = null, type = PaEMissionType.EVENT,
    arguments = JsonObject(), units = 10,
    progress = status?.let { PaEMissionProgress(1, 1, PaEMissionProgressType.COUNT, it) },
  )

  private val missions = PaEMissions(
    checkpoints = listOf(mission("Checkpoint 1")),
    missions = listOf(mission("Buy"), mission("Win")),
  )

  private fun completed(title: String, pkg: String = "com.game") =
    SessionEvent(type = "mission_completed", missionTitle = title, packageName = pkg)

  @Test
  fun `Confirms the known missions the server completed for this game`() = scenario {
    m Given "a heartbeat with a completion for a mission and for a checkpoint"
    val events = listOf(completed("Buy"), completed("Checkpoint 1"))

    m When "the confirmations are read"
    val confirmed = PaEMissionConfirmations.from(events, "com.game", missions, emptySet())

    m Then "the mission is confirmed; checkpoints aren't matched, as before"
    assertEquals(listOf("Buy"), confirmed.map { it.title })
  }

  @Test
  fun `A mission already completed in the data is not confirmed again`() = scenario {
    m Given "a new session after Play, with the server replaying an old completion"
    val done = PaEMissions(
      checkpoints = emptyList(),
      missions = listOf(mission("Buy", PaEMissionStatus.COMPLETED), mission("Win")),
    )

    m When "the confirmations are read"
    val confirmed =
      PaEMissionConfirmations.from(listOf(completed("Buy")), "com.game", done, emptySet())

    m Then "no second notification"
    assertEquals(emptyList<PaEMission>(), confirmed)
  }

  @Test
  fun `Ignores other event types, other games and unknown missions`() = scenario {
    m Given "a mixed bag of events"
    val events = listOf(
      SessionEvent(type = "level_up", missionTitle = "Buy", packageName = "com.game"),
      completed("Buy", pkg = "com.other"),
      completed("Not a mission"),
    )

    m When "the confirmations are read"
    val confirmed = PaEMissionConfirmations.from(events, "com.game", missions, emptySet())

    m Then "none count"
    assertEquals(emptyList<PaEMission>(), confirmed)
  }

  @Test
  fun `A replayed confirmation counts once`() = scenario {
    m Given "the server replays the same completion, and one was already seen"
    val events = listOf(completed("Buy"), completed("Buy"), completed("Win"))

    m When "the confirmations are read with Win already confirmed"
    val confirmed = PaEMissionConfirmations.from(events, "com.game", missions, setOf("Win"))

    m Then "only Buy, once"
    assertEquals(listOf("Buy"), confirmed.map { it.title })
  }

  @Test
  fun `Nothing is confirmed without missions`() = scenario {
    m Given "the session has no missions loaded"
    m When "the confirmations are read"
    val confirmed =
      PaEMissionConfirmations.from(listOf(completed("Buy")), "com.game", null, emptySet())

    m Then "none"
    assertEquals(emptyList<PaEMission>(), confirmed)
  }
}
