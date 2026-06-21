package cm.aptoide.pt.play_and_earn.events.data.model

import androidx.annotation.Keep

@Keep
internal data class SubmitEventResponseJson(
  val message: String?
)

@Keep
internal data class EventErrorJson(
  val detail: String?
)
