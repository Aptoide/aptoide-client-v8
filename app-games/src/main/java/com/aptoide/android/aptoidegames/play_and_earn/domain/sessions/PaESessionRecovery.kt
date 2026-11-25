package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

/**
 * The same P&E game is still in front but has no session: the server dropped it (a heartbeat
 * answered session_expired, session_not_found or paused_by_other_device, e.g. after the phone was
 * locked longer than the server's session lifetime) or creating it failed on the network. Without
 * a new one, play would go unrecorded until the user switched app and back.
 */
fun needsSessionRecreation(
  foregroundPackage: String,
  isPaEGame: Boolean,
  activeSessionPackages: Collection<String>,
): Boolean = isPaEGame && foregroundPackage !in activeSessionPackages
