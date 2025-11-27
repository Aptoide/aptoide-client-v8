package com.aptoide.android.aptoidegames.play_and_earn.presentation.missions

import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions.SessionContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages mission state and completion detection
 * Coordinates between different mission type checkers using the Strategy pattern
 */
@Singleton
class PaEMissionManager @Inject constructor(
  private val playTimeMissionChecker: PlayTimeMissionChecker,
  private val paEMissionsRepository: PaEMissionsRepository,
) {

  private val completionCheckers: Map<PaEMissionType, MissionCompletionChecker> = mapOf(
    PaEMissionType.PLAY_TIME to playTimeMissionChecker,
  )

  suspend fun getLocallyCompletedMissions(
    packageName: String,
    missions: List<PaEMission>,
    sessionContext: SessionContext
  ): List<PaEMission> {
    val localMissions = paEMissionsRepository.getCachedMissions(packageName)?.missions
      ?: return emptyList()

    val locallyCompletedMissions = localMissions
      .filter { it.progress?.status == PaEMissionStatus.COMPLETED }
      .map { it.title }
      .toSet()

    return missions
      .filterNot { it.title in locallyCompletedMissions }
      .filter { mission ->
        completionCheckers[mission.type]?.isLocallyCompleted(mission, sessionContext) ?: false
      }
  }
}
