package cm.aptoide.pt.play_and_earn.sessions.data

import cm.aptoide.pt.play_and_earn.sessions.domain.SessionEndInfo
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionInfo
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionStartInfo

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
