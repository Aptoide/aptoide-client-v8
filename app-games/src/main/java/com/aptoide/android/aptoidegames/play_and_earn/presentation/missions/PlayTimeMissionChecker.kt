package com.aptoide.android.aptoidegames.play_and_earn.presentation.missions

import cm.aptoide.pt.campaigns.domain.PaEMission
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions.SessionContext
import javax.inject.Inject

/**
 * Checks completion for PLAY_TIME missions
 * A play time mission is completed when:
 * current progress + total session time >= target
 * Time since last sync not considered for calculation
 */
class PlayTimeMissionChecker @Inject constructor() : MissionCompletionChecker {

  override fun isLocallyCompleted(mission: PaEMission, sessionContext: SessionContext): Boolean {
    if (mission.title in sessionContext.completedMissionTitles) {
      return false
    }

    // Skip if already detected as locally completed and waiting for server confirmation
    // A mission can only force a heartbeat once, even if server hasn't confirmed it yet
    if (mission.title in sessionContext.pendingConfirmationMissionTitles) {
      return false
    }

    val localProgressTime = mission.progress?.current ?: 0
    val targetProgressTime = mission.progress?.target ?: return false

    val projectedTime = localProgressTime + sessionContext.totalSessionTime

    return projectedTime >= targetProgressTime
  }
}
