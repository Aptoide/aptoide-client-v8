package com.aptoide.android.aptoidegames.play_and_earn.domain

import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.campaigns.domain.needsUsageAccess
import cm.aptoide.pt.test.gherkin.scenario
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// AND-901: whether a game's campaign needs the usage-access permission. The backend's answer
// (AND-900 `requires_usage_access`) wins; without it, derived from the missions themselves.
internal class PaEMissionsNeedsUsageAccessTest {

  private fun mission(
    type: PaEMissionType,
    progressType: PaEMissionProgressType? = PaEMissionProgressType.COUNT,
  ) = PaEMission(
    title = "m-$type-$progressType",
    description = null,
    icon = null,
    type = type,
    arguments = JsonObject(),
    units = 1,
    progress = progressType?.let {
      PaEMissionProgress(current = 0, target = 10, type = it, status = PaEMissionStatus.PENDING)
    },
  )

  @Test
  fun `Backend says yes, the missions say no, the backend wins`() = scenario {
    m Given "event-only missions flagged by the backend as requiring usage access"
    val missions = PaEMissions(
      checkpoints = emptyList(),
      missions = listOf(mission(PaEMissionType.EVENT)),
      requiresUsageAccess = true,
    )

    m When "the need for usage access is checked"
    val needed = missions.needsUsageAccess()

    m Then "usage access is needed"
    assertTrue(needed)
  }

  @Test
  fun `Backend says no, a time mission is listed, the backend wins`() = scenario {
    m Given "a play-time mission flagged by the backend as not requiring usage access"
    val missions = PaEMissions(
      checkpoints = emptyList(),
      missions = listOf(mission(PaEMissionType.PLAY_TIME, PaEMissionProgressType.SECONDS)),
      requiresUsageAccess = false,
    )

    m When "the need for usage access is checked"
    val needed = missions.needsUsageAccess()

    m Then "usage access is not needed"
    assertFalse(needed)
  }

  @Test
  fun `Without the backend answer a play-time mission needs usage access`() = scenario {
    m Given "cached missions (no backend answer) with a play-time mission"
    val missions = PaEMissions(
      checkpoints = emptyList(),
      missions = listOf(mission(PaEMissionType.EVENT), mission(PaEMissionType.PLAY_TIME)),
    )

    m When "the need for usage access is checked"
    val needed = missions.needsUsageAccess()

    m Then "usage access is needed"
    assertTrue(needed)
  }

  @Test
  fun `Without the backend answer a seconds checkpoint needs usage access`() = scenario {
    m Given "cached missions with a checkpoint measured in seconds"
    val missions = PaEMissions(
      checkpoints = listOf(mission(PaEMissionType.CHECKPOINT, PaEMissionProgressType.SECONDS)),
      missions = emptyList(),
    )

    m When "the need for usage access is checked"
    val needed = missions.needsUsageAccess()

    m Then "usage access is needed"
    assertTrue(needed)
  }

  @Test
  fun `Without the backend answer event and count missions do not need usage access`() = scenario {
    m Given "cached missions with only event and count-based checkpoint missions"
    val missions = PaEMissions(
      checkpoints = listOf(mission(PaEMissionType.CHECKPOINT)),
      missions = listOf(mission(PaEMissionType.EVENT)),
    )

    m When "the need for usage access is checked"
    val needed = missions.needsUsageAccess()

    m Then "usage access is not needed"
    assertFalse(needed)
  }

  @Test
  fun `Without the backend answer an event mission measured in seconds needs usage access`() =
    scenario {
      m Given "cached missions with an event mission whose progress is in seconds"
      val missions = PaEMissions(
        checkpoints = emptyList(),
        missions = listOf(mission(PaEMissionType.EVENT, PaEMissionProgressType.SECONDS)),
      )

      m When "the need for usage access is checked"
      val needed = missions.needsUsageAccess()

      m Then "usage access is needed"
      assertTrue(needed)
    }

  @Test
  fun `Without the backend answer a mission without progress does not need usage access`() =
    scenario {
      m Given "cached missions with an event mission that has no progress yet"
      val missions = PaEMissions(
        checkpoints = emptyList(),
        missions = listOf(mission(PaEMissionType.EVENT, progressType = null)),
      )

      m When "the need for usage access is checked"
      val needed = missions.needsUsageAccess()

      m Then "usage access is not needed"
      assertFalse(needed)
    }

  @Test
  fun `Without the backend answer and without missions usage access is not needed`() = scenario {
    m Given "no missions at all and no backend answer"
    val missions = PaEMissions(checkpoints = emptyList(), missions = emptyList())

    m When "the need for usage access is checked"
    val needed = missions.needsUsageAccess()

    m Then "usage access is not needed"
    assertFalse(needed)
  }
}
