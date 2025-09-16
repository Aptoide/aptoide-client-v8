package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEBundles

internal class FakePaECampaignsRepository : PaECampaignsRepository {
  override suspend fun getCampaigns(): Result<PaEBundles> =
    Result.success(paeCampaigns)
}
