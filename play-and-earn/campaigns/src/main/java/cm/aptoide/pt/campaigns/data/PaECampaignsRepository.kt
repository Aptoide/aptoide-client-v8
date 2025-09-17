package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.campaigns.domain.PaEBundles

interface PaECampaignsRepository {

  suspend fun getCampaigns(): Result<PaEBundles>

  suspend fun getCampaignMissions(packageName: String): Result<PaEMissions>
}
