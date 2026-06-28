package cm.aptoide.pt.campaigns.data.model

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

@Keep
internal data class PaEMissionsJson(
  val checkpoints: List<PaEMissionJson>,
  val missions: List<PaEMissionJson>
)

// Response of GET /api/missions (grouped by mission type). Each section is null when absent.
@Keep
internal data class PaEEventMissionsJson(
  @SerializedName("play_time") val playTime: List<PaEMissionJson>?,
  val streak: List<PaEMissionJson>?,
  val checkpoint: List<PaEMissionJson>?,
  val event: List<PaEMissionJson>?,
)

@Keep
internal data class PaEMissionJson(
  val title: String,
  val description: String?,
  val icon: String?,
  val type: PaEMissionTypeJson,
  val arguments: JsonObject,
  val units: Int,
  val progress: PaEMissionProgressJson?
)

@Keep
internal data class PaEMissionProgressJson(
  val current: Int?,
  val target: Int,
  val type: PaEMissionProgressTypeJson,
  val status: PaEMissionStatusJson?
)

@Keep
internal enum class PaEMissionTypeJson {
  PLAY_TIME,
  STREAK,
  CHECKPOINT,
  EVENT
}

@Keep
internal enum class PaEMissionProgressTypeJson {
  @SerializedName("gxp") GXP,
  @SerializedName("seconds") SECONDS,
  @SerializedName("count") COUNT
}

@Keep
internal enum class PaEMissionStatusJson {
  // Server sends lowercase; "completed" is confirmed, "pending"/"in_progress" pending backend confirmation.
  @SerializedName("pending") PENDING,
  @SerializedName("in_progress") IN_PROGRESS,
  @SerializedName("completed") COMPLETED,
}
