package cm.aptoide.pt.play_and_earn.events.data

import cm.aptoide.pt.play_and_earn.events.data.model.SubmitEventRequestJson
import cm.aptoide.pt.play_and_earn.events.data.model.SubmitEventResponseJson
import retrofit2.http.Body
import retrofit2.http.POST

internal interface EventsApi {
  @POST("/api/events/")
  suspend fun submitEvent(
    @Body request: SubmitEventRequestJson
  ): SubmitEventResponseJson
}
