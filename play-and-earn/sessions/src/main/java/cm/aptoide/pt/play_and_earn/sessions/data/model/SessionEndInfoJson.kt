package cm.aptoide.pt.play_and_earn.sessions.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class SessionEndInfoJson(
  val status: SessionEndStatus,
  @SerializedName("applied_seconds") val appliedSeconds: Int,
  @SerializedName("missions_updated") val missionsUpdated: Int,
  @SerializedName("lock_cleared") val lockCleared: Boolean?,
)

@Keep
internal enum class SessionEndStatus {
  @SerializedName("already_ended")
  ALREADY_ENDED,
  @SerializedName("ended")
  ENDED
}
