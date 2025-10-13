package cm.aptoide.pt.play_and_earn.sessions.data

import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionEndInfoJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionEndRequestData
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionHeartbeatData
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionInfoJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionStartInfoJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.SessionStartRequestData
import retrofit2.http.Body
import retrofit2.http.POST

internal interface SessionsApi {

  @POST("/api/v1/sessions/start")
  suspend fun startSession(
    @Body sessionStartData: SessionStartRequestData
  ): SessionStartInfoJson

  @POST("/api/v1/sessions/heartbeat")
  suspend fun heartbeatSession(
    @Body sessionHeartbeatData: SessionHeartbeatData
  ): SessionInfoJson

  @POST("/api/v1/sessions/end")
  suspend fun endSession(
    @Body sessionEndRequestData: SessionEndRequestData
  ): SessionEndInfoJson
}
