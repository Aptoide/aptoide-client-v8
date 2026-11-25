package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.campaigns.domain.PaEMissions

class PaESession(
  val sessionId: String,
  val packageName: String,
  val ttlSeconds: Int,
  missions: PaEMissions?,
  val heartbeatIntervalSeconds: Int
) {
  // Refreshed by the status heartbeat so new missions and the attribution are seen.
  var missions: PaEMissions? = missions

  // Missions confirmed as completed by the server
  val completedMissions = mutableListOf<String>()

  val pendingServerConfirmationMissions = mutableSetOf<String>()

  val sessionStartTime: Long = System.currentTimeMillis()
  var lastSyncTime: Long = sessionStartTime

  var lastAppOpenTime: Long = System.currentTimeMillis()

  var totalSessionTime: Int = 0

  // Measured play time the server has not accepted yet. Kept in milliseconds so the sub-second
  // remainder of every interval carries over to the next heartbeat instead of being lost.
  var unsentPlayMillis: Long = 0L
    private set

  /** Whole seconds of measured play the server has not accepted yet. */
  val usageTimeSinceLastSync: Int get() = (unsentPlayMillis / 1000L).toInt()

  /** What the next heartbeat claims: the backend credits at most [MAX_CLAIM_SECONDS] per beat. */
  val secondsToClaim: Int get() = usageTimeSinceLastSync.coerceAtMost(MAX_CLAIM_SECONDS)

  var syncSequence = 0

  fun pause() {
    lastAppOpenTime = System.currentTimeMillis()
  }

  /**
   * Adds measured play time. A failed heartbeat keeps its time for the next ones, which claim it
   * [MAX_CLAIM_SECONDS] at a time; anything beyond [MAX_UNSENT_PLAY_MILLIS] (a long run of failed
   * heartbeats) is dropped, as the server would not credit it anyway: it credits at most the
   * real time since the wallet's last credit.
   */
  fun addPlayTime(millis: Long) {
    unsentPlayMillis =
      (unsentPlayMillis + millis.coerceAtLeast(0L)).coerceAtMost(MAX_UNSENT_PLAY_MILLIS)
  }

  /** The server accepted a heartbeat that claimed [seconds]; the remainder stays for the next. */
  fun onPlayTimeSent(seconds: Int) {
    unsentPlayMillis = (unsentPlayMillis - seconds * 1000L).coerceAtLeast(0L)
  }

  fun shouldSync() =
    (System.currentTimeMillis() / 1000L) - (lastSyncTime / 1000L) > heartbeatIntervalSeconds

  fun isExpired(currentTimeSeconds: Long): Boolean {
    val lastSyncTimeSeconds = lastSyncTime / 1000L
    val timeSinceLastSync = currentTimeSeconds - lastSyncTimeSeconds
    return timeSinceLastSync > ttlSeconds
  }

  fun toSessionContext(): SessionContext {
    return SessionContext(
      totalSessionTime = totalSessionTime,
      usageTimeSinceLastSync = usageTimeSinceLastSync,
      completedMissionTitles = completedMissions.toSet(),
      pendingConfirmationMissionTitles = pendingServerConfirmationMissions,
      sessionStartTime = sessionStartTime
    )
  }

  companion object {
    /** The backend credits at most 60 s per heartbeat. */
    const val MAX_CLAIM_SECONDS = 60

    /** Two heartbeats' worth, so a heartbeat interval near 60 s loses nothing. */
    const val MAX_UNSENT_PLAY_MILLIS = 120_000L
  }
}
