package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEMissions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Suppress("unused")
internal class FakePaEMissionsRepository : PaEMissionsRepository {
  override suspend fun getCampaignMissions(
    packageName: String,
    forceRefresh: Boolean
  ): Result<PaEMissions> =
    Result.success(paeMissions)

  override fun observeCampaignMissions(packageName: String): Flow<Result<PaEMissions>> =
    flowOf(Result.success(paeMissions))

  override suspend fun getCachedMissions(packageName: String): PaEMissions? = paeMissions

  override suspend fun markMissionAsCompleted(packageName: String, missionTitle: String) {
  }
}
