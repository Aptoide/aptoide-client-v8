package cm.aptoide.pt.play_and_earn.sessions.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class SessionInfoJson(
  // Nullable: Gson gives null for a status this app doesn't know.
  val status: SessionStatus?,
  @SerializedName("applied_seconds") val appliedSeconds: Int,
  val ttl: Int,
  val events: List<SessionEventJson>?
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
  DUPLICATE_OR_OUT_OF_ORDER,

  @SerializedName("session_expired")
  SESSION_EXPIRED,

  // Another device of the same user owns this package's session now.
  @SerializedName("paused_by_other_device")
  PAUSED_BY_OTHER_DEVICE,

  @SerializedName("session_not_found")
  SESSION_NOT_FOUND,

  // Older backends: a zero-second heartbeat was dropped. Current ones answer "ok".
  @SerializedName("ignored_zero")
  IGNORED_ZERO
}
