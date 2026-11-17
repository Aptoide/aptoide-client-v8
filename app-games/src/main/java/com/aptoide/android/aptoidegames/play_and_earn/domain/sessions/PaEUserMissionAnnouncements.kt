package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionProgress
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEUserMission
import cm.aptoide.pt.campaigns.domain.PaEUserProgress
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionEvent
import com.google.gson.JsonObject

/**
 * Which of a heartbeat's events are user missions (Hackathon 2026) not announced yet.
 *
 * A user mission belongs to the player, not to the game being played: the session does not know
 * it, so it cannot be matched against the session's missions like a game's. The server replays
 * its events for a few minutes on every heartbeat of every session, so what was announced is
 * remembered here, across sessions, for longer than that replay. Not for ever: the same mission
 * comes back the next week or month and must be announced again.
 */
class PaEUserMissionAnnouncements(
  private val rememberForMs: Long = REMEMBER_FOR_MS,
) {
  private val announcedAt = mutableMapOf<String, Long>()

  fun newOnes(events: List<SessionEvent>, nowMs: Long): List<SessionEvent> {
    announcedAt.values.removeAll { nowMs - it >= rememberForMs }
    return events
      .filter { it.type == PaEMissionConfirmations.MISSION_COMPLETED && it.isUserMission() }
      .distinctBy { it.missionTitle }
      .filterNot { it.missionTitle in announcedAt }
      .onEach { announcedAt[it.missionTitle] = nowMs }
  }

  companion object {
    // The server replays an event for 5 minutes after the last one was pushed.
    const val REMEMBER_FOR_MS = 15 * 60 * 1000L
  }
}

fun SessionEvent.isUserMission(): Boolean = scope == USER_MISSION_SCOPE

const val USER_MISSION_SCOPE = "user"

/**
 * What to announce for a completed user mission. [progress] is the player's progress read after
 * the event, when it could be loaded: it gives the mission as the player knows it and the one to
 * do next. Without it the event alone is enough to say what was completed and what it paid.
 * [completedTitles] are all the user missions this heartbeat confirmed, which the progress may
 * still show as open.
 */
internal fun completedUserMission(
  event: SessionEvent,
  progress: PaEUserProgress?,
  completedTitles: Collection<String>,
): CompletedMissionEvent {
  val missions = progress?.missions.orEmpty()
  val completed = missions.find { it.title == event.missionTitle }
  return CompletedMissionEvent(
    mission = completed?.toMission(units = event.earnedUnits.takeIf { it > 0 })
      ?: PaEMission(
        title = event.missionTitle,
        description = null,
        icon = null,
        type = PaEMissionType.PLAY_TIME,
        arguments = JsonObject(),
        units = event.earnedUnits,
        progress = null,
      ),
    packageName = event.packageName,
    nextMission = missions
      .filterNot { it.isCompleted || it.title in completedTitles }
      .firstOrNull()
      ?.toMission(),
    isUserMission = true,
  )
}

// The reward the server says it paid wins over the one listed, should they ever differ.
private fun PaEUserMission.toMission(units: Int? = null) = PaEMission(
  title = title,
  description = description,
  icon = icon,
  type = type,
  arguments = JsonObject(),
  units = units ?: this.units,
  progress = PaEMissionProgress(
    current = progress.current,
    target = progress.target,
    type = progress.type,
    status = progress.status,
  ),
)
