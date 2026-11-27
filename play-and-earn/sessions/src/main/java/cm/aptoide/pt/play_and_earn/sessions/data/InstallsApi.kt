package cm.aptoide.pt.play_and_earn.sessions.data

import cm.aptoide.pt.play_and_earn.sessions.data.model.InstallAnswerJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.InstallClickRequestJson
import cm.aptoide.pt.play_and_earn.sessions.data.model.InstallReportRequestJson
import retrofit2.http.Body
import retrofit2.http.POST

internal interface InstallsApi {

  @POST("/api/v1/installs/click")
  suspend fun click(@Body request: InstallClickRequestJson): InstallAnswerJson

  @POST("/api/v1/installs/report")
  suspend fun report(@Body request: InstallReportRequestJson): InstallAnswerJson
}
