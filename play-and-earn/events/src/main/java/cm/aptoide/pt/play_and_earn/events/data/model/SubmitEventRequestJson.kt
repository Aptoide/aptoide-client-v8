package cm.aptoide.pt.play_and_earn.events.data.model

import androidx.annotation.Keep
import cm.aptoide.pt.play_and_earn.events.domain.EventType
import com.google.gson.annotations.SerializedName

@Keep
internal data class SubmitEventRequestJson(
  @SerializedName("event_type") val eventType: EventType
)
