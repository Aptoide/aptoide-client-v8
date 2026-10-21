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
  // AND-915: what the campaign is about, as the backend says it. Network only; null from the
  // cache or an older backend (see [campaignType]).
  val type: PaECampaignType? = null,
)

/**
 * A campaign is either play-time (the app measures it, usage access needed) or event (the
 * developer's MMP confirms it), never both (AND-915). The backend's answer wins; without it,
 * the mission types decide, the same way the backend does. Null without missions.
 */
fun PaEMissions.campaignType(): PaECampaignType? {
  type?.let { return it }
  val all = checkpoints + missions
  return when {
    all.isEmpty() -> null
    all.any { it.isTimeBased() } -> PaECampaignType.PLAY_TIME
    else -> PaECampaignType.EVENT
  }
}

/** What a campaign is about: the type of every one of its missions (AND-915). */
enum class PaECampaignType {
  PLAY_TIME,
  EVENT;

  companion object {
    /** The backend's value (`PLAY_TIME`, `EVENT`); null for one this app doesn't know. */
    fun fromApi(value: String?): PaECampaignType? =
      entries.find { it.name.equals(value?.trim(), ignoreCase = true) }
  }
}

data class PaEMission(
  val title: String,
  val description: String?,
  val icon: String?,
  val type: PaEMissionType,
  val arguments: JsonObject,
  val units: Int,
  val progress: PaEMissionProgress?
)

/**
 * Progress comes from the device's own play-time tracking (AND-878): without the usage-access
 * permission these missions can't move, so the UI hides them and the status heartbeat (AND-879)
 * doesn't wait for them.
 */
fun PaEMission.isTimeBased(): Boolean =
  type == PaEMissionType.PLAY_TIME || progress?.type == PaEMissionProgressType.SECONDS

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
