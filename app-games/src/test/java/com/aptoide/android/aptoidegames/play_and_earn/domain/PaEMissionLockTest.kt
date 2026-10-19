package com.aptoide.android.aptoidegames.play_and_earn.domain

import cm.aptoide.pt.campaigns.domain.PaEEvidenceSource
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.isLockedWithout
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.campaigns.domain.needsUsageAccess
import cm.aptoide.pt.campaigns.domain.rewardRows
import cm.aptoide.pt.test.gherkin.scenario
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// AND-903: which missions need the usage-access permission, and which rows show locked without it.
internal class PaEMissionLockTest {

  private fun mission(
    type: PaEMissionType = PaEMissionType.EVENT,
    progressType: PaEMissionProgressType? = PaEMissionProgressType.COUNT,
    status: PaEMissionStatus? = PaEMissionStatus.PENDING,
    evidenceSource: PaEEvidenceSource? = null,
  ) = PaEMission(
    title = "m-$type-$progressType-$evidenceSource",
    description = null,
    icon = null,
    type = type,
    arguments = JsonObject(),
    units = 1,
    progress = progressType?.let { PaEMissionProgress(0, 10, it, status) },
    evidenceSource = evidenceSource,
  )

  @Test
  fun `The backend's evidence source decides when it is known`() = scenario {
    m Given "missions whose evidence source the backend sent, with types that disagree"
    val usage =
      mission(type = PaEMissionType.EVENT, evidenceSource = PaEEvidenceSource.APTOIDE_USAGE)
    val mmp = mission(
      type = PaEMissionType.PLAY_TIME,
      progressType = PaEMissionProgressType.SECONDS,
      evidenceSource = PaEEvidenceSource.MMP,
    )
    val sdk = mission(type = PaEMissionType.PLAY_TIME, evidenceSource = PaEEvidenceSource.SDK)

    m When "each one is asked whether it needs usage access"
    val answers = listOf(usage, mmp, sdk).map { it.needsUsageAccess() }

    m Then "only the one measured by this app's usage tracking does"
    assertEquals(listOf(true, false, false), answers)
  }

  @Test
  fun `Without an evidence source a time-based mission needs usage access`() = scenario {
    m Given "cached missions (no evidence source) of every shape"
    val playTime = mission(type = PaEMissionType.PLAY_TIME)
    val seconds =
      mission(type = PaEMissionType.CHECKPOINT, progressType = PaEMissionProgressType.SECONDS)
    val event = mission(type = PaEMissionType.EVENT)
    val count = mission(type = PaEMissionType.CHECKPOINT)
    val noProgress = mission(type = PaEMissionType.EVENT, progressType = null)

    m When "each one is asked whether it needs usage access"
    val answers = listOf(playTime, seconds, event, count, noProgress).map { it.needsUsageAccess() }

    m Then "the play-time and seconds ones do, the others don't"
    assertEquals(listOf(true, true, false, false, false), answers)
  }

  @Test
  fun `A usage mission is locked without the permission`() = scenario {
    m Given "a pending mission measured by usage tracking and no usage access"
    val mission = mission(evidenceSource = PaEEvidenceSource.APTOIDE_USAGE)

    m When "the lock is checked"
    val locked = mission.isLockedWithout(trackingPermissions = false)

    m Then "it is locked"
    assertTrue(locked)
  }

  @Test
  fun `A usage mission is not locked with the permission`() = scenario {
    m Given "a pending mission measured by usage tracking and usage access granted"
    val mission = mission(evidenceSource = PaEEvidenceSource.APTOIDE_USAGE)

    m When "the lock is checked"
    val locked = mission.isLockedWithout(trackingPermissions = true)

    m Then "it is not locked"
    assertFalse(locked)
  }

  @Test
  fun `An event mission is never locked`() = scenario {
    m Given "a pending MMP-confirmed mission and no usage access"
    val mission = mission(evidenceSource = PaEEvidenceSource.MMP)

    m When "the lock is checked"
    val locked = mission.isLockedWithout(trackingPermissions = false)

    m Then "it is not locked"
    assertFalse(locked)
  }

  @Test
  fun `A completed usage mission is never locked`() = scenario {
    m Given "a completed usage mission and no usage access"
    val mission = mission(
      evidenceSource = PaEEvidenceSource.APTOIDE_USAGE,
      status = PaEMissionStatus.COMPLETED,
    )

    m When "the lock is checked"
    val locked = mission.isLockedWithout(trackingPermissions = false)

    m Then "it is not locked: there is nothing left to measure"
    assertFalse(locked)
  }

  @Test
  fun `An ongoing usage mission locks like a pending one`() = scenario {
    m Given "a usage mission in progress and the permission revoked meanwhile"
    val mission = mission(
      evidenceSource = PaEEvidenceSource.APTOIDE_USAGE,
      status = PaEMissionStatus.IN_PROGRESS,
    )

    m When "the lock is checked"
    val locked = mission.isLockedWithout(trackingPermissions = false)

    m Then "it is locked"
    assertTrue(locked)
  }
}

// AND-903: the whole lock truth table, and what the Rewards tab lists.
internal class PaERewardRowsTest {

  private fun mission(
    title: String,
    type: PaEMissionType = PaEMissionType.EVENT,
    progressType: PaEMissionProgressType? = PaEMissionProgressType.COUNT,
    status: PaEMissionStatus? = PaEMissionStatus.PENDING,
    evidenceSource: PaEEvidenceSource? = null,
  ) = PaEMission(
    title = title,
    description = null,
    icon = null,
    type = type,
    arguments = JsonObject(),
    units = 1,
    progress = progressType?.let { PaEMissionProgress(0, 10, it, status) },
    evidenceSource = evidenceSource,
  )

