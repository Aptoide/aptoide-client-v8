package com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions

import cm.aptoide.pt.campaigns.domain.PaEMissions

class PaESession(
  val sessionId: String,
  val packageName: String,
  val ttlSeconds: Int,
  val missions: PaEMissions?
) {
  // Missions confirmed as completed by the server
  val completedMissions = mutableListOf<String>()

  val pendingServerConfirmationMissions = mutableSetOf<String>()

  val sessionStartTime: Long = System.currentTimeMillis()
  var lastSyncTime: Long = sessionStartTime

  var lastAppOpenTime: Long = System.currentTimeMillis()

  var totalSessionTime: Int = 0
  var usageTimeSinceLastSync: Int = 0

  var syncSequence = 0

  fun pause() {
    lastAppOpenTime = System.currentTimeMillis()
  }

  fun shouldSync() = (System.currentTimeMillis() / 1000L) - (lastSyncTime / 1000L) > 15L

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
}
