package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.campaigns.domain.PaEBundles

@Suppress("unused")
internal class FakePaECampaignsRepository : PaECampaignsRepository {
  override suspend fun getCampaigns(): Result<PaEBundles> =
    Result.success(paeCampaigns)

  override suspend fun getCampaignMissions(packageName: String): Result<PaEMissions> =
    Result.success(paeMissions)
}
