package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissions
import kotlinx.coroutines.flow.Flow

interface PaEMissionsRepository {

  suspend fun getCampaignMissions(
    packageName: String,
    forceRefresh: Boolean = false
  ): Result<PaEMissions>

  // Global event missions (e.g. the first sign-in reward), from GET /api/missions?mission_type=EVENT.
  suspend fun getEventMissions(): Result<List<PaEMission>>

  fun observeCampaignMissions(packageName: String): Flow<Result<PaEMissions>>

  suspend fun getCachedMissions(packageName: String): PaEMissions?

  suspend fun markMissionAsCompleted(packageName: String, missionTitle: String)
}
