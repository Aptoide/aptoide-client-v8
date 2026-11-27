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

/**
 * Where the tracking poll's usage query starts (AND-964). Without a start the query only looks
 * back a minute, and a game in front longer than that has no event left in it: the poll sees no
 * foreground app and goes idle. A session's open time covers that; a game held in front without
 * a session (its start refused, e.g. for a VPN, or failed) needs the time it came to the front,
 * or the service stops asking for a session after a minute. Null: the default window.
 */
fun foregroundQueryStart(sessionOpenedAtMillis: Long?, inFrontSinceMillis: Long?): Long? =
  sessionOpenedAtMillis ?: inFrontSinceMillis

/**
 * Since when [foregroundPackage], seen in front at [nowMillis], has been in front: kept while
 * the same package stays there, started over for a new package or after the foreground was lost
 * ([previousSinceMillis] null).
 */
fun nextInFrontSince(
  previousPackage: String?,
  foregroundPackage: String,
  previousSinceMillis: Long?,
  nowMillis: Long,
): Long = previousSinceMillis
  ?.takeIf { foregroundPackage == previousPackage }
  ?: nowMillis
