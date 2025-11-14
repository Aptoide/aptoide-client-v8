package com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions

import cm.aptoide.pt.campaigns.domain.PaEMissions

class PaESession(
  val sessionId: String,
  val packageName: String,
  val ttlSeconds: Int,
  val missions: PaEMissions?
) {
  val completedMissions = mutableListOf<String>()

  val sessionStartTime: Long = System.currentTimeMillis()
  var lastSyncTime: Long = sessionStartTime

  var totalSessionTime: Int = 0
  var usageTimeSinceLastSync: Int = 0

  var syncSequence = 0

  fun shouldSync() = (System.currentTimeMillis() / 1000L) - (lastSyncTime / 1000L) > 15L
}
