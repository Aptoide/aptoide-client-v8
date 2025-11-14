package com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions

import cm.aptoide.pt.campaigns.data.PaECampaignsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.play_and_earn.sessions.data.PaESessionsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaESessionManager @Inject constructor(
  private val paESessionsRepository: PaESessionsRepository,
  private val paeCampaignsRepository: PaECampaignsRepository
) {

  val activeSessions = mutableListOf<PaESession>()

  val _completedMissions = MutableSharedFlow<PaEMission>()
  val completedMissions = _completedMissions.asSharedFlow()

  suspend fun createSession(packageName: String): Boolean {
    //Only create session if there is not already one with the same package
    if (activeSessions.none { it.packageName == packageName }) {
      val session = paESessionsRepository.startSession(packageName).getOrNull()

      if (session != null && session.sessionId.isNotBlank()) {
        val sessionMissions =
          paeCampaignsRepository.getCampaignMissions(packageName).getOrElse { return false }

        activeSessions.add(
          PaESession(
            sessionId = session.sessionId,
            packageName = packageName,
            ttlSeconds = session.ttl,
            missions = sessionMissions
          )
        )

        return true
      } else {
        return false
      }
    }

    return false
  }

  suspend fun syncSessions(currentForegroundPackage: String?) {
    val sessionsToRemove = mutableListOf<PaESession>()

    activeSessions.forEach { session ->
      if (session.packageName == currentForegroundPackage) { //Sync current session
        //Update last sync time
        session.usageTimeSinceLastSync += 6

        if (session.shouldSync()) {
          //Sessions needs to sync. Perform session heartbeat.
          val syncResult = paESessionsRepository.heartbeatSession(
            session.sessionId,
            session.packageName,
            session.syncSequence,
            session.usageTimeSinceLastSync
          ).getOrNull()

          if (syncResult != null) {
            session.lastSyncTime = System.currentTimeMillis()
            session.syncSequence++
            session.usageTimeSinceLastSync = 0

            session.totalSessionTime += syncResult.appliedSeconds

            //Check for completed mission events
            syncResult.events
              .filter { it.packageName == session.packageName }
              .forEach { mission ->
                if (!session.completedMissions.contains(mission.missionTitle)) {
                  val completedMission =
                    session.missions?.missions?.find { it.title == mission.missionTitle }

                  session.completedMissions.add(mission.missionTitle)

                  if (completedMission != null) {
                    _completedMissions.emit(completedMission)
                  }
                }
              }
          }
        }
      } else {
        // Not the current session. Check if TTL has expired
        val currentTimeSeconds = System.currentTimeMillis() / 1000L
        val lastSyncTimeSeconds = session.lastSyncTime / 1000L
        val timeSinceLastSync = currentTimeSeconds - lastSyncTimeSeconds

        if (timeSinceLastSync > session.ttlSeconds) {
          // Session expired. Mark for removal
          sessionsToRemove.add(session)
        }
      }
    }

    sessionsToRemove.forEach {
      activeSessions.remove(it)
    }
  }
}
