package com.aptoide.android.aptoidegames.play_and_earn.domain.sessions

import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.campaigns.domain.isTimeBased

/**
 * The mission to announce as "next" when one completes (Hackathon 2026, T5): the first one still
 * open, in the order the Rewards tab lists them (in progress, then pending, the server's order
 * within each). [completedTitles] are the missions confirmed since the data was fetched, the one
 * just completed included: their status still says in progress. Checkpoints are not missions.
 *
 * [includeTimeBased] is false while play time is not tracked (AND-878): those missions cannot
 * move and the Rewards tab hides them, so they are not promised here either.
 */
internal fun PaEMissions?.nextMission(
  completedTitles: Collection<String>,
  includeTimeBased: Boolean = true,
): PaEMission? = this?.missions.orEmpty()
  .filterNot { it.progress?.status == PaEMissionStatus.COMPLETED }
  .filterNot { it.title in completedTitles }
  .filter { includeTimeBased || !it.isTimeBased() }
  .minByOrNull { if (it.progress?.status == PaEMissionStatus.IN_PROGRESS) 0 else 1 }
