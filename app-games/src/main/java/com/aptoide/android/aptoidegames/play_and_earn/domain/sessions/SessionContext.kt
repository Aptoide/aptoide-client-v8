package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

/**
 * Snapshot of a session state for mission evaluation
 */
data class SessionContext(
  val totalSessionTime: Int,
  val usageTimeSinceLastSync: Int,
  val completedMissionTitles: Set<String>,
  val pendingConfirmationMissionTitles: Set<String>,
  val sessionStartTime: Long
)
