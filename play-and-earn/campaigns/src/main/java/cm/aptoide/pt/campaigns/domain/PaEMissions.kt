package cm.aptoide.pt.campaigns.domain

import com.google.gson.JsonObject

data class PaEMissions(
  val checkpoints: List<PaEMission>,
  val missions: List<PaEMission>,
  // The serving campaign's MMP click link, called when the game is installed. Only from the
  // network: the cached missions don't keep it.
  val campaignId: String? = null,
  val mmpClickUrl: String? = null,
  // AND-912: which MMP that link belongs to, as the backend says it (AND-916). Null from the
  // cache, an older backend, or a platform this app doesn't know: AppsFlyer behaviour then.
  val mmp: PaEMmpPlatform? = null,
  // Network only too: null from the cache, which keeps the missions showing.
  val attribution: PaEAttribution? = null,
  // AND-915/AND-920: who tracks the campaign, as the backend says it. From the network,
  // remembered for cached reads within the process; null after a process death or from an older
  // backend (see [tracking]).
  val tracking: PaETracking? = null,
)

/**
 * A campaign is either Aptoide-tracked (the app measures play time, usage access needed) or
 * MMP-tracked (the developer's MMP confirms events), never both (AND-915/AND-920). The backend's
 * answer wins; without it, the mission types decide, the same way the backend's backfill did.
 * Null without missions.
 */
fun PaEMissions.tracking(): PaETracking? {
  tracking?.let { return it }
  val all = checkpoints + missions
  return when {
    all.isEmpty() -> null
    all.any { it.isTimeBased() } -> PaETracking.APTOIDE
    else -> PaETracking.MMP
  }
}

/** The measurement tool behind an event campaign's click link (AND-912). */
enum class PaEMmpPlatform {
  APPSFLYER,
  ADJUST;

  companion object {
    /** The backend's value (`APPSFLYER`, `ADJUST`); null for one this app doesn't know. */
    fun fromApi(value: String?): PaEMmpPlatform? =
      entries.find { it.name.equals(value?.trim(), ignoreCase = true) }
  }
}

/** Who tracks a campaign: the Aptoide app, or the developer's MMP (AND-920). */
enum class PaETracking {
  APTOIDE,
  MMP;

  companion object {
    /** The backend's value (`APTOIDE`, `MMP`); null for one this app doesn't know. */
    fun fromApi(value: String?): PaETracking? =
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
