package cm.aptoide.pt.play_and_earn.sessions.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class SessionStartInfoJson(
  @SerializedName("session_id") val sessionId: String,
  val stolen: Boolean?,
  val ttl: Int
)
