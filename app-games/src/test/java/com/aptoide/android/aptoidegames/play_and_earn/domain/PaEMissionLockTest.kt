package com.aptoide.android.aptoidegames.play_and_earn.domain

import cm.aptoide.pt.campaigns.domain.PaEEvidenceSource
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionProgressType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.isLockedWithoutUsageAccess
import cm.aptoide.pt.campaigns.domain.needsUsageAccess
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
    val locked = mission.isLockedWithoutUsageAccess(hasUsageAccess = false)

    m Then "it is locked"
    assertTrue(locked)
  }

  @Test
  fun `A usage mission is not locked with the permission`() = scenario {
    m Given "a pending mission measured by usage tracking and usage access granted"
    val mission = mission(evidenceSource = PaEEvidenceSource.APTOIDE_USAGE)

    m When "the lock is checked"
    val locked = mission.isLockedWithoutUsageAccess(hasUsageAccess = true)

    m Then "it is not locked"
    assertFalse(locked)
  }

  @Test
  fun `An event mission is never locked`() = scenario {
    m Given "a pending MMP-confirmed mission and no usage access"
    val mission = mission(evidenceSource = PaEEvidenceSource.MMP)

    m When "the lock is checked"
    val locked = mission.isLockedWithoutUsageAccess(hasUsageAccess = false)

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
    val locked = mission.isLockedWithoutUsageAccess(hasUsageAccess = false)

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
    val locked = mission.isLockedWithoutUsageAccess(hasUsageAccess = false)

    m Then "it is locked"
    assertTrue(locked)
  }
}
