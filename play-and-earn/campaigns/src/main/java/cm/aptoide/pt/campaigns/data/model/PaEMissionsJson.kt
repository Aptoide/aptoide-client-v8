package cm.aptoide.pt.campaigns.data.model

import androidx.annotation.Keep
import com.google.gson.JsonObject

@Keep
internal data class PaEMissionsJson(
  val checkpoints: List<PaEMissionJson>,
  val missions: List<PaEMissionJson>
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
  val type: String,
  val status: PaEMissionStatusJson?
)

@Keep
internal enum class PaEMissionTypeJson {
  PLAY_TIME,
  STREAK,
  CHECKPOINT
}

@Keep
internal enum class PaEMissionStatusJson {
  PENDING,
  IN_PROGRESS,
  COMPLETED,
}
