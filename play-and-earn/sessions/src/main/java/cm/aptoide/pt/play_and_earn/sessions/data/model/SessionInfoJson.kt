package cm.aptoide.pt.play_and_earn.sessions.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class SessionInfoJson(
  val status: SessionStatus,
  @SerializedName("applied_seconds") val appliedSeconds: Int,
  val ttl: Int,
  val events: List<SessionEventJson>
)

@Keep
internal data class SessionEventJson(
  val type: String,
  @SerializedName("mission_title") val missionTitle: String,
  @SerializedName("earned_units") val earnedUnits: Int,
  @SerializedName("package") val packageName: String
)

@Keep
internal enum class SessionStatus {
  @SerializedName("ok")
  OK,

  @SerializedName("duplicate_or_out_of_order")
  DUPLICATE_OR_OUT_OF_ORDER
}
