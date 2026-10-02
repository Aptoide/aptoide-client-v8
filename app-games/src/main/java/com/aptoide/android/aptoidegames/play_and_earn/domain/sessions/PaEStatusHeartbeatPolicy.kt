package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.campaigns.domain.PaEAttributionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissions

/**
 * When the status heartbeat after Play stops (AND-879).
 *
 * Without the usage-access permission the app can't see that the game left the foreground, so
 * "while the game is running" is approximated: heartbeats go on until there is nothing left to
 * wait for, the screen has been off for a while, or a long backstop passes. Deliberately NOT
 * stopped when Aptoide Games comes back to the foreground: users switch between both.
 */
class PaEStatusHeartbeatPolicy(
  private val startedAtMillis: Long,
  private val screenOffLimitMillis: Long,
  /** The backstop; may be raised once the remote-config value is known. */
  var maxDurationMillis: Long,
) {

  enum class StopReason { ALL_DONE, NOT_ELIGIBLE, SCREEN_OFF, MAX_DURATION }

  // Written by the screen receiver (main thread), read by the heartbeat loop (IO).
  @Volatile
  private var screenOffSinceMillis: Long? = null

  /** No heartbeats while the screen is off: nobody is playing. */
  val isPaused: Boolean get() = screenOffSinceMillis != null

  fun onScreenOff(nowMillis: Long) {
    if (screenOffSinceMillis == null) screenOffSinceMillis = nowMillis
  }

  fun onScreenOn() {
    screenOffSinceMillis = null
  }

  /**
   * @param missions the game's missions as last loaded, null when unknown (keeps going).
   * @param confirmed mission titles the server confirmed during this run.
   */
  fun stopReason(
    missions: PaEMissions?,
    confirmed: Set<String>,
    nowMillis: Long,
  ): StopReason? {
    if (nowMillis - startedAtMillis >= maxDurationMillis) return StopReason.MAX_DURATION
    screenOffSinceMillis?.let { since ->
      if (nowMillis - since >= screenOffLimitMillis) return StopReason.SCREEN_OFF
    }
    if (missions == null) return null
    if (missions.attribution?.status == PaEAttributionStatus.NOT_ELIGIBLE) {
      return StopReason.NOT_ELIGIBLE
    }
    // Checkpoints are time-based and hidden while usage tracking is off: not waited for.
    val open = missions.missions.filterNot { mission ->
      mission.progress?.status == PaEMissionStatus.COMPLETED || mission.title in confirmed
    }
    return if (open.isEmpty()) StopReason.ALL_DONE else null
  }
}
