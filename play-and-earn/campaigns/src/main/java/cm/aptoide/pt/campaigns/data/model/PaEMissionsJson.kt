package cm.aptoide.pt.campaigns.data.model

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

@Keep
internal data class PaEMissionsJson(
  val checkpoints: List<PaEMissionJson>,
  val missions: List<PaEMissionJson>,
  @SerializedName("campaign_id") val campaignId: Long?,
  @SerializedName("mmp_click_url") val mmpClickUrl: String?,
  val attribution: PaEAttributionJson?,
  // AND-900: what the campaign needs to be earned. Absent on older backends.
  @SerializedName("requires_usage_access") val requiresUsageAccess: Boolean?,
  @SerializedName("requires_mmp") val requiresMmp: Boolean?,
)

// The user's status for the game's campaign; only sent to signed-in users.
@Keep
internal data class PaEAttributionJson(
  val status: String?,
  val reason: String?,
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
  val progress: PaEMissionProgressJson?,
  // AND-900: `aptoide_usage` | `mmp` | `sdk`. Absent on older backends.
  @SerializedName("evidence_source") val evidenceSource: String?,
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
