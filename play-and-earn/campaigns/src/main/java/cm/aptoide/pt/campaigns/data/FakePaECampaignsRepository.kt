package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEBundles
import cm.aptoide.pt.campaigns.domain.PaEMissions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Suppress("unused")
internal class FakePaECampaignsRepository : PaECampaignsRepository {
  override suspend fun getCampaigns(): Result<PaEBundles> =
    Result.success(paeCampaigns)

  override suspend fun getCampaignMissions(packageName: String): Result<PaEMissions> =
    Result.success(paeMissions)

  override fun observeCampaignMissions(packageName: String): Flow<Result<PaEMissions>> =
    flowOf(Result.success(paeMissions))
}
