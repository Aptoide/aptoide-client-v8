package cm.aptoide.pt.campaigns.domain

import com.google.gson.JsonObject

data class PaEMissions(
  val checkpoints: List<PaEMission>,
  val missions: List<PaEMission>
)

data class PaEMission(
  val title: String,
  val description: String?,
  val icon: String,
  val type: PaEMissionType,
  val arguments: JsonObject,
  val units: Int,
  val progress: PaEMissionProgress?
)

data class PaEMissionProgress(
  val current: Int?,
  val target: Int,
  val type: String,
  val status: PaEMissionStatus?
) {
  fun getNormalizedProgress(): Float = current?.toFloat()?.div(target)?.coerceIn(0f, 1f) ?: 0f
}

enum class PaEMissionType {
  PLAY_TIME,
  STREAK,
  CHECKPOINT
}

enum class PaEMissionStatus {
  PENDING,
  ONGOING,
  COMPLETED,
}
