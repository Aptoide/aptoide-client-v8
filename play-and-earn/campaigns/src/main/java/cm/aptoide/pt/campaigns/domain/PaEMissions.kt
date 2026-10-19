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
  // AND-900: what the campaign needs to be earned, as the backend sees it. Network only; null
  // from the cache or an older backend (see [needsUsageAccess]).
  val requiresUsageAccess: Boolean? = null,
  val requiresMmp: Boolean? = null,
)

/**
 * Whether playing this game's missions needs the usage-access permission (AND-901). The
 * backend's answer wins; without it, any mission this app's usage tracking measures means yes.
 */
fun PaEMissions.needsUsageAccess(): Boolean =
  requiresUsageAccess ?: (checkpoints + missions).any { it.needsUsageAccess() }

data class PaEMission(
  val title: String,
  val description: String?,
  val icon: String?,
  val type: PaEMissionType,
  val arguments: JsonObject,
  val units: Int,
  val progress: PaEMissionProgress?,
  // AND-900: who measures the mission. Network only; null from the cache or an older backend.
  val evidenceSource: PaEEvidenceSource? = null,
)

/**
 * Progress comes from the device's own play-time tracking (AND-878): without the usage-access
 * permission these missions can't move, so the UI hides them and the status heartbeat (AND-879)
 * doesn't wait for them.
 */
fun PaEMission.isTimeBased(): Boolean =
  type == PaEMissionType.PLAY_TIME || progress?.type == PaEMissionProgressType.SECONDS

/**
 * Whether this mission is measured by this app's usage tracking (AND-903): the backend's
 * `evidence_source` (AND-900) decides; without it (cache, older backend), [isTimeBased] does.
 */
fun PaEMission.needsUsageAccess(): Boolean = when (evidenceSource) {
  PaEEvidenceSource.APTOIDE_USAGE -> true
  PaEEvidenceSource.MMP, PaEEvidenceSource.SDK -> false
  null -> isTimeBased()
}

/**
 * Shown locked in the Rewards tab (AND-903): measured by usage tracking, whose permissions
 * (usage access and overlay) the device doesn't grant, and still with something to measure.
 * Tapping the lock opens the permission flow.
 */
fun PaEMission.isLockedWithout(trackingPermissions: Boolean): Boolean =
  needsUsageAccess() && !trackingPermissions && progress?.status != PaEMissionStatus.COMPLETED

/** A row of the Rewards tab: the mission and whether it shows locked (AND-903). */
data class PaERewardRow(val mission: PaEMission, val locked: Boolean)

data class PaERewardRows(val checkpoints: List<PaERewardRow>, val missions: List<PaERewardRow>)

/**
 * What the Rewards tab lists (AND-878/AND-903). With the usage path switched off remotely
 * ([usageTrackingEnabled] false), the missions this app's usage tracking measures are hidden and
 * nothing locks; with it on, every mission is listed and the usage-measured ones lock while the
 * tracking permissions are missing.
 */
fun PaEMissions.rewardRows(
  usageTrackingEnabled: Boolean,
  trackingPermissions: Boolean,
): PaERewardRows {
  fun rows(list: List<PaEMission>) =
    if (usageTrackingEnabled) {
      list.map { PaERewardRow(it, locked = it.isLockedWithout(trackingPermissions)) }
    } else {
      list.filterNot { it.needsUsageAccess() }.map { PaERewardRow(it, locked = false) }
    }
  return PaERewardRows(checkpoints = rows(checkpoints), missions = rows(missions))
}

data class PaEMissionProgress(
  val current: Int?,
  val target: Int,
  val type: PaEMissionProgressType,
  val status: PaEMissionStatus?
) {
  fun getNormalizedProgress(): Float = current?.toFloat()?.div(target)?.coerceIn(0f, 1f) ?: 0f
}

/** Who measures a mission's progress: this app's usage tracking, the developer's MMP, or an SDK. */
enum class PaEEvidenceSource {
  APTOIDE_USAGE,
  MMP,
  SDK;

  companion object {
    /** The backend's value (`aptoide_usage`, ...); null for one this app doesn't know. */
    fun fromApi(value: String?): PaEEvidenceSource? =
      entries.find { it.name.equals(value?.trim(), ignoreCase = true) }
  }
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
