package cm.aptoide.pt.play_and_earn.sessions.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class SessionHeartbeatData(
  @SerializedName("session_id") val sessionId: String,
  @SerializedName("package") val packageName: String,
  @SerializedName("seq") val sequence: Int,
  val seconds: Int,
  @SerializedName("device_id") val deviceId: String,
  @SerializedName("device_ts_ms") val deviceTsInMs: Long = 0L,
  @SerializedName("tz_offset_min") val tzOffset: Long = 0L,
)
