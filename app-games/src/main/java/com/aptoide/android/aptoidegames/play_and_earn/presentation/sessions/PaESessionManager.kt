package com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions

import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.play_and_earn.sessions.data.PaESessionsRepository
import cm.aptoide.pt.play_and_earn.sessions.data.SessionExpiredException
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionInfo
import com.aptoide.android.aptoidegames.play_and_earn.presentation.missions.PaEMissionManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaESessionManager @Inject constructor(
  private val paESessionsRepository: PaESessionsRepository,
  private val paeMissionsRepository: PaEMissionsRepository,
  private val paeMissionManager: PaEMissionManager
) {

  val activeSessions = mutableListOf<PaESession>()

  private val _completedMissions = MutableSharedFlow<PaEMission>()
  val completedMissions = _completedMissions.asSharedFlow()

  /**
   * Creates a new session for the given package
   * @return true if session was successfully created, false otherwise
   */
  suspend fun createSession(packageName: String): Boolean {
    if (hasActiveSession(packageName)) {
      return false
    }

    val session = paESessionsRepository.startSession(packageName).getOrElse { return false }

    if (session.sessionId.isBlank()) {
      return false
    }

    val sessionMissions = paeMissionsRepository.getCampaignMissions(packageName)
      .getOrElse { return false }

    activeSessions.add(
      PaESession(
        sessionId = session.sessionId,
        packageName = packageName,
        ttlSeconds = session.ttl,
        missions = sessionMissions
      )
    )

    return true
  }

  private fun hasActiveSession(packageName: String): Boolean {
    return activeSessions.any { it.packageName == packageName }
  }

  suspend fun syncSessions(currentForegroundPackage: String?, syncIntervalSeconds: Int = 6) {
    val currentTimeSeconds = System.currentTimeMillis() / 1000L
    val sessionsToRemove = mutableListOf<PaESession>()

    activeSessions.forEach { session ->
      val shouldRemove = if (session.packageName == currentForegroundPackage) {
        // Sync the active session and check if it expired
        syncActiveSession(session, syncIntervalSeconds)
      } else {
        // Check if inactive session has expired
        session.isExpired(currentTimeSeconds)
      }

      if (shouldRemove) {
        sessionsToRemove.add(session)
      }
    }

    activeSessions.removeAll(sessionsToRemove.toSet())
  }

  /**
   * Syncs an active session by performing a heartbeat and processing results
   * @return true if session expired and should be removed, false otherwise
   */
  private suspend fun syncActiveSession(
    session: PaESession,
    syncIntervalSeconds: Int
  ): Boolean {
    session.usageTimeSinceLastSync += syncIntervalSeconds

    val sessionMissions = session.missions?.missions.orEmpty()
    val sessionContext = session.toSessionContext()

    // Check if there are any newly completed missions that haven't been synced yet
    val locallyCompletedMissions = paeMissionManager.getLocallyCompletedMissions(
      packageName = session.packageName,
      missions = sessionMissions,
      sessionContext = sessionContext
    )
    
    val newlyCompletedMissions = locallyCompletedMissions
      .filterNot { it.title in session.pendingServerConfirmationMissions }

    val shouldForceSyncForMission = newlyCompletedMissions.isNotEmpty()
    val shouldPerformSync = shouldForceSyncForMission || session.shouldSync()

    if (!shouldPerformSync) return false

    newlyCompletedMissions.forEach { mission ->
      session.pendingServerConfirmationMissions.add(mission.title)
    }

    return paESessionsRepository.heartbeatSession(
      session.sessionId,
      session.packageName,
      session.syncSequence,
      session.usageTimeSinceLastSync
    ).fold(
      onSuccess = { syncResult ->
        updateSessionAfterSync(session, syncResult)
        processCompletedMissions(session, syncResult)
        false  // Session is healthy, don't remove
      },
      onFailure = { exception ->
        exception is SessionExpiredException  // Session is expired and should be removed. Return true
      }
    )
  }

  private fun updateSessionAfterSync(
    session: PaESession,
    syncResult: SessionInfo
  ) {
    session.lastSyncTime = System.currentTimeMillis()
    session.syncSequence++
    session.usageTimeSinceLastSync = 0
    session.totalSessionTime += syncResult.appliedSeconds
  }

  private suspend fun processCompletedMissions(
    session: PaESession,
    syncResult: SessionInfo
  ) {
    syncResult.events
      .filter { it.packageName == session.packageName }
      .forEach { missionEvent ->
        if (missionEvent.missionTitle !in session.completedMissions) {
          session.missions?.missions
            ?.find { it.title == missionEvent.missionTitle }
            ?.let { completedMission ->
              session.completedMissions.add(missionEvent.missionTitle)

              session.pendingServerConfirmationMissions.remove(missionEvent.missionTitle)

              // Mark mission as completed in local DB
              paeMissionsRepository.markMissionAsCompleted(
                packageName = session.packageName,
                missionTitle = missionEvent.missionTitle
              )

              _completedMissions.tryEmit(completedMission)
            }
        }
      }
  }
}
