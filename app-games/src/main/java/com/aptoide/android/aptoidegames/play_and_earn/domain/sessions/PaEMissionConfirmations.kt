package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionEvent

/** Which of a heartbeat's events are new mission confirmations for a session's game. */
object PaEMissionConfirmations {

  const val MISSION_COMPLETED = "mission_completed"

  /**
   * The server replays recent events on every heartbeat and sends other event types too, so a
   * confirmation counts once: a `mission_completed` for this package, for a mission the session
   * knows, not already completed in the data (a replay after a new session or a second Play),
   * and not already seen confirmed in this session. Checkpoints are not matched, as before.
   */
  fun from(
    events: List<SessionEvent>,
    packageName: String,
    missions: PaEMissions?,
    alreadyConfirmed: Collection<String>,
  ): List<PaEMission> {
    val known = missions?.missions.orEmpty()
      .filterNot { it.progress?.status == PaEMissionStatus.COMPLETED }
      .associateBy { it.title }
    return events
      .asSequence()
      .filter { it.type == MISSION_COMPLETED && it.packageName == packageName }
      .map { it.missionTitle }
      .distinct()
      .filterNot { it in alreadyConfirmed }
      .mapNotNull { known[it] }
      .toList()
  }
}
