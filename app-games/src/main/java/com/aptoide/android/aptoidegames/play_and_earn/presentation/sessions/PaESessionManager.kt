package com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.toPaERefusal
import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.play_and_earn.sessions.data.PaESessionsRepository
import cm.aptoide.pt.play_and_earn.sessions.data.SessionExpiredException
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionInfo
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPreferencesRepository
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.CompletedMissionEvent
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaEMissionConfirmations
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaESession
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaESessionRefusals
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaESessionStartResult
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.PaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.missions.PaEMissionManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaESessionManager @Inject constructor(
  private val paESessionsRepository: PaESessionsRepository,
  private val paeMissionsRepository: PaEMissionsRepository,
  private val paeMissionManager: PaEMissionManager,
  private val paEPreferencesRepository: PaEPreferencesRepository,
  private val paEAnalytics: PaEAnalytics,
  private val paESessionRefusals: PaESessionRefusals,
) {

  val activeSessions = mutableListOf<PaESession>()

  // Buffered: emitting suspends until the collector is done, and the collector downloads a
  // notification icon. Without slack, several missions confirmed in one heartbeat would stall the
  // polling loop behind the network.
  private val _completedMissions = MutableSharedFlow<CompletedMissionEvent>(extraBufferCapacity = 8)
  val completedMissions = _completedMissions.asSharedFlow()

  /**
   * The refusal that still keeps [packageName] from starting a session (AND-950), from
   * [PaESessionRefusals]: its own region refusal or one for every game.
   */
  fun refusalOf(packageName: String): PaERefusal? =
    paESessionRefusals.blockingRefusalOf(packageName)

  /**
   * After a tracking poll: true when an account, device or update refusal holds for every game,
   * so tracking can only wait for nothing while the "recording" notification stays up.
   */
  fun shouldStopService(): Boolean = paESessionRefusals.everyGameRefusal() != null

  /** After a status heartbeat for [packageName]: true when it can no longer get a session. */
  fun shouldStopStatusMode(packageName: String): Boolean = refusalOf(packageName) != null

  /** Creates a new session for the given package. */
  suspend fun createSession(packageName: String): PaESessionStartResult {
    if (hasActiveSession(packageName)) {
      return PaESessionStartResult.AlreadyActive
    }
    refusalOf(packageName)?.let { return PaESessionStartResult.Refused(it) }

    val session = paESessionsRepository.startSession(packageName).getOrElse { error ->
      // Only a play-blocking refusal ends anything; a limit is a failure like any other.
      val refusal = error.toPaERefusal()?.takeIf { it.code.blocksPlay }
        ?: return PaESessionStartResult.Failed
      onRefused(packageName, refusal)
      if (shouldStopService()) activeSessions.clear()
      return PaESessionStartResult.Refused(refusal)
    }
    // Accepted now: whatever refused it before no longer applies.
    paESessionRefusals.onSessionStarted(packageName)

    if (session.sessionId.isBlank()) {
      return PaESessionStartResult.Failed
    }

    val sessionMissions = paeMissionsRepository.getCampaignMissions(packageName)
      .getOrElse { return PaESessionStartResult.Failed }

    val heartbeatIntervalSeconds = paEPreferencesRepository.getHeartbeatIntervalSeconds()

    activeSessions.add(
      PaESession(
        sessionId = session.sessionId,
        packageName = packageName,
        ttlSeconds = session.ttl,
        missions = sessionMissions,
        heartbeatIntervalSeconds = heartbeatIntervalSeconds
      )
    )

    paEAnalytics.sendPaESessionStart(packageName)

    return PaESessionStartResult.Started
  }

  private fun onRefused(packageName: String, refusal: PaERefusal) =
    paESessionRefusals.record(packageName, refusal)

  /** The play-blocking refusal [error] is, if it is one (AND-950). */
  private fun blockingRefusal(error: Throwable): PaERefusal? =
    error.toPaERefusal()?.takeIf { it.code.blocksPlay }

  private fun hasActiveSession(packageName: String): Boolean {
    return activeSessions.any { it.packageName == packageName }
  }

  /** Forgets every session and every refusal: the service stopped, nothing holds any more. */
  fun clearAllSessions() {
    activeSessions.clear()
    paESessionRefusals.clear()
  }

  /**
   * Forgets every session but keeps the refusals: the service stops because of one, and the
   * Rewards tab must still say why (AND-950).
   */
  fun dropSessionsKeepingRefusals() {
    activeSessions.clear()
  }

  /**
   * Status heartbeat (AND-879): a zero-second heartbeat for [packageName], sent after Play while
   * usage tracking is off. Counts no play time; its only purpose is to receive the missions the
   * developer's MMP confirmed, which [completedMissions] then announces.
   * @return false when the session is gone and the caller should stop. A refused heartbeat
   * (AND-950) drops it for good: [refusalOf] says why.
   */
  suspend fun heartbeatStatus(packageName: String): Boolean {
    val session = activeSessions.firstOrNull { it.packageName == packageName } ?: return false
    return paESessionsRepository.heartbeatSession(
      session.sessionId,
      session.packageName,
      session.syncSequence,
      seconds = 0,
    ).fold(
      onSuccess = { syncResult ->
        updateSessionAfterSync(session, syncResult)
        processCompletedMissions(session, syncResult)
        true
      },
      onFailure = { exception ->
        updateSessionAfterSync(session)
        val refusal = blockingRefusal(exception)
        if (refusal != null) {
          onRefused(session.packageName, refusal)
          activeSessions.remove(session)
          if (shouldStopService()) activeSessions.clear()
          return false
        }
        if (exception is SessionExpiredException) activeSessions.remove(session)
        exception !is SessionExpiredException
      }
    )
  }

  /** Refreshes the missions (and the attribution status) the session was created with. */
  suspend fun refreshMissions(packageName: String) {
    val session = activeSessions.firstOrNull { it.packageName == packageName } ?: return
    paeMissionsRepository.getCampaignMissions(packageName, forceRefresh = true)
      .onSuccess { session.missions = it }
  }

  /** Tells the server the session is over and forgets it locally. */
  suspend fun endSession(packageName: String) {
    val session = activeSessions.firstOrNull { it.packageName == packageName } ?: return
    activeSessions.remove(session)
    paESessionsRepository.endSession(session.sessionId, session.packageName)
  }

  /**
   * @param playedMillis play time measured since the previous poll for [currentForegroundPackage]
   * (see PaEPlayTimeMeter); 0 when it was not really played.
   */
  suspend fun syncSessions(currentForegroundPackage: String?, playedMillis: Long) {
    val currentTimeSeconds = System.currentTimeMillis() / 1000L
    val sessionsToRemove = mutableListOf<PaESession>()

    activeSessions.forEach { session ->
      val shouldRemove = if (session.packageName == currentForegroundPackage) {
        // Sync the active session and check if it expired
        syncActiveSession(session, playedMillis)
      } else {
        // Check if inactive session has expired
        session.isExpired(currentTimeSeconds)
      }

      if (shouldRemove) {
        sessionsToRemove.add(session)
      }
    }

    activeSessions.removeAll(sessionsToRemove.toSet())
    // AND-950: a refusal for every game keeps no session, the one in front or any other.
    if (shouldStopService()) activeSessions.clear()
  }

  /**
   * Syncs an active session by performing a heartbeat and processing results
   * @return true if session expired and should be removed, false otherwise
   */
  private suspend fun syncActiveSession(
    session: PaESession,
    playedMillis: Long
  ): Boolean {
    session.addPlayTime(playedMillis)

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

    val claimedSeconds = session.secondsToClaim
    return paESessionsRepository.heartbeatSession(
      session.sessionId,
      session.packageName,
      session.syncSequence,
      claimedSeconds
    ).fold(
      onSuccess = { syncResult ->
        updateSessionAfterSync(session, syncResult, claimedSeconds)
        processCompletedMissions(session, syncResult)
        false  // Session is healthy, don't remove
      },
      onFailure = { exception ->
        // Update session state even on failure to prevent immediate retries
        // and ensure sequence numbers remain unique. The unsent play time stays for the next one.
        updateSessionAfterSync(session)
        // AND-950: a play-blocking refusal (e.g. account_restricted) drops the session, not
        // retried. Any other refusal (a 429) is a failure like before: kept and retried.
        val refusal = blockingRefusal(exception)
        if (refusal != null) onRefused(session.packageName, refusal)
        // Session is expired (or refused) and should be removed. Return true
        refusal != null || exception is SessionExpiredException
      }
    )
  }

  private fun updateSessionAfterSync(
    session: PaESession,
    syncResult: SessionInfo? = null,
    claimedSeconds: Int = 0,
  ) {
    session.lastSyncTime = System.currentTimeMillis()
    session.syncSequence++
    if (syncResult != null) {
      // Only what was claimed: time measured meanwhile and sub-second remainders carry over.
      session.onPlayTimeSent(claimedSeconds)
      session.totalSessionTime += syncResult.appliedSeconds
    }
  }

  private suspend fun processCompletedMissions(
    session: PaESession,
    syncResult: SessionInfo
  ) {
    PaEMissionConfirmations.from(
      events = syncResult.events,
      packageName = session.packageName,
      missions = session.missions,
      alreadyConfirmed = session.completedMissions,
    ).forEach { completedMission ->
      session.completedMissions.add(completedMission.title)
      session.pendingServerConfirmationMissions.remove(completedMission.title)

      // Mark mission as completed in local DB
      paeMissionsRepository.markMissionAsCompleted(
        packageName = session.packageName,
        missionTitle = completedMission.title
      )

      paEAnalytics.sendPaEMissionCompleted(session.packageName)

      _completedMissions.emit(
        CompletedMissionEvent(
          mission = completedMission,
          packageName = session.packageName
        )
      )
    }
  }
}