  @Test
  fun `Locked exactly when usage-measured, permissions missing and not completed`() = scenario {
    m Given "every evidence source, status (including no progress) and permission state"
    val sources = listOf(null) + PaEEvidenceSource.entries
    val statuses = listOf<PaEMissionStatus?>(null) + PaEMissionStatus.entries
    val cases = buildList {
      for (source in sources) for (status in statuses) for (granted in listOf(true, false)) {
        // null evidence source: a play-time mission, i.e. time-based by type
        val type = if (source == null) PaEMissionType.PLAY_TIME else PaEMissionType.EVENT
        val m = mission("m", type = type, status = status, evidenceSource = source)
        add(Triple(m, granted, status))
      }
    }

    m When "each one is checked"
    val wrong = cases.filter { (mission, granted, status) ->
      val usageMeasured = mission.evidenceSource == null ||
        mission.evidenceSource == PaEEvidenceSource.APTOIDE_USAGE
      val expected = usageMeasured && !granted && status != PaEMissionStatus.COMPLETED
      mission.isLockedWithout(trackingPermissions = granted) != expected
    }

    m Then "all 32 combinations match the rule"
    assertEquals(32, cases.size)
    assertEquals(emptyList<Any>(), wrong.map { (m, g, s) -> "${m.evidenceSource}/$s/granted=$g" })
  }

  @Test
  fun `With the usage path on and no permissions only the usage-measured rows lock`() = scenario {
    m Given "a mixed campaign: usage missions pending, ongoing, completed; an MMP one; checkpoints"
    val missions = PaEMissions(
      checkpoints = listOf(
        mission("cp-gxp", type = PaEMissionType.CHECKPOINT),
        mission(
          "cp-seconds",
          type = PaEMissionType.CHECKPOINT,
          evidenceSource = PaEEvidenceSource.APTOIDE_USAGE,
        ),
      ),
      missions = listOf(
        mission("time-pending", evidenceSource = PaEEvidenceSource.APTOIDE_USAGE),
        mission(
          "time-ongoing",
          status = PaEMissionStatus.IN_PROGRESS,
          evidenceSource = PaEEvidenceSource.APTOIDE_USAGE,
        ),
        mission(
          "time-done",
          status = PaEMissionStatus.COMPLETED,
          evidenceSource = PaEEvidenceSource.APTOIDE_USAGE,
        ),
        mission("purchase", evidenceSource = PaEEvidenceSource.MMP),
      ),
    )

    m When "the rows are built without the permissions"
    val rows = missions.rewardRows(usageTrackingEnabled = true, trackingPermissions = false)

    m Then "every mission is listed and only the unfinished usage-measured ones are locked"
    assertEquals(
      listOf("cp-gxp" to false, "cp-seconds" to true),
      rows.checkpoints.map { it.mission.title to it.locked },
    )
    assertEquals(
      listOf(
        "time-pending" to true,
        "time-ongoing" to true,
        "time-done" to false,
        "purchase" to false,
      ),
      rows.missions.map { it.mission.title to it.locked },
    )
  }

  @Test
  fun `With the permissions nothing locks`() = scenario {
    m Given "usage-measured and MMP missions, the usage path on"
    val missions = PaEMissions(
      checkpoints = listOf(
        mission(
          "cp",
          type = PaEMissionType.CHECKPOINT,
          evidenceSource = PaEEvidenceSource.APTOIDE_USAGE,
        ),
      ),
      missions = listOf(
        mission("time", evidenceSource = PaEEvidenceSource.APTOIDE_USAGE),
        mission("purchase", evidenceSource = PaEEvidenceSource.MMP),
      ),
    )

    m When "the rows are built with the permissions granted"
    val rows = missions.rewardRows(usageTrackingEnabled = true, trackingPermissions = true)

    m Then "every row is listed and none is locked"
    assertEquals(listOf("cp"), rows.checkpoints.map { it.mission.title })
    assertEquals(listOf("time", "purchase"), rows.missions.map { it.mission.title })
    assertTrue(rows.checkpoints.none { it.locked } && rows.missions.none { it.locked })
  }

  @Test
  fun `With the usage path off the usage-measured rows are hidden and nothing locks`() = scenario {
    m Given "usage-measured and MMP missions, the kill switch off, no permissions"
    val missions = PaEMissions(
      checkpoints = listOf(
        mission("cp-gxp", type = PaEMissionType.CHECKPOINT),
        mission(
          "cp-seconds",
          type = PaEMissionType.CHECKPOINT,
          evidenceSource = PaEEvidenceSource.APTOIDE_USAGE,
        ),
      ),
      missions = listOf(
        mission("time", evidenceSource = PaEEvidenceSource.APTOIDE_USAGE),
        mission("cached-time", type = PaEMissionType.PLAY_TIME),
        mission("purchase", evidenceSource = PaEEvidenceSource.MMP),
      ),
    )

    m When "the rows are built"
    val rows = missions.rewardRows(usageTrackingEnabled = false, trackingPermissions = false)

    m Then "only the rows nothing on this device needs to measure remain, unlocked (Phase 1)"
    assertEquals(listOf("cp-gxp"), rows.checkpoints.map { it.mission.title })
    assertEquals(listOf("purchase"), rows.missions.map { it.mission.title })
    assertTrue(rows.checkpoints.none { it.locked } && rows.missions.none { it.locked })
  }
}
