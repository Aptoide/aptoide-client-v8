package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEMissions
import kotlinx.coroutines.flow.Flow

interface PaEMissionsRepository {

  suspend fun getCampaignMissions(
    packageName: String,
    forceRefresh: Boolean = false
  ): Result<PaEMissions>

  fun observeCampaignMissions(packageName: String): Flow<Result<PaEMissions>>

  suspend fun markMissionAsCompleted(packageName: String, missionTitle: String)
}
