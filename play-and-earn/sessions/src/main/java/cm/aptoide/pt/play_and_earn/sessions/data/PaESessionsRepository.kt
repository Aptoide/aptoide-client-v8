package cm.aptoide.pt.play_and_earn.sessions.data

import cm.aptoide.pt.play_and_earn.sessions.domain.SessionEndInfo
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionInfo
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionStartInfo

/**
 * A request the backend refused (AND-950) fails with a
 * [cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException], as does a heartbeat whose
 * status is `account_restricted`.
 */
interface PaESessionsRepository {

  suspend fun startSession(packageName: String): Result<SessionStartInfo>

  suspend fun heartbeatSession(
    sessionId: String,
    packageName: String,
    sequence: Int,
    seconds: Int
  ): Result<SessionInfo>

  suspend fun endSession(sessionId: String, packageName: String): Result<SessionEndInfo>
}
