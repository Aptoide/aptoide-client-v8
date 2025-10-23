package cm.aptoide.pt.play_and_earn.sessions.data

import cm.aptoide.pt.environment_info.DeviceIdProvider
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionEndInfoJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionEndRequestData
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionEndStatus
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionEventJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionHeartbeatData
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionInfoJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionStartInfoJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionStartRequestData
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionStatus
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionEndInfo
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionEvent
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionInfo
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionStartInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultPaESessionsRepository @Inject constructor(
  private val sessionsApi: SessionsApi,
  private val deviceIdProvider: DeviceIdProvider,
  private val dispatcher: CoroutineDispatcher
) : PaESessionsRepository {

  override suspend fun startSession(packageName: String) = withContext(dispatcher) {
    try {
      val deviceId =
        deviceIdProvider.getDeviceId() ?: throw IllegalStateException("Missing device ID")

      val response = sessionsApi.startSession(
        SessionStartRequestData(
          packageName = packageName,
          deviceId = deviceId
        )
      ).toDomainModel()

      Result.success(response)
    } catch (e: Throwable) {
      e.printStackTrace()
      Result.failure(e)
    }
  }

  override suspend fun heartbeatSession(
    sessionId: String,
    packageName: String,
    sequence: Int,
    seconds: Int
  ) = withContext(dispatcher) {
    try {
      val deviceId =
        deviceIdProvider.getDeviceId() ?: throw IllegalStateException("Missing device ID")

      val deviceTsInMs = System.currentTimeMillis()
      val tzOffsetMin =
        TimeZone.getDefault().getOffset(deviceTsInMs).toLong() / 60_000L

      val request = SessionHeartbeatData(
        sessionId = sessionId,
        packageName = packageName,
        sequence = sequence,
        seconds = seconds,
        deviceId = deviceId,
        deviceTsInMs = deviceTsInMs,
        tzOffset = tzOffsetMin
      )

      val response = sessionsApi.heartbeatSession(request)
      checkSessionStatus(response.status)

      Result.success(response.toDomainModel())
    } catch (e: Throwable) {
      e.printStackTrace()
      Result.failure(e)
    }
  }

  override suspend fun endSession(sessionId: String, packageName: String) =
    withContext(dispatcher) {
      try {
        val request = SessionEndRequestData(
          sessionId = sessionId,
          packageName = packageName,
          deviceId = deviceIdProvider.getDeviceId()
            ?: throw IllegalStateException("Missing device ID")
        )

        val response = sessionsApi.endSession(request)
        checkEndStatus(response.status)

        Result.success(response.toDomainModel())
      } catch (e: Throwable) {
        e.printStackTrace()
        Result.failure(e)
      }
    }
}

private fun checkSessionStatus(status: SessionStatus) {
  when (status) {
    SessionStatus.OK -> Unit
    SessionStatus.DUPLICATE_OR_OUT_OF_ORDER -> throw DuplicateOrOutOfOrderException("Session status is duplicate or out of order")
  }
}

private fun checkEndStatus(status: SessionEndStatus) {
  when (status) {
    SessionEndStatus.ENDED -> Unit
    SessionEndStatus.ALREADY_ENDED -> throw AlreadyEndedException("Session has already ended")
  }
}

private fun SessionStartInfoJson.toDomainModel() = SessionStartInfo(
  sessionId = this.sessionId,
  ttl = this.ttl
)

private fun SessionInfoJson.toDomainModel() = SessionInfo(
  appliedSeconds = this.appliedSeconds,
  ttl = this.ttl,
  events = this.events.map(SessionEventJson::toDomainModel)
)

private fun SessionEventJson.toDomainModel() = SessionEvent(
  type = this.type,
  missionTitle = this.missionTitle,
  packageName = this.packageName
)

private fun SessionEndInfoJson.toDomainModel() = SessionEndInfo(
  appliedSeconds = this.appliedSeconds,
  missionsUpdated = this.missionsUpdated,
  lockCleared = this.lockCleared
)
