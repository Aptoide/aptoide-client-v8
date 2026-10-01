package cm.aptoide.pt.campaigns.domain

import com.google.gson.JsonObject

data class PaEMissions(
  val checkpoints: List<PaEMission>,
  val missions: List<PaEMission>,
  // The serving campaign's MMP click link, called when the game is installed. Only from the
  // network: the cached missions don't keep it.
  val campaignId: String? = null,
  val mmpClickUrl: String? = null,
  // Network only too: null from the cache, which keeps the missions showing.
  val attribution: PaEAttribution? = null,
)

data class PaEMission(
  val title: String,
  val description: String?,
  val icon: String?,
  val type: PaEMissionType,
  val arguments: JsonObject,
  val units: Int,
  val progress: PaEMissionProgress?
)

data class PaEMissionProgress(
  val current: Int?,
  val target: Int,
  val type: PaEMissionProgressType,
  val status: PaEMissionStatus?
) {
  fun getNormalizedProgress(): Float = current?.toFloat()?.div(target)?.coerceIn(0f, 1f) ?: 0f
}

enum class PaEMissionType {
  PLAY_TIME,
  STREAK,
  CHECKPOINT,
  EVENT
}

enum class PaEMissionProgressType {
  GXP,
  SECONDS,
  COUNT
}

enum class PaEMissionStatus {
  PENDING,
  IN_PROGRESS,
  COMPLETED,
}
