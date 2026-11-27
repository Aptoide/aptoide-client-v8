package cm.aptoide.pt.play_and_earn.sessions.data

import cm.aptoide.pt.play_and_earn.sessions.data.model.AccountStatusJson
import retrofit2.http.GET

internal interface AccountStatusApi {

  @GET("/api/v1/account/status")
  suspend fun getAccountStatus(): AccountStatusJson
}
